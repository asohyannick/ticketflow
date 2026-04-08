package com.boaz.ticket_service.repository;
import com.boaz.ticket_service.constant.TicketStatus;
import com.boaz.ticket_service.entity.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
public interface TicketRepository extends JpaRepository< Ticket, String> {
Page<Ticket> findByStatus( TicketStatus status, Pageable pageable);
Page<Ticket> findByCreatedByUserId(String userId, Pageable pageable);
Page<Ticket> findByAssigneeId(String assigneeId, Pageable pageable);
}