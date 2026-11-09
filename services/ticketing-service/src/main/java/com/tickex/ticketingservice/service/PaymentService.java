package com.tickex.ticketingservice.service;

import com.tickex.ticketingservice.exception.ApiException;
import com.tickex.ticketingservice.model.entity.OrderEntity;
import com.tickex.ticketingservice.model.entity.OrderItemEntity;
import com.tickex.ticketingservice.model.entity.PaymentEntity;
import com.tickex.ticketingservice.model.entity.TicketEntity;
import com.tickex.ticketingservice.model.enums.OrderStatus;
import com.tickex.ticketingservice.model.enums.PaymentStatus;
import com.tickex.ticketingservice.repository.OrderRepository;
import com.tickex.ticketingservice.repository.PaymentRepository;
import com.tickex.ticketingservice.repository.TicketInventoryRepository;
import com.tickex.ticketingservice.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.time.OffsetDateTime;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final TicketRepository ticketRepository;
    private final TicketInventoryRepository ticketInventoryRepository;

    @Transactional
    public void confirmPayment(UUID userId, UUID orderId, String provider, String providerPaymentId, String rawJson) {

        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> ApiException.notFound("ORDER_NOT_FOUND", "Order not found"));

        if (!order.getUserId().equals(userId)) {
            throw new ApiException("FORBIDDEN", HttpStatus.FORBIDDEN, "Order does not belong to user");
        }

        if (order.getStatus() == OrderStatus.PAID) return;

        if (order.getExpiresAt() != null && order.getExpiresAt().isBefore(OffsetDateTime.now())) {
            throw new ApiException("ORDER_EXPIRED", HttpStatus.CONFLICT, "Order expired");
        }

        PaymentEntity payment = new PaymentEntity();
        payment.setOrder(order);
        payment.setProvider(provider);
        payment.setProviderPaymentId(providerPaymentId);
        payment.setStatus(PaymentStatus.CAPTURED); // demo
        payment.setAmount(order.getTotalAmount());
        payment.setCurrency(order.getCurrency());
        payment.setRawPayloadJson(rawJson == null ? "{}" : rawJson);
        paymentRepository.save(payment);

        List<OrderItemEntity> items = order.getItems();

        for (OrderItemEntity it : items) {
            int ok = ticketInventoryRepository.commitReservationToSold(it.getTicketTypeId(), it.getQuantity());
            if (ok != 1) {
                throw new ApiException("INVENTORY_COMMIT_FAILED", HttpStatus.CONFLICT, "Inventory commit failed");
            }
        }

        for (OrderItemEntity it : items) {
            for (int i = 0; i < it.getQuantity(); i++) {

                TicketEntity t = new TicketEntity();
                t.setOrder(order);
                t.setOrderItem(it);
                t.setOwnerUserId(order.getUserId());
                t.setEventId(it.getEventId());
                t.setEventSessionId(it.getEventSessionId());
                t.setTicketTypeId(it.getTicketTypeId());

                String serial = order.getId() + "-" + it.getId() + "-" + i;
                t.setSerialNo(serial);
                t.setQrTokenHash(sha256Hex(serial));

                ticketRepository.save(t);
            }
        }

        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);
    }

    private static String sha256Hex(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(s.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
