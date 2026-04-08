package com.boaz.user_service.repository;
import com.boaz.user_service.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.Set;
public interface PermissionRepository extends JpaRepository< Permission, String> {
	Optional<Permission> findByName(String name);
	Set<Permission> findByNameIn(Set<String> names);
}