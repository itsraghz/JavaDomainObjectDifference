package com.raghsonline.javadomainobjdifference.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    private String orderId;
    private String orderDate;
    private List<Product> products;
    private double totalAmount;
    private Payment payment;
    private TrackingInfo trackingInfo;

    // Getters and setters
}