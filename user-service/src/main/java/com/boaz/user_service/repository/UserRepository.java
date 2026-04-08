package com.boaz.user_service.repository;
import com.boaz.user_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
public interface UserRepository extends JpaRepository< User, String> {
	boolean existsByEmail(String email);
}