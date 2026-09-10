package com.eray.dynamicqueuemonitor.dto;

import com.eray.dynamicqueuemonitor.model.WorkerType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AddThreadsRequest(
        @NotNull WorkerType type,
        @Min(1) int count
) {
}