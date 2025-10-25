package com.metaverse.aurai_adra.dto;

public class KakaoProfile {
    private Long id;          // kakao user id
    private String nickname;  // properties.nickname
    private String gender;    // "male"/"female"
    private String ageRange;  // "20~29"

    public KakaoProfile() {}

    public KakaoProfile(Long id, String nickname, String gender, String ageRange) {
        this.id = id;
        this.nickname = nickname;
        this.gender = gender;
        this.ageRange = ageRange;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getAgeRange() { return ageRange; }
    public void setAgeRange(String ageRange) { this.ageRange = ageRange; }
}