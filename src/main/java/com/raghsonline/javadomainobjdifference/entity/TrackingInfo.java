package com.raghsonline.javadomainobjdifference.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrackingInfo {
    private String createdDate;
    private String createdBy;
    private String updatedDate;
    private String updatedBy;

    // Getters and setters
}