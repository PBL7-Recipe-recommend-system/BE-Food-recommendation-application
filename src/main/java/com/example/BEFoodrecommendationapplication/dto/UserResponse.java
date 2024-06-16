package com.example.BEFoodrecommendationapplication.dto;

import com.example.BEFoodrecommendationapplication.entity.Role;
import com.example.BEFoodrecommendationapplication.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private Role role;
    private Boolean isActive;


    public static UserResponse fromUser(User user) {
        return UserResponse.builder()
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .gender(user.getGender())
                .birthday(user.getBirthday())
                .avatar(user.getAvatar())
                .role(user.getRole())
                .isActive(user.isActive())
                .build();
    }
}
