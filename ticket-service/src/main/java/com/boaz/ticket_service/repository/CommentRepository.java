package com.boaz.ticket_service.repository;
import com.boaz.ticket_service.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface CommentRepository extends JpaRepository< Comment, String> {
	List<Comment> findByTicketIdOrderByCreatedAtAsc(String ticketId);
}