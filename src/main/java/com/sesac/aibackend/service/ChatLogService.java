package com.sesac.aibackend.service;

import com.sesac.aibackend.domain.ChatLog;
import com.sesac.aibackend.domain.User;
import com.sesac.aibackend.error.NotFoundException;
import com.sesac.aibackend.repository.ChatLogRepository;
import com.sesac.aibackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatLogService {
    private final UserRepository userRepository;
    private final ChatLogRepository chatLogRepository;

    @Transactional
    public ChatLog save(String username, String prompt, String response) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> NotFoundException.of("user", userId));
        User user = userRepository.findByUsername(username)
                .orElseThrow(()->NotFoundException.of("user",username));
        return chatLogRepository.save(
                ChatLog.builder()
                        .user(user)
                        .prompt(prompt)
                        .response(response)
                        .build()
        );
    }

    /**
     * 기본 조회 — userId(PK)로 조회, fetch join 없음.
     * 응답에서 getUser().getId()만 읽는 from()과 짝을 이룹니다.
     */
    @Transactional(readOnly = true)
    public List<ChatLog> findByUserId(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> NotFoundException.of("user", userId));
        return chatLogRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * fetch join 조회 — userId(PK)로 조회하며 user를 함께 로딩.
     * 응답에서 getUser().getUsername()을 읽는 fromWithUsername()과 짝을 이룹니다.
     */
    @Transactional(readOnly = true)
    public List<ChatLog> findByUserIdWithUser(Long userId) {
        // 존재하지 않는 사용자는 404로 구분 (fetch join은 결과가 없으면 빈 리스트라 구분 불가)
        userRepository.findById(userId)
                .orElseThrow(() -> NotFoundException.of("user", userId));
        return chatLogRepository.findByUserIdWithUser(userId);
    }
}
