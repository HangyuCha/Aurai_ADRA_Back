package com.metaverse.aurai_adra.service;

import com.metaverse.aurai_adra.domain.User;
import com.metaverse.aurai_adra.dto.UserRegisterDto;
import com.metaverse.aurai_adra.jwt.JwtTokenProvider;
import com.metaverse.aurai_adra.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public void registerUser(UserRegisterDto userRegisterDto) {
        User user = new User();
        user.setNickname(userRegisterDto.getNickname());
        user.setPassword(passwordEncoder.encode(userRegisterDto.getPassword()));
        user.setGender(userRegisterDto.getGender());
        user.setAgeRange(userRegisterDto.getAgeRange());
        userRepository.save(user);
    }

    public String loginUser(String nickname, String password) {
        User user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        return jwtTokenProvider.createToken(nickname); // ← 반드시 이 형태여야 함
    }

    public User findByNickname(String nickname) {
        Optional<User> userOpt = userRepository.findByNickname(nickname);
        return userOpt.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

    public void deleteByNickname(String nickname) {
        userRepository.findByNickname(nickname).ifPresent(userRepository::delete);
    }
}