package com.farmers.studyfit.domain.member.entity;

import com.farmers.studyfit.domain.common.entity.MemberRole;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.time.LocalDate;

@MappedSuperclass
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@SuperBuilder
public abstract class Member {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique=true, nullable=false, length=30)
    private String loginId;

    @Column(nullable=false)
    private String passwordHash;

    @Column(nullable=false, length=50)
    private String name;

    @Column(nullable=false)
    private LocalDate birth;

    @Column(nullable=false, length=20)
    private String phoneNumber;

    @Column
    private String profileImg;

    public abstract MemberRole getRole();
}
