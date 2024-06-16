package com.example.BEFoodrecommendationapplication.service.User;

import com.example.BEFoodrecommendationapplication.dto.AddUserRequest;
import com.example.BEFoodrecommendationapplication.dto.UserResponse;
import com.example.BEFoodrecommendationapplication.entity.User;
import com.example.BEFoodrecommendationapplication.exception.DuplicateDataException;
import com.example.BEFoodrecommendationapplication.exception.InvalidEmailException;
import com.example.BEFoodrecommendationapplication.exception.RecordNotFoundException;
import com.example.BEFoodrecommendationapplication.exception.WrongFormatPasswordException;
import com.example.BEFoodrecommendationapplication.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

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

    @Override
    @Transactional
    public void addUserAccount(AddUserRequest userRequest) throws DuplicateDataException {
        if (!userRequest.getEmail().matches(EMAIL_REGEX)) {
            throw new InvalidEmailException("Invalid email format");
        }
        if (!userRequest.getPassword().matches(PASSWORD_REGEX)) {
            throw new WrongFormatPasswordException("Password must contain uppercase and lowercase letters, at least 8 characters, at least one number, and at least one special character.");
        }

        Optional<User> existedUser = userRepository.findByEmail(userRequest.getEmail());
        if (existedUser.isPresent()) {
            throw new DuplicateDataException("Email already exists.");
        } else {

            String passwordEncode = passwordEncoder.encode(userRequest.getPassword());
            User user = User.builder()
                    .email(userRequest.getEmail())
                    .name(userRequest.getName())
                    .password(passwordEncode)
                    .createdAt(LocalDate.now())
                    .role(userRequest.getRole()) // set the role passed as parameter
                    .active(true)
                    .isCustomPlan(false)
                    .otpGeneratedTime(LocalDateTime.now())
                    .build();
            userRepository.save(user);

        }
    }
}
