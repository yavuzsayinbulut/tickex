package com.tickex.ticketingservice.service;

import com.tickex.ticketingservice.exception.ApiException;
import com.tickex.ticketingservice.model.entity.TicketCheckInEntity;
import com.tickex.ticketingservice.model.entity.TicketEntity;
import com.tickex.ticketingservice.model.enums.TicketStatus;
import com.tickex.ticketingservice.repository.TicketCheckInRepository;
import com.tickex.ticketingservice.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CheckInService {

    private final TicketRepository ticketRepository;
    private final TicketCheckInRepository checkInRepository;

    @Transactional
    public void checkIn(UUID operatorUserId, UUID ticketId, String gate, String deviceId) {

        TicketEntity ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> ApiException.notFound("TICKET_NOT_FOUND", "Ticket not found"));

        if (ticket.getStatus() != TicketStatus.ISSUED) {
            throw new ApiException("TICKET_NOT_USABLE", HttpStatus.CONFLICT,
                    "Ticket status is " + ticket.getStatus());
        }

        TicketCheckInEntity ci = new TicketCheckInEntity();
        ci.setTicket(ticket);
        ci.setOperatorUserId(operatorUserId);
        ci.setGate(gate);
        ci.setDeviceId(deviceId);
        ci.setStatus("SUCCESS");
        ci.setCheckedInAt(OffsetDateTime.now());

        checkInRepository.save(ci);

        ticket.setStatus(TicketStatus.USED);
        ticket.setUsedAt(OffsetDateTime.now());
        ticketRepository.save(ticket);
    }
}
