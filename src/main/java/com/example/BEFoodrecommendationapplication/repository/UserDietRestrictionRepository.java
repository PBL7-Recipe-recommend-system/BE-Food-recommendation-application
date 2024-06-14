package com.example.BEFoodrecommendationapplication.repository;


import com.example.BEFoodrecommendationapplication.entity.DietRestriction;
import com.example.BEFoodrecommendationapplication.entity.User;
import com.example.BEFoodrecommendationapplication.entity.UserDietRestriction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDietRestrictionRepository extends JpaRepository<UserDietRestriction, Integer> {
    UserDietRestriction findByUserAndDietRestriction(User user, DietRestriction dietRestriction);

    UserDietRestriction findByUser(User user);
}
