package com.eray.dynamicqueuemonitor.dto;

import com.eray.dynamicqueuemonitor.model.WorkerState;

import java.util.Map;

public record SystemStatusResponse(
        QueueStatus queue,
        Map<WorkerState, Long> senders,
        Map<WorkerState, Long> receivers
) {

    public record QueueStatus(
            int size,
            int capacity,
            double occupancyPercentage
    ) {
    }
}