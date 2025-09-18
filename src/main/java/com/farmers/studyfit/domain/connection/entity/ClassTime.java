package com.farmers.studyfit.domain.connection.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "connection_id")
    private Connection connection;
    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week")
    private DayOfWeek day;
    private LocalTime startTime;
    private LocalTime endTime;


}
