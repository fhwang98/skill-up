package com.skillup.backend.domain.user.entity;

import com.skillup.backend.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tbl_user")
public class UserEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRoleType role; // ROLE_USER, ROLE_ADMIN

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SocialProviderType provider; // LOCAL, NAVER, KAKAO

    private String providerId; // 로컬일때 NULL

    @Builder.Default
    private boolean deleted = false;

    public void updateSocialUser(String email, String nickname, String providerId) {
        this.email = email;
        this.nickname = nickname;
        this.providerId = providerId;
    }

    public void updateUser(String nickname) {
        this.nickname = nickname;
    }

    public void updatePassword(String password) {
        this.password = password;
    }
}
