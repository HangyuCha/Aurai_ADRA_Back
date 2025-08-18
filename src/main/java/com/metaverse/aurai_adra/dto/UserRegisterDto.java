package com.metaverse.aurai_adra.dto;

import lombok.Data;

@Data
public class UserRegisterDto {
    private String nickname;
    private String password;
    private String gender;
    private String ageRange;
}