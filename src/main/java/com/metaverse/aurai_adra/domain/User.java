// src/main/java/com/metaverse/aurai_adra/domain/User.java
package com.metaverse.aurai_adra.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "account", catalog = "adra")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Column(length = 10)
    private String gender;

    @Column(name = "age_range", length = 10)
    private String ageRange;

    @Column(name = "kakao_id", unique = true)
    private String kakaoId;

    @Column(name = "provider", length = 20)
    private String provider;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "modified_at", nullable = false)
    private LocalDateTime modifiedAt;

    // learning age persisted fields
    @Column(name = "learning_age_decade")
    private Integer learningAgeDecade;

    @Column(name = "learning_age_percent")
    private Integer learningAgePercent;

    @Column(name = "learning_age_updated_at")
    private LocalDateTime learningAgeUpdatedAt;

    public User() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getAgeRange() { return ageRange; }
    public void setAgeRange(String ageRange) { this.ageRange = ageRange; }

    public String getKakaoId() { return kakaoId; }
    public void setKakaoId(String kakaoId) { this.kakaoId = kakaoId; }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getModifiedAt() { return modifiedAt; }

    public Integer getLearningAgeDecade() { return learningAgeDecade; }
    public void setLearningAgeDecade(Integer learningAgeDecade) { this.learningAgeDecade = learningAgeDecade; }

    public Integer getLearningAgePercent() { return learningAgePercent; }
    public void setLearningAgePercent(Integer learningAgePercent) { this.learningAgePercent = learningAgePercent; }

    public LocalDateTime getLearningAgeUpdatedAt() { return learningAgeUpdatedAt; }
    public void setLearningAgeUpdatedAt(LocalDateTime learningAgeUpdatedAt) { this.learningAgeUpdatedAt = learningAgeUpdatedAt; }
}