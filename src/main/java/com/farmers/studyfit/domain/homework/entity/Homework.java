package com.farmers.studyfit.domain.homework.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Homework {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "homework_date_id")
    @JsonIgnore
    private HomeworkDate homeworkDate;
    @Column(nullable = false, length = 1000)
    private String content;
    @Column(nullable = false)
    private boolean isChecked;
    @Column(nullable = false)
    private boolean isPhotoRequired;  // 사진 필수 여부

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }
    public boolean isChecked() {
        return this.isChecked;
    }
}