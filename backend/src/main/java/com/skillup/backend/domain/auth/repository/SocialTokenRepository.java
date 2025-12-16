package com.skillup.backend.domain.auth.repository;

import com.skillup.backend.domain.auth.entity.SocialTokenEntity;
import org.springframework.data.repository.CrudRepository;

public interface SocialTokenRepository extends CrudRepository<SocialTokenEntity, String> { }