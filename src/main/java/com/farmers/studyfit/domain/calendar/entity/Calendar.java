package com.farmers.studyfit.domain.calendar.entity;

import com.farmers.studyfit.domain.connection.entity.Connection;
import com.farmers.studyfit.domain.member.entity.Student;
import com.farmers.studyfit.domain.member.entity.Teacher;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Calendar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "connection_id")
    private Connection connection;
    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;
    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String content;
    @Enumerated(EnumType.STRING)
    private ScheduleType scheduleType;
}

