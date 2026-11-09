package com.tickex.ticketingservice.service;

import com.tickex.ticketingservice.exception.ApiException;
import com.tickex.ticketingservice.model.dto.CreateOrderRequest;
import com.tickex.ticketingservice.model.dto.CreateOrderResponse;
import com.tickex.ticketingservice.model.entity.OrderEntity;
import com.tickex.ticketingservice.model.entity.OrderItemEntity;
import com.tickex.ticketingservice.model.entity.TicketTypeEntity;
import com.tickex.ticketingservice.model.enums.OrderStatus;
import com.tickex.ticketingservice.repository.OrderRepository;
import com.tickex.ticketingservice.repository.TicketInventoryRepository;
import com.tickex.ticketingservice.repository.TicketTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final TicketInventoryRepository ticketInventoryRepository;

    @Transactional
    public CreateOrderResponse createOrder(UUID userId, CreateOrderRequest req) {

        if (req.idempotencyKey() != null) {
            Optional<OrderEntity> existing = orderRepository.findByIdempotencyKey(req.idempotencyKey());
            if (existing.isPresent()) {
                return map(existing.get());
            }
        }

        Map<UUID, TicketTypeEntity> ticketTypesById = new HashMap<>();
        List<CreateOrderRequest.Item> requestItems = req.items();

        for (CreateOrderRequest.Item item : requestItems) {
            TicketTypeEntity tt = ticketTypeRepository.findById(item.ticketTypeId())
                    .orElseThrow(() -> ApiException.notFound(
                            "TICKET_TYPE_NOT_FOUND",
                            "Ticket type not found: " + item.ticketTypeId()
                    ));

            if (!tt.getEventSessionId().equals(req.eventSessionId())) {
                throw ApiException.badRequest("SESSION_MISMATCH", "Ticket type does not belong to eventSessionId");
            }

            ticketTypesById.put(tt.getId(), tt);
        }

        String currency = ticketTypesById.values().stream()
                .findFirst()
                .map(TicketTypeEntity::getCurrency)
                .orElseThrow(() -> ApiException.badRequest("EMPTY_ITEMS", "Items cannot be empty"));

        boolean anyDifferent = ticketTypesById.values().stream()
                .anyMatch(t -> !currency.equals(t.getCurrency()));

        if (anyDifferent) {
            throw ApiException.badRequest("CURRENCY_MISMATCH", "All items must use same currency");
        }

        for (CreateOrderRequest.Item item : requestItems) {
            int updated = ticketInventoryRepository.reserveIfAvailable(item.ticketTypeId(), item.quantity());
            if (updated != 1) {
                throw new ApiException("OUT_OF_STOCK", HttpStatus.CONFLICT,
                        "Not enough inventory for " + item.ticketTypeId());
            }
        }

        OrderEntity order = new OrderEntity();
        order.setUserId(userId);
        order.setStatus(OrderStatus.RESERVED);
        order.setCurrency(currency);
        order.setPromoCode(req.promoCode());
        order.setIdempotencyKey(req.idempotencyKey());
        order.setExpiresAt(OffsetDateTime.now().plusMinutes(15));

        BigDecimal sub = BigDecimal.ZERO;
        BigDecimal fee = BigDecimal.ZERO;

        for (CreateOrderRequest.Item itemReq : requestItems) {
            TicketTypeEntity tt = ticketTypesById.get(itemReq.ticketTypeId());

            OrderItemEntity item = new OrderItemEntity();
            item.setOrder(order);
            item.setTicketTypeId(tt.getId());
            item.setEventId(tt.getEventId());
            item.setEventSessionId(tt.getEventSessionId());
            item.setQuantity(itemReq.quantity());
            item.setUnitPrice(tt.getBasePrice());
            item.setUnitFee(tt.getServiceFee());

            BigDecimal line = tt.getBasePrice()
                    .add(tt.getServiceFee())
                    .multiply(BigDecimal.valueOf(itemReq.quantity()));

            item.setLineTotal(line);

            order.getItems().add(item);

            sub = sub.add(tt.getBasePrice().multiply(BigDecimal.valueOf(itemReq.quantity())));
            fee = fee.add(tt.getServiceFee().multiply(BigDecimal.valueOf(itemReq.quantity())));
        }

        order.setSubtotalAmount(sub);
        order.setFeeAmount(fee);
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setTotalAmount(sub.add(fee));

        orderRepository.save(order);
        return map(order);
    }

    private CreateOrderResponse map(OrderEntity o) {
        List<CreateOrderResponse.OrderItem> items = o.getItems().stream()
                .map(i -> new CreateOrderResponse.OrderItem(i.getTicketTypeId(), i.getQuantity(), i.getLineTotal()))
                .toList();

        return new CreateOrderResponse(
                o.getId(),
                o.getStatus().name(),
                o.getCurrency(),
                o.getTotalAmount(),
                o.getExpiresAt(),
                items
        );
    }
}
