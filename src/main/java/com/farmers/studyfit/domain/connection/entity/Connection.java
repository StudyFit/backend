package com.farmers.studyfit.domain.connection.entity;

import com.farmers.studyfit.domain.calendar.entity.Calendar;
import com.farmers.studyfit.domain.homework.entity.HomeworkDate;
import com.farmers.studyfit.domain.member.entity.Student;
import com.farmers.studyfit.domain.member.entity.Teacher;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Connection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;
    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;
    private String subject;
    private String teacherColor;
    private String studentColor;
    private String address;
    private String memo;
    @Enumerated(EnumType.STRING)
    private ConnectionState status;
    private Date startDate;
    private Date endDate;

    @OneToMany
    private List<ClassTime> classTimeList;
    @OneToMany
    private List<Calendar> calenderList;
    @OneToMany
    private List<HomeworkDate> homeworkDateList;
}