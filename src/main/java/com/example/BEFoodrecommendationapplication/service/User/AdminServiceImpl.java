package com.example.BEFoodrecommendationapplication.service.User;

import com.example.BEFoodrecommendationapplication.dto.UserResponse;
import com.example.BEFoodrecommendationapplication.entity.User;
import com.example.BEFoodrecommendationapplication.exception.RecordNotFoundException;
import com.example.BEFoodrecommendationapplication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;

    @Override
    public Page<UserResponse> findAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(UserResponse::fromUser);
    }

    @Override
    public UserResponse findUserById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("User not found with id " + id));

        return UserResponse.fromUser(user);
    }
}
