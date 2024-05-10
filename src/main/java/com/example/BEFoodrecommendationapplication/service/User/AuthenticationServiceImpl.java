package com.example.BEFoodrecommendationapplication.service.User;

import com.example.BEFoodrecommendationapplication.dto.AuthenticationRequest;
import com.example.BEFoodrecommendationapplication.dto.AuthenticationResponse;
import com.example.BEFoodrecommendationapplication.dto.RegisterRequest;
import com.example.BEFoodrecommendationapplication.entity.Role;

import com.example.BEFoodrecommendationapplication.entity.Token;
import com.example.BEFoodrecommendationapplication.entity.User;
import com.example.BEFoodrecommendationapplication.exception.*;
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

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailUtil emailUtil;
    private final OtpUtil otpUtil;
    private final TokenRepository tokenRepository;

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

    @Override
    @Transactional
    public String register(RegisterRequest request) throws DuplicateDataException {
        if (!request.getEmail().matches(EMAIL_REGEX)) {
            throw new InvalidEmailException("Invalid email format");
        }
        if (!request.getPassword().matches(PASSWORD_REGEX)) {
            throw new WrongFormatPasswordException("Password must contain uppercase and lowercase letters, at least 8 characters, at least one number, and at least one special character.");
        }

        Optional<User> existedUser = userRepository.findByEmail(request.getEmail());
        if (existedUser.isPresent() && existedUser.get().isActive()) {
            throw new DuplicateDataException("Email already exists.");
        }

        String otp = otpUtil.generateOtp();
        try {
            emailUtil.sendOtpEmail(request.getEmail(), otp);
        } catch (MessagingException e) {
            throw new RuntimeException("Unable to send otp please try again");
        }

        if(existedUser.isPresent() && !existedUser.get().isActive())
        {
            User user = existedUser.get();
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setCreatedAt(LocalDate.now());
            user.setOtp(otp);
            user.setOtpGeneratedTime(LocalDateTime.now());
            userRepository.save(user);
            return "Please check your email for the verification";
        }

        String passwordEncode = passwordEncoder.encode(request.getPassword());
        User user = User.builder()
                .email(request.getEmail())
                .name(request.getName())
                .password(passwordEncode)
                .createdAt(LocalDate.now())
                .role(Role.USER)
                .otp(otp)
                .active(false)
                .otpGeneratedTime(LocalDateTime.now())
                .build();
        userRepository.save(user);

        return "Please check your email for the verification";
    }
    @Override
    public AuthenticationResponse verifyAccount(String email, String otp) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with this email: " + email));
        if (user.getOtp().equals(otp) && Duration.between(user.getOtpGeneratedTime(),
                LocalDateTime.now()).getSeconds() < (3 * 60)) {

            user.setActive(true);

            userRepository.save(user);

            String jwtToken = jwtService.generateToken(user);

            saveUserToken(jwtToken, user);

            return AuthenticationResponse.builder().accessToken(jwtToken).build();
        }
        else {
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
        revokeAllTokenByUser(user);
        userRepository.save(user);
        return "Set password successfully.";
    }

    private void revokeAllTokenByUser(User user) {
        List<Token> validTokens = tokenRepository.findAllTokensByUser(user.getId());
        if(validTokens.isEmpty()) {
            return;
        }

        validTokens.forEach(t-> {
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