package com.sparta.delivery.ai.service;

import com.querydsl.core.BooleanBuilder;
import com.sparta.delivery.ai.domain.Ai;
import com.sparta.delivery.ai.domain.QAi;
import com.sparta.delivery.ai.dto.AiAllResponseDto;
import com.sparta.delivery.ai.dto.AiResponseDto;
import com.sparta.delivery.ai.repository.AiRepository;
import com.sparta.delivery.global.exception.BusinessException;
import com.sparta.delivery.global.exception.domain.ErrorCode;
import com.sparta.delivery.user.domain.Role;
import com.sparta.delivery.user.domain.User;
import com.sparta.delivery.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AiService {

    private final AiRepository aiRepository;
    private final UserRepository userRepository;

//    @Async  //데이터 정합성 문제로 일단 보류
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveAi(String question, String answer) {
        Ai ai = new Ai(question, answer);
        aiRepository.save(ai);
    }

    // 모든 질문과 답변 조회
    @Transactional(readOnly = true)
    public AiAllResponseDto getAllChats(Long userId, String startday, String endday, String word) {
        //권한 체크
        User user = userRepository.findById(userId).orElseThrow(()-> new BusinessException(ErrorCode.USER_NOT_FOUND));
        if(user.getRole() != Role.ADMIN) throw new BusinessException(ErrorCode.NOT_ADMIN);

        //검색 조건 빌더
        BooleanBuilder builder  = new BooleanBuilder();
        QAi ai = QAi.ai;

        if(StringUtils.hasText(startday)) {
            LocalDateTime sDay = LocalDateTime.of(LocalDate.parse(startday), LocalTime.of(0,0,0));
            builder.and(ai.createdAt.goe(sDay));
        }

        if(StringUtils.hasText(endday)) {
            LocalDateTime eDay = LocalDateTime.of(LocalDate.parse(endday), LocalTime.of(23,59,59));
            builder.and(ai.createdAt.loe(eDay));
        }

        if(StringUtils.hasText(word)) {
            builder.and(ai.answer.contains(word));
        }

        List<Ai> aiList = (List<Ai>) aiRepository.findAll(builder, Sort.by(Sort.Order.desc("createdAt")));

        List<AiResponseDto> aiResponseDtoList = aiList.stream()
                .map(AiResponseDto::new)
                .toList();
        return new AiAllResponseDto(aiResponseDtoList);
    }

    @Transactional(readOnly = true)
    public AiResponseDto getChat(Long userId, String aiId) {
        //권한 체크
        User user = userRepository.findById(userId).orElseThrow(()-> new BusinessException(ErrorCode.USER_NOT_FOUND));
        if(user.getRole() != Role.ADMIN) throw new BusinessException(ErrorCode.NOT_ADMIN);

        Ai ai = aiRepository.findById(UUID.fromString(aiId)).orElseThrow(()-> new BusinessException(ErrorCode.AI_NOT_FOUND));
        return new AiResponseDto(ai);
    }
}
