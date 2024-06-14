package com.example.BEFoodrecommendationapplication.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "diet_restriction")
public class DietRestriction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "restriction_id")
    private Integer id;

    @Column(name = "restriction_type")
    private String type;

    @Column(name = "description")
    private String description;


}
