package com.example.springtest.repository;

import com.example.springtest.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<User, Long> {
    // ID에 해당하는 프로필을 찾아서 반환
    Optional<User> findById(Long id);
}