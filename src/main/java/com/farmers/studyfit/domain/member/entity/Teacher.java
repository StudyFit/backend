package com.farmers.studyfit.domain.member.entity;

import com.farmers.studyfit.domain.connection.entity.Connection;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
@SuperBuilder
public class Teacher extends Member {
    @OneToMany(mappedBy = "teacher")
    private List<Connection> connectionList;
}
