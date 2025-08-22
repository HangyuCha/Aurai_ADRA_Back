package com.metaverse.aurai_adra.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponseDto {
    private String accessToken;
    private String nickname;    // 추가
    private String ageRange;    // 추가
    private String gender;
}