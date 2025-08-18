package com.farmers.studyfit.domain.homework.service;

import com.farmers.studyfit.domain.common.converter.DtoConverter;
import com.farmers.studyfit.domain.common.dto.HomeworkDateResponseDto;
import com.farmers.studyfit.domain.connection.entity.Connection;
import com.farmers.studyfit.domain.connection.repository.ConnectionRepository;
import com.farmers.studyfit.domain.homework.dto.AssignFeedbackRequestDto;
import com.farmers.studyfit.domain.homework.dto.AssignHomeworkRequestDto;
import com.farmers.studyfit.domain.homework.dto.CheckHomeworkRequestDto;
import com.farmers.studyfit.domain.homework.entity.Homework;
import com.farmers.studyfit.domain.homework.entity.HomeworkDate;
import com.farmers.studyfit.domain.homework.repository.HomeworkDateRepository;
import com.farmers.studyfit.domain.homework.repository.HomeworkRepository;
import com.farmers.studyfit.domain.member.entity.Student;
import com.farmers.studyfit.domain.member.entity.Teacher;
import com.farmers.studyfit.domain.member.service.MemberService;
import com.farmers.studyfit.exception.CustomException;
import com.farmers.studyfit.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeworkService {
    private final ConnectionRepository connectionRepository;
    private final HomeworkDateRepository homeworkDateRepository;
    private final HomeworkRepository homeworkRepository;
    private final MemberService memberService;
    private final DtoConverter dtoConverter;

    @Transactional
    public void assignHomework(Long connectionId, AssignHomeworkRequestDto assignHomeworkRequestDto) {
        Connection connection = connectionRepository.findById(connectionId)
                .orElseThrow(() -> new CustomException(ErrorCode.CONNECTION_NOT_FOUND));

        HomeworkDate homeworkDate = homeworkDateRepository.findByConnectionIdAndDate(
                connectionId, assignHomeworkRequestDto.getDate()
        ).orElseGet(() -> {
            HomeworkDate newDate = HomeworkDate.builder()
                    .connection(connection)
                    .teacher(connection.getTeacher())
                    .student(connection.getStudent())
                    .date(assignHomeworkRequestDto.getDate())
                    .build();
            return homeworkDateRepository.save(newDate);
        });

        Homework homework = Homework.builder()
                .homeworkDate(homeworkDate)
                .content(assignHomeworkRequestDto.getContent())
                .isChecked(false)
                .build();

        homeworkRepository.save(homework);
    }

    @Transactional
    public void assignFeedback(Long homeworkDateId, AssignFeedbackRequestDto assignFeedbackRequestDto) {
        HomeworkDate homeworkDate = homeworkDateRepository.findById(homeworkDateId)
                .orElseThrow(() -> new CustomException(ErrorCode.HOMEWORK_DATE_NOT_FOUND));
        homeworkDate.setFeedback(assignFeedbackRequestDto.getFeedback());
    }

    @Transactional
    public void checkHomework(Long homeworkId, CheckHomeworkRequestDto checkHomeworkRequestDto) {
        Homework homework = homeworkRepository.findById(homeworkId)
                .orElseThrow(() -> new CustomException(ErrorCode.HOMEWORK_NOT_FOUND));
        homework.setChecked(checkHomeworkRequestDto.isChecked());
        homeworkRepository.save(homework);
    }

    @Transactional
    public List<HomeworkDateResponseDto> getHomeworkListByStudent(Long studentId) {
        List<HomeworkDate> homeworkDates = homeworkDateRepository.findByConnectionId(studentId);
        return homeworkDates.stream()
                .sorted(Comparator.comparing(HomeworkDate::getDate).thenComparing(HomeworkDate::getId))
                .map(dtoConverter::toHomeworkDateResponse)
                .toList();
    }

    @Transactional
    public List<HomeworkDateResponseDto> getHomeworkListByDate(Long homeworkDateId) {
        Teacher teacher = memberService.getCurrentTeacherMember();
        HomeworkDate homeworkDate = homeworkDateRepository.findById(homeworkDateId)
                .orElseThrow(() -> new CustomException(ErrorCode.HOMEWORK_DATE_NOT_FOUND));

        LocalDate date = homeworkDate.getDate();
        List<HomeworkDate> homeworkDates = homeworkDateRepository.findByDateAndTeacherId(date, teacher.getId());
        return homeworkDates.stream()
                .map(dtoConverter::toHomeworkDateResponse)
                .toList();
    }
}
