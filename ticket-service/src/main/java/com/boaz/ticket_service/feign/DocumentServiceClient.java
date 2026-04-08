package com.boaz.ticket_service.feign;
import com.boaz.ticket_service.resilience.DocumentServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
@FeignClient(
		name = "document-service",
		fallback = DocumentServiceFallback.class
)
public interface DocumentServiceClient {

// Used to verify a document exists before linking it to a ticket
	@GetMapping("/api/documents/{id}")
	Object getDocumentById(@PathVariable("id") String documentId);
}