package com.boaz.document_service.kafka;
import com.boaz.document_service.dto.DocumentEventPayload;
import com.boaz.document_service.entity.Document;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
@Component
@RequiredArgsConstructor
@Slf4j
public class DocumentEventProducer {

	private final KafkaTemplate<String, Object> kafkaTemplate;
	
	@Value("${kafka.producer.topic.document-uploaded:document.uploaded}")
	private String documentUploadedTopic;
	
	public void publishDocumentUploaded( Document document) {
		DocumentEventPayload payload = new DocumentEventPayload(
				"DOCUMENT_UPLOADED",
				document.getId(),
				document.getOriginalFileName(),
				document.getContentType(),
				document.getFileSize(),
				document.getTicketId(),
				document.getUploadedByUserId(),
				LocalDateTime.now()
		);
		
		kafkaTemplate.send(documentUploadedTopic, document.getId(), payload);
		log.info("Published DOCUMENT_UPLOADED event for document: {} (ticketId={})",
				document.getId(), document.getTicketId());
	}
}