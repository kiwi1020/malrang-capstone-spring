package com.malrang.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_ratings")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class UserRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rated_user_id", nullable = false)
    private User ratedUser; // 평점을 받은 사용자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rater_user_id", nullable = true)
    private User raterUser; // 평점을 준 사용자

    @Column(name = "rating", nullable = false)
    private Double rating;

    @Builder
    public UserRating(User ratedUser, User raterUser, Double rating) {
        this.ratedUser = ratedUser;
        this.raterUser = raterUser;
        this.rating = rating;
    }
}