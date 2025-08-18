package com.metaverse.aurai_adra.controller;

import com.metaverse.aurai_adra.dto.UserRegisterDto;
import com.metaverse.aurai_adra.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
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

    // 로그인 API 추가
    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody UserRegisterDto userLoginDto) {
        try {
            userService.loginUser(userLoginDto.getNickname(), userLoginDto.getPassword());
            return ResponseEntity.ok("로그인에 성공했습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}