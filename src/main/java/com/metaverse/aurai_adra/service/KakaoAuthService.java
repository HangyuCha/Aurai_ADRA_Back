package com.metaverse.aurai_adra.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.metaverse.aurai_adra.dto.KakaoProfile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class KakaoAuthService {

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.client-secret:}") // 선택
    private String clientSecret;

    @Value("${kakao.redirect-uri}")
    private String defaultRedirectUri;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper om = new ObjectMapper();

    public String exchangeCodeForToken(String code, String redirectUri) {
        String uri = "https://kauth.kakao.com/oauth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "authorization_code");
        form.add("client_id", clientId);
        form.add("code", code);
        form.add("redirect_uri", (redirectUri != null && !redirectUri.isBlank()) ? redirectUri : defaultRedirectUri);
        if (clientSecret != null && !clientSecret.isBlank()) {
            form.add("client_secret", clientSecret);
        }

        HttpEntity<MultiValueMap<String, String>> req = new HttpEntity<>(form, headers);

        ResponseEntity<String> res;
        try {
            res = restTemplate.exchange(uri, HttpMethod.POST, req, String.class);
        } catch (RestClientException e) {
            throw new IllegalStateException("Kakao token HTTP error: " + e.getMessage(), e);
        }

        if (!res.getStatusCode().is2xxSuccessful() || res.getBody() == null) {
            String body = res.getBody();
            throw new IllegalArgumentException("Kakao token error (" + res.getStatusCode().value() + "): " + body);
        }

        try {
            JsonNode root = om.readTree(res.getBody());
            String accessToken = root.path("access_token").asText(null);
            if (accessToken == null) {
                throw new IllegalArgumentException("Kakao token parse error: " + res.getBody());
            }
            return accessToken;
        } catch (Exception parse) {
            throw new IllegalArgumentException("Kakao token parse error: " + parse.getMessage(), parse);
        }
    }

    public KakaoProfile getUserProfile(String accessToken) {
        String uri = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> req = new HttpEntity<>(headers);

        ResponseEntity<String> res;
        try {
            res = restTemplate.exchange(uri, HttpMethod.GET, req, String.class);
        } catch (RestClientException e) {
            throw new IllegalStateException("Kakao me HTTP error: " + e.getMessage(), e);
        }

        if (!res.getStatusCode().is2xxSuccessful() || res.getBody() == null) {
            throw new IllegalArgumentException("Kakao me error (" + res.getStatusCode().value() + "): " + res.getBody());
        }

        try {
            JsonNode root = om.readTree(res.getBody());
            long id = root.path("id").asLong();
            String nickname = root.path("kakao_account").path("profile").path("nickname").asText(null);
            if (nickname == null) nickname = root.path("properties").path("nickname").asText(null);
            String gender = root.path("kakao_account").path("gender").asText(null);
            String ageRange = root.path("kakao_account").path("age_range").asText(null);
            return new KakaoProfile(id, nickname, gender, ageRange);
        } catch (Exception parse) {
            throw new IllegalArgumentException("Kakao me parse error: " + parse.getMessage(), parse);
        }
    }
}