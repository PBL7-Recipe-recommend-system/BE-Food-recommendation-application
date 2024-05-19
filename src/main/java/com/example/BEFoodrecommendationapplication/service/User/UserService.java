package com.example.BEFoodrecommendationapplication.service.User;

import com.example.BEFoodrecommendationapplication.dto.UserInput;
import com.example.BEFoodrecommendationapplication.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


public interface UserService {
    User save(Integer id, UserInput userInput);
    User getUser(Integer id);
    String uploadAvatar(MultipartFile multipartFile, Integer id) throws IOException;
}
