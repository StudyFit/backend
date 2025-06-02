package com.farmers.studyfit.domain.homework.service;

import com.farmers.studyfit.domain.connection.entity.Connection;
import com.farmers.studyfit.domain.connection.repository.ConnectionRepository;
import com.farmers.studyfit.domain.homework.dto.AssignHomeworkRequestDto;
import com.farmers.studyfit.domain.homework.entity.Homework;
import com.farmers.studyfit.domain.homework.entity.HomeworkDate;
import com.farmers.studyfit.domain.homework.repository.HomeworkDateRepository;
import com.farmers.studyfit.domain.homework.repository.HomeworkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HomeworkService {
    private final ConnectionRepository connectionRepository;
    private final HomeworkDateRepository homeworkDateRepository;
    private final HomeworkRepository homeworkRepository;

    public void assignHomework(AssignHomeworkRequestDto assignHomeworkRequestDto) {
        Connection connection = connectionRepository.findById(assignHomeworkRequestDto.getConnectionId())
                .orElseThrow(() -> new IllegalArgumentException("연결 정보를 찾을 수 없습니다."));

        // HomeworkDate 존재 여부 확인
        HomeworkDate homeworkDate = homeworkDateRepository.findByConnectionIdAndDate(
                assignHomeworkRequestDto.getConnectionId(), assignHomeworkRequestDto.getDate()
        ).orElseGet(() -> {
            HomeworkDate newDate = HomeworkDate.builder()
                    .connection(connection)
                    .date(assignHomeworkRequestDto.getDate())
                    .build();
            return homeworkDateRepository.save(newDate);
        });

        // 숙제 등록
        Homework homework = Homework.builder()
                .homeworkDate(homeworkDate)
                .content(assignHomeworkRequestDto.getContent())
                .isPhotoRequired(assignHomeworkRequestDto.isPhotoRequired())
                .isCompleted(false)
                .build();

        homeworkRepository.save(homework);
    }
}
