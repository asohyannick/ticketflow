package com.boaz.user_service.service.permission;
import com.boaz.user_service.dto.PermissionResponse;
import com.boaz.user_service.entity.Permission;
import com.boaz.user_service.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
@Slf4j
public class PermissionService {

	private final PermissionRepository permissionRepository;
	
	public List< PermissionResponse > getAllPermissions() {
		return permissionRepository.findAll()
				       .stream()
				       .map(this::toResponse)
				       .collect(Collectors.toList());
	}
	
	public Permission getOrCreate(String name, String description) {
		return permissionRepository.findByName(name)
				       .orElseGet(() -> permissionRepository.save(
						       Permission.builder().name(name).description(description).build()
				       ));
	}
	
	private PermissionResponse toResponse(Permission p) {
		return new PermissionResponse(
				p.getId (),
				p.getName (),
				p.getDescription ()
		);
	}
}