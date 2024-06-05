package com.example.BEFoodrecommendationapplication.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "water_intake")
public class WaterIntake {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "amount")
    private Float amount; // Water intake in liters

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


}

