package com.farmers.studyfit.domain.homework.service;

import com.farmers.studyfit.domain.common.converter.DateConverter;
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
import com.farmers.studyfit.domain.homework.entity.HomeworkPhoto;
import com.farmers.studyfit.domain.homework.repository.HomeworkDateRepository;
import com.farmers.studyfit.domain.homework.repository.HomeworkRepository;
import com.farmers.studyfit.domain.homework.repository.HomeworkPhotoRepository;
import com.farmers.studyfit.domain.member.entity.Student;
import com.farmers.studyfit.domain.member.entity.Teacher;
import com.farmers.studyfit.domain.member.repository.StudentRepository;
import com.farmers.studyfit.domain.member.repository.TeacherRepository;
import com.farmers.studyfit.domain.member.service.MemberService;
import com.farmers.studyfit.domain.S3Service;
import com.farmers.studyfit.domain.notification.service.NotificationService;
import com.farmers.studyfit.exception.CustomException;
import com.farmers.studyfit.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final HomeworkPhotoRepository homeworkPhotoRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final MemberService memberService;
    private final DtoConverter dtoConverter;
    private final S3Service s3Service;
    private final DateConverter dateConverter;
    private final NotificationService notificationService;

    @Transactional
    public void postHomework(Long connectionId, HomeworkRequestDto homeworkRequestDto) {
        Connection connection = connectionRepository.findById(connectionId)
                .orElseThrow(() -> new CustomException(ErrorCode.CONNECTION_NOT_FOUND));

        HomeworkDate homeworkDate = homeworkDateRepository.findByConnection_IdAndDate(
                connectionId, homeworkRequestDto.getDate()
        ).orElseGet(() -> {
            HomeworkDate newDate = HomeworkDate.builder()
                    .connection(connection)
                    .teacher(connection.getTeacher())
                    .student(connection.getStudent())
                    .date(homeworkRequestDto.getDate())
                    .build();
            return homeworkDateRepository.save(newDate);
        });
        
        Homework homework = Homework.builder()
                .homeworkDate(homeworkDate)
                .content(homeworkRequestDto.getContent())
                .isChecked(false)
                .isPhotoRequired(homeworkRequestDto.isPhotoRequired())
                .build();
        homeworkRepository.save(homework);

        String content = dateConverter.convertDate(homeworkDate.getDate().toString()) + " 숙제가 등록되었습니다.";
        notificationService.sendNotification(connection.getTeacher(), connection.getStudent(), content);
    }

    @Transactional
    public void patchHomework(Long homeworkId, HomeworkRequestDto homeworkRequestDto) {
        Homework homework = homeworkRepository.findById(homeworkId)
                .orElseThrow(() -> new CustomException(ErrorCode.HOMEWORK_NOT_FOUND));

        if (homeworkRequestDto.getContent() != null) {
            homework.setContent(homeworkRequestDto.getContent());
        }
        homework.setPhotoRequired(homeworkRequestDto.isPhotoRequired());
    }

    @Transactional
    public void deleteHomework(Long homeworkDateId) {
        HomeworkDate homeworkDate = homeworkDateRepository.findById(homeworkDateId)
                .orElseThrow(() -> new CustomException(ErrorCode.HOMEWORK_DATE_NOT_FOUND));
        homeworkDateRepository.delete(homeworkDate);
    }

    @Transactional
    public void postFeedback(Long homeworkDateId, PostFeedbackRequestDto postFeedbackRequestDto) {
        HomeworkDate homeworkDate = homeworkDateRepository.findById(homeworkDateId)
                .orElseThrow(() -> new CustomException(ErrorCode.HOMEWORK_DATE_NOT_FOUND));
        homeworkDate.setFeedback(postFeedbackRequestDto.getFeedback());

        Connection connection = homeworkDate.getConnection();
        String content = dateConverter.convertDate(homeworkDate.getDate() + " 숙제 피드백이 달렸습니다.";
        notificationService.sendNotification(connection.getTeacher(), connection.getStudent(), content);
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
    public String checkHomework(Long homeworkId, CheckHomeworkRequestDto checkHomeworkRequestDto) {
        Homework homework = homeworkRepository.findById(homeworkId)
                .orElseThrow(() -> new CustomException(ErrorCode.HOMEWORK_NOT_FOUND));
        
        String uploadedImageUrl = null;
        
        if (checkHomeworkRequestDto.isChecked()) {
            if (homework.isPhotoRequired()) {
                if (checkHomeworkRequestDto.getPhoto() == null || checkHomeworkRequestDto.getPhoto().isEmpty()) {
                    throw new CustomException(ErrorCode.PHOTO_REQUIRED_FOR_HOMEWORK);
                }
                
                try {
                    String fileName = s3Service.uploadFile(checkHomeworkRequestDto.getPhoto());
                    uploadedImageUrl = s3Service.getFileUrl(fileName);
                    
                    HomeworkPhoto homeworkPhoto = HomeworkPhoto.builder()
                            .homework(homework)
                            .url(fileName)
                            .build();
                    homeworkPhotoRepository.save(homeworkPhoto);
                    
                } catch (Exception e) {
                    throw new CustomException(ErrorCode.CANNOT_UPLOAD_IMG);
                }
            }
        }
        
        homework.setChecked(checkHomeworkRequestDto.isChecked());
        homeworkRepository.save(homework);

        Connection connection = homework.getHomeworkDate().getConnection();
        String content = dateConverter.convertDate(homework.getHomeworkDate().getDate().toString()) + " 숙제가 완료되었습니다.";
        notificationService.sendNotification(connection.getStudent(), connection.getTeacher(), content);

        return uploadedImageUrl;
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
        // 권한 검증: 해당 연결의 선생님 또는 학생만 조회 가능
        String loginId = SecurityContextHolder.getContext().getAuthentication().getName();
        Connection conn = connectionRepository.findById(connectionId)
                .orElseThrow(() -> new CustomException(ErrorCode.CONNECTION_NOT_FOUND));
        
        boolean isAuthorized = false;
        
        // 선생님인 경우
        if (teacherRepository.findByLoginId(loginId).isPresent()) {
            Teacher teacher = memberService.getCurrentTeacherMember();
            if (conn.getTeacher().getId().equals(teacher.getId())) {
                isAuthorized = true;
            }
        }
        // 학생인 경우
        else if (studentRepository.findByLoginId(loginId).isPresent()) {
            Student student = memberService.getCurrentStudentMember();
            if (conn.getStudent().getId().equals(student.getId())) {
                isAuthorized = true;
            }
        }
        
        if (!isAuthorized) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
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