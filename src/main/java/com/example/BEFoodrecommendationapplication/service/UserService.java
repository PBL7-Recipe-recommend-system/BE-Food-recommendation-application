package com.example.BEFoodrecommendationapplication.service;

import com.example.BEFoodrecommendationapplication.dto.UserInput;
import com.example.BEFoodrecommendationapplication.entity.User;
import org.springframework.stereotype.Service;


public interface UserService {
    public User save(Integer id, UserInput userInput);
    public User getUser(Integer id);
}
