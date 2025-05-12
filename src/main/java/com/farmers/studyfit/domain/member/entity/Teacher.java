package com.farmers.studyfit.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter @Setter
@NoArgsConstructor
@SuperBuilder
public class Teacher extends Member {
    // 추가 필드 없음
}
