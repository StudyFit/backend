package com.farmers.studyfit.domain.homework.service;

import com.farmers.studyfit.domain.common.converter.DtoConverter;
import com.farmers.studyfit.domain.common.dto.HomeworkDateResponseDto;
import com.farmers.studyfit.domain.connection.entity.Connection;
import com.farmers.studyfit.domain.connection.repository.ConnectionRepository;
import com.farmers.studyfit.domain.homework.dto.CurrentMonthRateResponse;
import com.farmers.studyfit.domain.homework.dto.PostFeedbackRequestDto;
import com.farmers.studyfit.domain.homework.dto.HomeworkRequestDto;
import com.farmers.studyfit.domain.homework.dto.CheckHomeworkRequestDto;
import com.farmers.studyfit.domain.homework.entity.Homework;
import com.farmers.studyfit.domain.homework.entity.HomeworkDate;
import com.farmers.studyfit.domain.homework.repository.HomeworkDateRepository;
import com.farmers.studyfit.domain.homework.repository.HomeworkRepository;
import com.farmers.studyfit.domain.member.entity.Teacher;
import com.farmers.studyfit.domain.member.service.MemberService;
import com.farmers.studyfit.exception.CustomException;
import com.farmers.studyfit.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
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
    public void postHomework(Long connectionId, HomeworkRequestDto postHomeworkRequestDto) {
        Connection connection = connectionRepository.findById(connectionId)
                .orElseThrow(() -> new CustomException(ErrorCode.CONNECTION_NOT_FOUND));

        HomeworkDate homeworkDate = homeworkDateRepository.findByConnection_IdAndDate(
                connectionId, postHomeworkRequestDto.getDate()
        ).orElseGet(() -> {
            HomeworkDate newDate = HomeworkDate.builder()
                    .connection(connection)
                    .teacher(connection.getTeacher())
                    .student(connection.getStudent())
                    .date(postHomeworkRequestDto.getDate())
                    .build();
            return homeworkDateRepository.save(newDate);
        });

        Homework homework = Homework.builder()
                .homeworkDate(homeworkDate)
                .content(postHomeworkRequestDto.getContent())
                .isChecked(false)
                .isPhotoRequired(false)
                .build();

        homeworkDate.getHomeworkList().add(homework);
        homeworkRepository.save(homework);
    }

    @Transactional
    public void patchHomework(Long homeworkId, HomeworkRequestDto homeworkRequestDto) {
        Homework homework = homeworkRepository.findById(homeworkId)
                .orElseThrow(() -> new CustomException(ErrorCode.HOMEWORK_NOT_FOUND));

        if (homeworkRequestDto.getContent() != null) {
            homework.setContent(homeworkRequestDto.getContent());
        }
    }

    @Transactional
    public void deleteHomework(Long connectionId) {
        Homework homework = homeworkRepository.findById(connectionId).orElseThrow(() -> new CustomException(ErrorCode.HOMEWORK_NOT_FOUND));
        homeworkRepository.delete(homework);
    }

    @Transactional
    public void postFeedback(Long homeworkDateId, PostFeedbackRequestDto postFeedbackRequestDto) {
        HomeworkDate homeworkDate = homeworkDateRepository.findById(homeworkDateId)
                .orElseThrow(() -> new CustomException(ErrorCode.HOMEWORK_DATE_NOT_FOUND));
        homeworkDate.setFeedback(postFeedbackRequestDto.getFeedback());
    }

    @Transactional
    public void deleteFeedback(Long homeworkDateId) {
        HomeworkDate homeworkDate = homeworkDateRepository.findById(homeworkDateId)
                .orElseThrow(() -> new CustomException(ErrorCode.HOMEWORK_DATE_NOT_FOUND));
        if (homeworkDate.getFeedback() == null || homeworkDate.getFeedback().isBlank()) {
            throw new CustomException(ErrorCode.FEEDBACK_NOT_FOUND);
        }
        homeworkDate.setFeedback(null);
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

    @Transactional(readOnly = true)
    public CurrentMonthRateResponse getCurrentMonthRate(Long connectionId) {
        // 1) 소유권/권한
        Teacher teacher = memberService.getCurrentTeacherMember();
        Connection conn = connectionRepository.findById(connectionId)
                .orElseThrow(() -> new CustomException(ErrorCode.CONNECTION_NOT_FOUND));
        if (!conn.getTeacher().getId().equals(teacher.getId())) {
            throw new CustomException(ErrorCode.CONNECTION_NOT_FOUND);
        }

        YearMonth now = YearMonth.now();
        LocalDate startDate = now.atDay(1);
        LocalDate endDate   = now.atEndOfMonth();

        List<Homework> list = homeworkRepository
                .findAllWithDateByConnectionAndRange(connectionId, startDate, endDate);

        long total = list.size();
        long completed = list.stream().filter(Homework::isChecked).count();
        double rate = (total == 0) ? 0.0 : round1(completed * 100.0 / total);
        String status = (total == 0) ? "NO_HOMEWORK" : "HAS_HOMEWORK";

        return new CurrentMonthRateResponse(
                connectionId,
                now.getYear(),
                now.getMonthValue(),
                total,
                completed,
                rate,
                status
        );
    }
    private static double round1(double v) { return Math.round(v * 10.0) / 10.0; }
}