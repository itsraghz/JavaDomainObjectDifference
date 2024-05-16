package com.raghsonline.javadomainobjdifference;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class Change {

    private ChangeType changeType;
    private String componentName;
    private String fieldName;
    private String currentValue;
    private String previousValue;
}
