package com.sesac.aibackend.domain;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name="users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 로그인 아이디 (유일)
     */
    @Column(
            unique = true,
            nullable = false,
            length = 100
    )
    private String username;

    /**
     * BCrypt 해시 (Day4에서 사용)
     */
    @Column(length = 200)
    private String passwordHash;

    /**
     * USER / ADMIN
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    /**
     * ChatLog → User 방향만 사용하는 단방향 연관관계.
     *
     * 현재는 User에서 ChatLog 목록을 조회할 요구사항이 없어
     * 불필요한 양방향 매핑을 추가하지 않습니다.
     *
     * 향후 "특정 사용자의 모든 대화 내역 조회" 기능이 필요해지면
     * 아래 @OneToMany 매핑을 활성화할 수 있습니다.
     *
     * 참고:
     * - FK(외래키) 주인은 ChatLog의 @ManyToOne(user) 측입니다.
     * - User 측은 조회 편의를 위한 역방향 매핑입니다.
     */
//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<ChatLog> chatLogs = new ArrayList<>();
}
