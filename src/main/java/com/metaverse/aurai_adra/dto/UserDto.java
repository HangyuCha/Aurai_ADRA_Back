package com.metaverse.aurai_adra.dto;

import com.metaverse.aurai_adra.domain.User;
import lombok.Data;

@Data
public class UserDto {
    private String id;
    private String nickname;
    private String gender;
    private String ageRange;

    public static UserDto from(User u) {
        if (u == null) return null;
        UserDto d = new UserDto();
        // domain.User의 id 타입(예: Long 또는 String)에 맞춰 String으로 변환
        d.setId(u.getId() != null ? String.valueOf(u.getId()) : null);
        d.setNickname(u.getNickname());
        d.setGender(u.getGender());
        d.setAgeRange(u.getAgeRange());
        return d;
    }
}