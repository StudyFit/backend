package com.farmers.studyfit.domain.member.entity;

import com.farmers.studyfit.domain.calendar.entity.Calendar;
import com.farmers.studyfit.domain.connection.entity.Connection;
import com.farmers.studyfit.domain.homework.entity.HomeworkDate;
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

    @OneToMany(mappedBy = "student")
    private List<Connection> connectionList;
    @OneToMany(mappedBy = "student")
    private List<Calendar> calendarList;
    @OneToMany(mappedBy = "student")
    private List<HomeworkDate> homeworkDateList;

}
