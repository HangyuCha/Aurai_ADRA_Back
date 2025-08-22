package com.metaverse.aurai_adra.controller;

import com.metaverse.aurai_adra.dto.LoginResponseDto;
import com.metaverse.aurai_adra.dto.UserRegisterDto;
import com.metaverse.aurai_adra.domain.User;
import com.metaverse.aurai_adra.jwt.JwtTokenProvider;
import com.metaverse.aurai_adra.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    public UserController(UserService userService, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserRegisterDto userRegisterDto) {
        try {
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