package com.farmers.studyfit.domain.member.entity;

import com.farmers.studyfit.domain.connection.entity.Connection;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@SuperBuilder
public class Student extends Member {
    @Column
    private String school;

    @Column
    private Integer grade;

    @OneToMany
    private List<Connection> connectionList;
}
