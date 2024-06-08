package com.example.BEFoodrecommendationapplication.service.User;

import com.example.BEFoodrecommendationapplication.dto.AuthenticationRequest;
import com.example.BEFoodrecommendationapplication.dto.AuthenticationResponse;
import com.example.BEFoodrecommendationapplication.dto.RegisterRequest;
import com.example.BEFoodrecommendationapplication.entity.Role;
import com.example.BEFoodrecommendationapplication.entity.Token;
import com.example.BEFoodrecommendationapplication.entity.User;
import com.example.BEFoodrecommendationapplication.exception.DuplicateDataException;
import com.example.BEFoodrecommendationapplication.exception.InvalidEmailException;
import com.example.BEFoodrecommendationapplication.exception.RecordNotFoundException;
import com.example.BEFoodrecommendationapplication.exception.WrongFormatPasswordException;
import com.example.BEFoodrecommendationapplication.repository.TokenRepository;
import com.example.BEFoodrecommendationapplication.repository.UserRepository;
import com.example.BEFoodrecommendationapplication.util.EmailUtil;
import com.example.BEFoodrecommendationapplication.util.OtpUtil;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailUtil emailUtil;
    private final OtpUtil otpUtil;
    private final TokenRepository tokenRepository;

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
        } else {

            String passwordEncode = passwordEncoder.encode(request.getPassword());
            User user = User.builder()
                    .email(request.getEmail())
                    .name(request.getName())
                    .password(passwordEncode)
                    .createdAt(LocalDate.now())
                    .role(Role.USER)
                    .active(true)
                    .isCustomPlan(false)
                    .otpGeneratedTime(LocalDateTime.now())
                    .build();
            userRepository.save(user);
            String jwtToken = jwtService.generateToken(user);
            saveUserToken(jwtToken, user);

            return AuthenticationResponse.builder().accessToken(jwtToken).role(user.getRole()).build();
        }


    }

    @Override
    public String verifyAccount(String email, String otp) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with this email: " + email));

        if (user.getOtp().equals(otp) && Duration.between(user.getOtpGeneratedTime(),
                LocalDateTime.now()).getSeconds() < (5 * 60)) {

            user.setActive(true);

            userRepository.save(user);

            return "Account Verified";
        } else {
            throw new RuntimeException("Wrong token or time out, please try again!");
        }
    }

    @Override
    public String regenerateOtp(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with this email: " + email));
        String otp = otpUtil.generateOtp();
        try {
            emailUtil.sendOtpEmail(email, otp);
        } catch (MessagingException e) {
            throw new RuntimeException("Unable to send otp please try again");
        }
        user.setOtp(otp);
        user.setOtpGeneratedTime(LocalDateTime.now());
        userRepository.save(user);
        return "Email sent... please verify account within 1 minute";
    }

    @Override
    public AuthenticationResponse setPassword(Integer userId, String oldPassword, String newPassword, String confirmPassword) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new RecordNotFoundException("User not found with this id: " + userId)
        );

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BadCredentialsException("Current password is incorrect");
        }
        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("New password and confirm password do not match");
        }

        return getAuthenticationResponse(newPassword, user);
    }

    private AuthenticationResponse getAuthenticationResponse(String newPassword, User user) {
        if (!newPassword.matches(PASSWORD_REGEX)) {
            throw new WrongFormatPasswordException("Password must contain uppercase and lowercase letters, at least 8 characters, at least one number, and at least one special character.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        revokeAllTokenByUser(user);
        userRepository.save(user);
        String jwtToken = jwtService.generateToken(user);

        saveUserToken(jwtToken, user);
        return AuthenticationResponse.builder().accessToken(jwtToken).build();
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
        if (!user.isActive()) {
            throw new RuntimeException("Your account is not verified");
        }

        String jwtToken = jwtService.generateToken(user);

        revokeAllTokenByUser(user);
        saveUserToken(jwtToken, user);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .role(user.getRole())
                .build();
    }

    @Override
    public String forgotPassword(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new RecordNotFoundException("User not found with this email: " + email)
        );
        regenerateOtp(email);
        return "Please check your email to set new password for your account.";
    }

    @Override
    public AuthenticationResponse setPassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new RecordNotFoundException("User not found with this email: " + email)
        );

        return getAuthenticationResponse(newPassword, user);
    }

    private void revokeAllTokenByUser(User user) {
        List<Token> validTokens = tokenRepository.findAllTokensByUser(user.getId());
        if (validTokens.isEmpty()) {
            return;
        }

        validTokens.forEach(t -> {
            t.setLoggedOut(true);
        });

        tokenRepository.saveAll(validTokens);
    }

    private void saveUserToken(String jwt, User user) {
        Token token = Token.builder()
                .token(jwt)
                .loggedOut(false)
                .user(user)
                .build();
        tokenRepository.save(token);
    }
}