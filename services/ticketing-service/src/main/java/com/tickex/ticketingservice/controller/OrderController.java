package com.tickex.ticketingservice.controller;

import com.tickex.ticketingservice.model.dto.ConfirmPaymentRequest;
import com.tickex.ticketingservice.model.dto.CreateOrderRequest;
import com.tickex.ticketingservice.model.response.ApiResponse;
import com.tickex.ticketingservice.model.dto.CreateOrderResponse;
import com.tickex.ticketingservice.service.OrderService;
import com.tickex.ticketingservice.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ticketing/orders")
public class OrderController {
//Test
    private final OrderService orderService;
    private final PaymentService paymentService;

    @PostMapping
    public ApiResponse<CreateOrderResponse> create(
            @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody CreateOrderRequest request
    ) {
        return ApiResponse.ok("ORDER_CREATED", "Order reserved", orderService.createOrder(userId, request));
    }

    @PostMapping("/{orderId}/confirm-payment")
    public ApiResponse<Void> confirmPayment(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID orderId,
            @Valid @RequestBody ConfirmPaymentRequest req
    ) {
        paymentService.confirmPayment(userId, orderId, req.provider(), req.providerPaymentId(), req.rawPayloadJson());
        return ApiResponse.ok("PAYMENT_CONFIRMED", "Payment confirmed & tickets issued", null);
    }
}
