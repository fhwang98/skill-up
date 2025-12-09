package com.skillup.backend.domain.user.repository;

import com.skillup.backend.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);

}
