package com.farmers.studyfit.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("STUDENT")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@SuperBuilder
public class Student extends Member {
    @Column(nullable=false, length=100)
    private String school;

    @Column(nullable=false)
    private Integer grade;
}
