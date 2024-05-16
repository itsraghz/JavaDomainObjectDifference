package com.raghsonline.javadomainobjdifference.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
    private String method;
    private double amountPaid;
    private String transactionId;
    private String paymentDate;

    // Getters and setters
}