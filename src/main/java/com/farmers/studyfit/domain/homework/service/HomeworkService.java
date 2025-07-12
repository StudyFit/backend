package com.farmers.studyfit.domain.homework.service;

import com.farmers.studyfit.domain.connection.entity.Connection;
import com.farmers.studyfit.domain.connection.repository.ConnectionRepository;
import com.farmers.studyfit.domain.connection.service.ConnectionService;
import com.farmers.studyfit.domain.homework.dto.AssignFeedbackRequestDto;
import com.farmers.studyfit.domain.homework.dto.AssignHomeworkRequestDto;
import com.farmers.studyfit.domain.homework.dto.SubmitHomeworkRequestDto;
import com.farmers.studyfit.domain.homework.entity.Feedback;
import com.farmers.studyfit.domain.homework.entity.Homework;
import com.farmers.studyfit.domain.homework.entity.HomeworkDate;
import com.farmers.studyfit.domain.homework.repository.FeedbackRepository;
import com.farmers.studyfit.domain.homework.repository.HomeworkDateRepository;
import com.farmers.studyfit.domain.homework.repository.HomeworkRepository;
import com.farmers.studyfit.domain.member.entity.Student;
import com.farmers.studyfit.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeworkService {
    private final ConnectionRepository connectionRepository;
    private final HomeworkDateRepository homeworkDateRepository;
    private final HomeworkRepository homeworkRepository;
    private final FeedbackRepository feedbackRepository;
    private final ConnectionService connectionService;
    private final MemberService memberService;


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

    //숙제 allCompleted 수정 필요
    @Transactional
    public void assignFeedback(AssignFeedbackRequestDto assignFeedbackRequestDto) {
        HomeworkDate homeworkDate = homeworkDateRepository.findById(assignFeedbackRequestDto.getHomeworkDateId())
                .orElseThrow(() -> new IllegalArgumentException("숙제 날짜 정보를 찾을 수 없습니다."));

        // 2. 숙제들이 모두 완료되었는지 확인
        List<Homework> homeworkList = homeworkDate.getHomeworkList();
        boolean allCompleted = true;
        for (Homework homework : homeworkList) {
            if (!homework.isCompleted()) {
                allCompleted = false;
                break;
            }
        }
        if (!allCompleted) {
            throw new IllegalStateException("아직 완료되지 않은 숙제가 있어 피드백을 작성할 수 없습니다.");
        }

        // 3. 피드백 생성 및 연관관계 설정
        Feedback feedback = Feedback.builder()
                .content(assignFeedbackRequestDto.getFeedback())
                .homeworkDate(homeworkDate)
                .build();

        feedbackRepository.save(feedback);
    }

    //숙제 제출 시 isCompleted = true
    @Transactional
    public void submitHomework(SubmitHomeworkRequestDto submitHomeworkRequestDto) {
        Homework homework = homeworkRepository.findById(submitHomeworkRequestDto.getHomeworkId())
                .orElseThrow(() -> new IllegalArgumentException("해당 숙제를 찾을 수 없습니다."));

        homework.setCompleted(submitHomeworkRequestDto.isCompleted());
        homeworkRepository.save(homework);
    }

    public List<HomeworkDto> getHomeworkListsByStudent(AssignHomeworkRequestDto assignHomeworkRequestDto) {
        Student student = memberService.getCurrentStudentMember();
        List<Connection> connections = connectionRepository.findByStudentId(student.getId());
        List<HomeworkDto> getHomeworkListsByStudent();

    }
}
