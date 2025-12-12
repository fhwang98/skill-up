package com.skillup.backend.domain.user.repository;

import com.skillup.backend.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);

    Optional<UserEntity> findByEmailAndDeleted(String email, boolean deleted);
}
