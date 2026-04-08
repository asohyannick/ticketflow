package com.boaz.ticket_service.resilience;
import com.boaz.ticket_service.feign.DocumentServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.Map;
@Component
@Slf4j
public class DocumentServiceFallback implements DocumentServiceClient {

@Override
public Object getDocumentById(String documentId) {
	log.warn("FALLBACK: document-service unavailable for documentId={}", documentId);
	return Map.of(
			"id", documentId,
			"status", "unavailable",
			"message", "Document service is currently unavailable"
	);
}
}