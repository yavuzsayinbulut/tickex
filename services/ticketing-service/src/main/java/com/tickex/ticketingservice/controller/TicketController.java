package com.tickex.ticketingservice.controller;

import com.tickex.ticketingservice.model.dto.CheckInRequest;
import com.tickex.ticketingservice.model.response.ApiResponse;
import com.tickex.ticketingservice.service.CheckInService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ticketing/tickets")
public class TicketController {

    private final CheckInService checkInService;

    @PostMapping("/{ticketId}/check-in")
    public ApiResponse<Void> checkIn(
            @RequestHeader("X-User-Id") UUID operatorUserId,
            @PathVariable UUID ticketId,
            @Valid @RequestBody CheckInRequest req
    ) {
        checkInService.checkIn(operatorUserId, ticketId, req.gate(), req.deviceId());
        return ApiResponse.ok("CHECKIN_OK", "Ticket checked-in", null);
    }
}
