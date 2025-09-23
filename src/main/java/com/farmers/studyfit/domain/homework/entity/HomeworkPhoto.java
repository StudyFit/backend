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
@Table(name = "HOMEWORK_PHOTO")
public class HomeworkPhoto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "HOMEWORK_PHOTO_ID")
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "HOMEWORK_ID", nullable = false)
    @JsonIgnore
    private Homework homework;
    
    @Column(name = "URL", nullable = false, length = 500)
    private String url;
}