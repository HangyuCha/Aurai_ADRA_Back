package com.metaverse.aurai_adra.dto;

public class AuthResponse {
    private boolean needsRegister;
    private String accessToken;        // 기존 사용자면 앱 JWT
    private String oauthProvider;      // "kakao"
    private String oauthAccessToken;   // 신규 가입 유도 시 사용
    private KakaoProfile profile;      // 닉네임/성별/연령대

    public boolean isNeedsRegister() { return needsRegister; }
    public void setNeedsRegister(boolean needsRegister) { this.needsRegister = needsRegister; }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public String getOauthProvider() { return oauthProvider; }
    public void setOauthProvider(String oauthProvider) { this.oauthProvider = oauthProvider; }

    public String getOauthAccessToken() { return oauthAccessToken; }
    public void setOauthAccessToken(String oauthAccessToken) { this.oauthAccessToken = oauthAccessToken; }

    public KakaoProfile getProfile() { return profile; }
    public void setProfile(KakaoProfile profile) { this.profile = profile; }
}