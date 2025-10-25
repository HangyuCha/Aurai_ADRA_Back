package com.metaverse.aurai_adra.dto;

import lombok.Data;

@Data
public class UserRegisterDto {
    private String nickname;
    private String password;
    private String gender;
    private String ageRange;
    private String oauthProvider;      // "kakao"
    private String oauthAccessToken;   // 카카오 사용자 액세스 토큰
}