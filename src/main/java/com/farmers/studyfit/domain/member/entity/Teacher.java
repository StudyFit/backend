package com.farmers.studyfit.domain.member.entity;

import com.farmers.studyfit.domain.calendar.entity.Calendar;
import com.farmers.studyfit.domain.common.entity.MemberRole;
import com.farmers.studyfit.domain.connection.entity.Connection;
import com.farmers.studyfit.domain.homework.entity.HomeworkDate;
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
    @OneToMany(mappedBy = "teacher")
    private List<Calendar> calendarList;
    @OneToMany(mappedBy = "teacher")
    private List<HomeworkDate> homeworkDateList;

    @Override
    public MemberRole getRole() { return MemberRole.STUDENT; }

}
