package com.tickex.ticketingservice.repository;

import com.tickex.ticketingservice.model.entity.TicketInventoryEntity;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface TicketInventoryRepository extends JpaRepository<TicketInventoryEntity, UUID> {

    Optional<TicketInventoryEntity> findByTicketType_Id(UUID ticketTypeId);

    @Modifying
    @Query("""
        update TicketInventoryEntity i
           set i.reservedQuantity = i.reservedQuantity + :qty,
               i.version = i.version + 1
         where i.ticketType.id = :ticketTypeId
           and (i.totalQuantity - i.soldQuantity - i.reservedQuantity) >= :qty
    """)
    int reserveIfAvailable(@Param("ticketTypeId") UUID ticketTypeId, @Param("qty") int qty);

    @Modifying
    @Query("""
        update TicketInventoryEntity i
           set i.reservedQuantity = i.reservedQuantity - :qty,
               i.soldQuantity = i.soldQuantity + :qty,
               i.version = i.version + 1
         where i.ticketType.id = :ticketTypeId
           and i.reservedQuantity >= :qty
    """)
    int commitReservationToSold(@Param("ticketTypeId") UUID ticketTypeId, @Param("qty") int qty);
}
