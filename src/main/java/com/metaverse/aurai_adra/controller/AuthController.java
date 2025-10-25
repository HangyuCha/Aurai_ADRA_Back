package com.metaverse.aurai_adra.controller;

import com.metaverse.aurai_adra.domain.User;
import com.metaverse.aurai_adra.dto.AuthResponse;
import com.metaverse.aurai_adra.dto.KakaoAuthRequest;
import com.metaverse.aurai_adra.dto.KakaoProfile;
import com.metaverse.aurai_adra.jwt.JwtTokenProvider;
import com.metaverse.aurai_adra.service.KakaoAuthService;
import com.metaverse.aurai_adra.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final KakaoAuthService kakaoAuthService;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(KakaoAuthService kakaoAuthService,
                          UserService userService,
                          JwtTokenProvider jwtTokenProvider) {
        this.kakaoAuthService = kakaoAuthService;
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/kakao")
    public ResponseEntity<?> kakao(@RequestBody KakaoAuthRequest req) {
        try {
            String kakaoAccessToken = kakaoAuthService.exchangeCodeForToken(req.getCode(), req.getRedirectUri());
            KakaoProfile profile = kakaoAuthService.getUserProfile(kakaoAccessToken);
            String kakaoId = String.valueOf(profile.getId());

            Optional<User> userOpt = userService.findByKakaoIdOptional(kakaoId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                String jwt = jwtTokenProvider.createToken(user.getNickname());

                AuthResponse res = new AuthResponse();
                res.setNeedsRegister(false);
                res.setAccessToken(jwt);
                res.setOauthProvider("kakao");
                res.setProfile(new com.metaverse.aurai_adra.dto.KakaoProfile(null, user.getNickname(), user.getGender(), user.getAgeRange()));
                return ResponseEntity.ok(res);
            } else {
                AuthResponse res = new AuthResponse();
                res.setNeedsRegister(true);
                res.setOauthProvider("kakao");
                res.setOauthAccessToken(kakaoAccessToken);
                res.setProfile(profile);
                return ResponseEntity.status(202).body(res);
            }
        } catch (IllegalArgumentException bad) {
            // 카카오가 반환한 4xx 성격(redirect-uri, client-secret, code 재사용 등) → 400으로 내려줌
            return ResponseEntity.badRequest().body(bad.getMessage());
        } catch (Exception ex) {
            // 카카오 통신 오류 등 → 502로 내려줌
            return ResponseEntity.status(502).body("Kakao auth upstream error: " + ex.getMessage());
        }
    }
}