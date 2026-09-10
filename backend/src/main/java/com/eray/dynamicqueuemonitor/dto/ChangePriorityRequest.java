package com.eray.dynamicqueuemonitor.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record ChangePriorityRequest(
        @Min(1) @Max(10) int priority
) {
}