package com.boaz.user_service.repository;
import com.boaz.user_service.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.Set;
public interface RoleRepository extends JpaRepository<Role, String> {
	Optional< Role > findByName( String name);
	Set<Role> findByNameIn(Set<String> names);
	boolean existsByName(String name);
}