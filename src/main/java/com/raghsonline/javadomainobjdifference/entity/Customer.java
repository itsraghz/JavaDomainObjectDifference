package com.raghsonline.javadomainobjdifference.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Customer {
    private String id;
    private String name;
    private String email;
    private String phone;
    private Address address;
    private List<Order> orders;

    // Getters and setters
}