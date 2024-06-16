package com.example.BEFoodrecommendationapplication.dto;

import com.example.BEFoodrecommendationapplication.entity.Role;
import lombok.*;

import java.time.LocalDate;
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddUserRequest {
    private String name;
    private String email;
    private String password;
    private String gender;
    private LocalDate birthday;
    private Role role;
    private Boolean isActive;

}
