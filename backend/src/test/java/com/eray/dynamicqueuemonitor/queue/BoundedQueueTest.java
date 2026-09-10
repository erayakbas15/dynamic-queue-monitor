package com.eray.dynamicqueuemonitor.queue;

import com.eray.dynamicqueuemonitor.model.WorkerState;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class BoundedQueueTest {

    @Test
    void shouldAddAndRemoveItems() throws InterruptedException {
        BoundedQueue<String> queue = new BoundedQueue<>(2);
        AtomicReference<WorkerState> state =
                new AtomicReference<>(WorkerState.RUNNABLE);

        queue.put("first", state::set);

        assertEquals(1, queue.size());
        assertEquals("first", queue.take(state::set));
        assertEquals(0, queue.size());
    }

    @Test
    void shouldCalculateOccupancyPercentage() throws InterruptedException {
        BoundedQueue<String> queue = new BoundedQueue<>(4);

        queue.put("one", state -> {});
        queue.put("two", state -> {});

        assertEquals(50.0, queue.getOccupancyPercentage());
    }

    @Test
    void shouldRejectInvalidCapacity() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new BoundedQueue<>(0)
        );
    }
}