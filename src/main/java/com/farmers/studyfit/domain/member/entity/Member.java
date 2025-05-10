package com.farmers.studyfit.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.time.LocalDate;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
        name = "role",
        discriminatorType = DiscriminatorType.STRING,
        length = 20
)
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
}
