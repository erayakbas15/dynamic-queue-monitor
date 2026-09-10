package com.eray.dynamicqueuemonitor.dto;

import jakarta.validation.constraints.Min;

public record StartSystemRequest(
        @Min(0) int senderCount,
        @Min(0) int receiverCount,
        @Min(1) int queueCapacity
) {
}