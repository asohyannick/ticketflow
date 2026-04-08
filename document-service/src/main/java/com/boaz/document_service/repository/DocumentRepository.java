package com.boaz.document_service.repository;
import com.boaz.document_service.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface DocumentRepository extends JpaRepository< Document, String> {
	List<Document> findByTicketId(String ticketId);
	List<Document> findByUploadedByUserId(String userId);
}