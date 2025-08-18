package com.metaverse.aurai_adra.service;

import com.metaverse.aurai_adra.domain.User;
import com.metaverse.aurai_adra.dto.UserRegisterDto;
import com.metaverse.aurai_adra.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(UserRegisterDto userRegisterDto) {
        // 사용자 이름 중복 확인
        userRepository.findById(userRegisterDto.getNickname()).ifPresent(user -> { // .getNickname()으로 변경
            throw new IllegalStateException("이미 존재하는 사용자 이름입니다.");
        });

        // 비밀번호 암호화 로직 (나중에 추가)
        String hashedPassword = userRegisterDto.getPassword();

        User user = new User();
        user.setNickname(userRegisterDto.getNickname()); // .setNickname()으로 변경
        user.setPassword(hashedPassword);
        user.setGender(userRegisterDto.getGender());
        user.setAgeRange(userRegisterDto.getAgeRange());
        user.setCreatedAt(LocalDateTime.now());
        user.setModifiedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    public User loginUser(String nickname, String password) { // 파라미터 이름을 nickname으로 변경
        // 1. 사용자 이름으로 DB에서 사용자 정보 조회
        User user = userRepository.findById(nickname)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 2. 비밀번호 일치 여부 확인
        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return user;
    }
}