package com.example.BEFoodrecommendationapplication.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component
@RequiredArgsConstructor
public class EmailUtil {

    private final TemplateEngine templateEngine;
    private final JavaMailSender javaMailSender;
    public static final String UTF_8_ENCODING = "UTF-8";

    @Value("${spring.mail.username}")
    private String fromEmail;
    @Async
    public void sendOtpEmail(String email, String otp) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8_ENCODING);
        helper.setFrom(fromEmail);
        helper.setTo(email);
        helper.setSubject("Food Recommendation App Otp Verification");
        helper.setPriority(1);
        Context context = new Context();
        context.setVariable("otp",otp);
        context.setVariable("email",email);

        String emailContent = templateEngine.process("SendOtpEmail", context);
        helper.setText(emailContent, true);

        javaMailSender.send(message);

    }
    @Async
    public void sendResetPasswordEmail(String email) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject("Reset password");
        mimeMessageHelper.setText("""
        <div>
          <a href="http://localhost:8082/api/v1/auth/set-password?email=%s" target="_blank">click link to set your password.</a>
        </div>
        """.formatted(email), true);

        javaMailSender.send(mimeMessage);
    }
}