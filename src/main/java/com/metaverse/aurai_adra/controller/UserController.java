package com.metaverse.aurai_adra.controller;

import com.metaverse.aurai_adra.dto.LoginResponseDto;
import com.metaverse.aurai_adra.dto.UserRegisterDto;
import com.metaverse.aurai_adra.domain.User;
import com.metaverse.aurai_adra.jwt.JwtTokenProvider;
import com.metaverse.aurai_adra.service.KakaoAuthService;
import com.metaverse.aurai_adra.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final KakaoAuthService kakaoAuthService;

    public UserController(UserService userService,
                          JwtTokenProvider jwtTokenProvider,
                          KakaoAuthService kakaoAuthService) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.kakaoAuthService = kakaoAuthService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegisterDto userRegisterDto) {
        try {
            // OAuth 회원가입 분기
            if ("kakao".equalsIgnoreCase(userRegisterDto.getOauthProvider())
                    && userRegisterDto.getOauthAccessToken() != null
                    && !userRegisterDto.getOauthAccessToken().isBlank()) {
                User saved = userService.registerUserFromOAuth(userRegisterDto, kakaoAuthService);
                // 선택: 가입 직후 JWT 발급해서 바로 로그인 상태로 만들고 싶다면 아래 사용
                // String token = jwtTokenProvider.createToken(saved.getNickname());
                // return ResponseEntity.ok(LoginResponseDto.builder()
                //         .accessToken(token)
                //         .nickname(saved.getNickname())
                //         .ageRange(saved.getAgeRange())
                //         .gender(saved.getGender())
                //         .build());
                return ResponseEntity.ok("OAuth 회원가입이 완료되었습니다.");
            }

            // 일반 회원가입
            userService.registerUser(userRegisterDto);
            return ResponseEntity.ok("회원가입이 성공적으로 완료되었습니다.");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> loginUser(@RequestBody UserRegisterDto userLoginDto) {
        try {
            String accessToken = userService.loginUser(userLoginDto.getNickname(), userLoginDto.getPassword());
            User user = userService.findByNickname(userLoginDto.getNickname());

            LoginResponseDto responseDto = LoginResponseDto.builder()
                    .accessToken(accessToken)
                    .nickname(user.getNickname())
                    .ageRange(user.getAgeRange())
                    .gender(user.getGender())
                    .build();

            return ResponseEntity.ok(responseDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(LoginResponseDto.builder().accessToken(null).build());
        }
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMe(@RequestHeader(value = "Authorization", required = false) String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ResponseEntity.status(401).build();
        }
        String token = authorization.substring(7);
        if (!jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(401).build();
        }
        String nickname = jwtTokenProvider.getNicknameFromToken(token);
        userService.deleteByNickname(nickname);
        return ResponseEntity.noContent().build();
    }
}