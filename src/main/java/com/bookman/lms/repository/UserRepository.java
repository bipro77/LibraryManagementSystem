package com.bookman.lms.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookman.lms.entity.User;


public interface UserRepository extends JpaRepository<User, Long>{
	Optional<User> findByUsername(String username);
}
