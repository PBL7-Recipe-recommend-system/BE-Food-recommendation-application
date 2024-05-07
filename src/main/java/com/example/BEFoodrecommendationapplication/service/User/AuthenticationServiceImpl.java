package com.example.BEFoodrecommendationapplication.service.User;

import com.example.BEFoodrecommendationapplication.dto.AuthenticationRequest;
import com.example.BEFoodrecommendationapplication.dto.AuthenticationResponse;
import com.example.BEFoodrecommendationapplication.dto.RegisterRequest;
import com.example.BEFoodrecommendationapplication.entity.Role;

import com.example.BEFoodrecommendationapplication.entity.User;
import com.example.BEFoodrecommendationapplication.exception.*;
import com.example.BEFoodrecommendationapplication.repository.UserRepository;
import com.example.BEFoodrecommendationapplication.util.EmailUtil;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailUtil emailUtil;

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

    @Override
    @Transactional
    public AuthenticationResponse register(RegisterRequest request) throws DuplicateDataException {
        if (!request.getEmail().matches(EMAIL_REGEX)) {
            throw new InvalidEmailException("Invalid email format");
        }
        if (!request.getPassword().matches(PASSWORD_REGEX)) {
            throw new WrongFormatPasswordException("Password must contain uppercase and lowercase letters, at least 8 characters, at least one number, and at least one special character.");
        }

        Optional<User> existedUser = userRepository.findByEmail(request.getEmail());
        if (existedUser.isPresent()) {
            throw new DuplicateDataException("Email already exists.");
        }
        String passwordEncode = passwordEncoder.encode(request.getPassword());
        User user = User.builder()
                .email(request.getEmail())
                .name(request.getName())
                .password(passwordEncode)
                .createdAt(LocalDate.now())
                .role(Role.USER)
                .build();
        userRepository.save(user);
        String jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .build();
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException ex) {
            throw new InvalidEmailException("Invalid username or password");
        }
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RecordNotFoundException("There is no user with that email!"));
        String jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .build();
    }

    @Override
    public String forgotPassword(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new RecordNotFoundException("User not found with this email: " + email)
        );

        try {
            emailUtil.sendResetPasswordEmail(email);
        } catch (MessagingException e) {
            throw new ErrorException("Unable to send email, please try again.");
        }
        return "Please check your email to set new password for your account.";
    }

    @Override
    public String setPassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new RecordNotFoundException("User not found with this email: " + email)
        );
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return "Set password successfully.";
    }

}