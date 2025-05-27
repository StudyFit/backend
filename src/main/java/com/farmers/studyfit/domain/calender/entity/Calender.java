package com.farmers.studyfit.domain.calender.entity;

import com.farmers.studyfit.domain.connection.entity.Connection;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Calender {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "connection_id")
    private Connection connection;
    private LocalTime startTime;
    private LocalTime endTime;
    private String content;
    private String memo;
    private ScheduleType scheduleType;
}

