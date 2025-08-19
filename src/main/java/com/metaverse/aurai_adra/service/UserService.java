package com.metaverse.aurai_adra.service;

import com.metaverse.aurai_adra.domain.User;
import com.metaverse.aurai_adra.dto.UserRegisterDto;
import com.metaverse.aurai_adra.jwt.JwtTokenProvider;
import com.metaverse.aurai_adra.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider; // JwtTokenProvider 필드 추가

    // 생성자에 JwtTokenProvider 주입받도록 수정
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public User registerUser(UserRegisterDto userRegisterDto) {
        // ... (기존 registerUser 메소드 코드는 동일)
        userRepository.findByNickname(userRegisterDto.getNickname()).ifPresent(user -> {
            throw new IllegalStateException("이미 존재하는 닉네임입니다.");
        });

        String encodedPassword = passwordEncoder.encode(userRegisterDto.getPassword());

        User user = new User();
        user.setNickname(userRegisterDto.getNickname());
        user.setPassword(encodedPassword);
        user.setGender(userRegisterDto.getGender());
        user.setAgeRange(userRegisterDto.getAgeRange());
        user.setCreatedAt(LocalDateTime.now());
        user.setModifiedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    public String loginUser(String nickname, String password) { // 반환 타입을 String으로 변경
        // 1. 닉네임으로 DB에서 사용자 정보 조회
        Optional<User> optionalUser = userRepository.findByNickname(nickname);

        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }

        User user = optionalUser.get();

        // 2. 비밀번호 일치 여부 확인
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 3. 토큰 생성 및 반환
        return jwtTokenProvider.createToken(user.getNickname());
    }
}