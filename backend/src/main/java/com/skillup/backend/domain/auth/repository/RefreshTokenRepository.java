package com.skillup.backend.domain.auth.repository;

import com.skillup.backend.domain.auth.entity.RefreshTokenEntity;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshTokenEntity, String> { }