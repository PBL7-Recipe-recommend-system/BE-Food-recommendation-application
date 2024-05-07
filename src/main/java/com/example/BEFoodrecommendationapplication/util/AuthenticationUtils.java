package com.example.BEFoodrecommendationapplication.util;

import com.example.BEFoodrecommendationapplication.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthenticationUtils {
    private AuthenticationUtils()
    {

    }
    public static User getUserFromSecurityContext()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null)
        {
            Object principal = authentication.getPrincipal();
            if(principal instanceof User user)
                return user;
        }
        return null;
    }
}
