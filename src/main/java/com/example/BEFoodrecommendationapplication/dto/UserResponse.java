package com.example.BEFoodrecommendationapplication.dto;

import com.example.BEFoodrecommendationapplication.entity.User;
import lombok.*;

import java.time.LocalDate;
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private int userId;
    private String name;
    private String email;
    private String gender;
    private LocalDate birthday;
    private String avatar;

    public static UserResponse fromUser(User user) {
        return UserResponse.builder()
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .gender(user.getGender())
                .birthday(user.getBirthday())
                .avatar(user.getAvatar())
                .build();
    }
}
