package com.metaverse.aurai_adra.service;

import com.metaverse.aurai_adra.domain.User;
import com.metaverse.aurai_adra.dto.UserRegisterDto;
import com.metaverse.aurai_adra.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(UserRegisterDto userRegisterDto) {
        // 닉네임 중복 확인
        userRepository.findByNickname(userRegisterDto.getNickname()).ifPresent(user -> {
            throw new IllegalStateException("이미 존재하는 닉네임입니다.");
        });

        // 비밀번호 암호화 로직 (나중에 추가)
        String hashedPassword = userRegisterDto.getPassword();

        User user = new User();
        user.setNickname(userRegisterDto.getNickname());
        user.setPassword(hashedPassword);
        user.setGender(userRegisterDto.getGender());
        user.setAgeRange(userRegisterDto.getAgeRange());
        user.setCreatedAt(LocalDateTime.now());
        user.setModifiedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    public User loginUser(String nickname, String password) {
        // 1. 닉네임으로 DB에서 사용자 정보 조회
        Optional<User> optionalUser = userRepository.findByNickname(nickname);

        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }

        User user = optionalUser.get();

        // 2. 비밀번호 일치 여부 확인
        // 실제로는 암호화된 비밀번호를 비교해야 합니다.
        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return user;
    }
}