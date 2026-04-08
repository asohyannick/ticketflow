package com.boaz.document_service.service.minioService;
import com.boaz.document_service.exception.FileStorageException;
import io.minio.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.concurrent.TimeUnit;
@Service
@RequiredArgsConstructor
@Slf4j
public class MinioService {

		private final MinioClient minioClient;
		
		@Value("${minio.bucket-name}")
		private String bucketName;
		
		public void uploadFile(String storedFileName, MultipartFile file) {
			try {
				ensureBucketExists();
				
				minioClient.putObject(
						PutObjectArgs.builder()
								.bucket(bucketName)
								.object(storedFileName)
								.stream(file.getInputStream(), file.getSize(), -1)
								.contentType(file.getContentType())
								.build()
				);
				
				log.info("Uploaded file to MinIO: bucket={} key={}",
						bucketName, storedFileName);
				
			} catch (Exception e) {
				throw new FileStorageException (
						"Failed to upload file to MinIO: " + e.getMessage(), e);
			}
		}
		
		public String generatePresignedUrl(String storedFileName) {
			try {
				String url = minioClient.getPresignedObjectUrl(
						GetPresignedObjectUrlArgs.builder()
								.bucket(bucketName)
								.object(storedFileName)
								.method(Method.GET)
								.expiry(5, TimeUnit.MINUTES)
								.build()
				);
				
				log.info("Generated presigned URL for: {} (valid 5 minutes)", storedFileName);
				return url;
				
			} catch (Exception e) {
				throw new FileStorageException(
						"Failed to generate presigned URL: " + e.getMessage(), e);
			}
		}
		
		public void deleteFile(String storedFileName) {
			try {
				minioClient.removeObject(
						RemoveObjectArgs.builder()
								.bucket(bucketName)
								.object(storedFileName)
								.build()
				);
				log.info("Deleted file from MinIO: {}", storedFileName);
			} catch (Exception e) {
				throw new FileStorageException(
						"Failed to delete file from MinIO: " + e.getMessage(), e);
			}
		}
		
		private void ensureBucketExists() {
			try {
				boolean exists = minioClient.bucketExists(
						BucketExistsArgs.builder()
								.bucket(bucketName)
								.build()
				);
				
				if (!exists) {
					minioClient.makeBucket(
							MakeBucketArgs.builder()
									.bucket(bucketName)
									.build()
					);
					log.info("Created MinIO bucket: {}", bucketName);
				}
			} catch (Exception e) {
				throw new FileStorageException(
						"Failed to verify/create bucket: " + e.getMessage(), e);
			}
		}
}
