package com.farmers.studyfit.domain.homework.entity;

import com.farmers.studyfit.domain.connection.entity.Connection;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HomeworkDate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "connection_id")
    private Connection connection;
    private LocalDate date;

    @OneToOne
    private Feedback feedback;
    @OneToMany
    private List<Homework> homeworkList;
}
