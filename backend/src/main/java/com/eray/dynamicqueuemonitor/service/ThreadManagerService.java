package com.eray.dynamicqueuemonitor.service;

import com.eray.dynamicqueuemonitor.dto.SystemStatusResponse;
import com.eray.dynamicqueuemonitor.model.WorkerState;
import com.eray.dynamicqueuemonitor.model.WorkerType;
import com.eray.dynamicqueuemonitor.queue.BoundedQueue;
import com.eray.dynamicqueuemonitor.worker.AbstractWorker;
import com.eray.dynamicqueuemonitor.worker.ReceiverWorker;
import com.eray.dynamicqueuemonitor.worker.SenderWorker;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ThreadManagerService {

    private final Map<String, SenderWorker> senders = new ConcurrentHashMap<>();
    private final Map<String, ReceiverWorker> receivers = new ConcurrentHashMap<>();

    private final AtomicInteger senderCounter = new AtomicInteger();
    private final AtomicInteger receiverCounter = new AtomicInteger();

    private volatile BoundedQueue<String> queue = new BoundedQueue<>(10);

    public synchronized void startSystem(int senderCount, int receiverCount, int queueCapacity) {
        stopSystem();

        senders.clear();
        receivers.clear();

        senderCounter.set(0);
        receiverCounter.set(0);

        queue = new BoundedQueue<>(queueCapacity);

        if (senderCount > 0) {
            addWorkers(WorkerType.SENDER, senderCount);
        }

        if (receiverCount > 0) {
            addWorkers(WorkerType.RECEIVER, receiverCount);
        }
    }

    public synchronized void addWorkers(WorkerType type, int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("Thread count must be greater than zero");
        }

        for (int i = 0; i < count; i++) {
            if (type == WorkerType.SENDER) {
                addSender();
            } else {
                addReceiver();
            }
        }
    }

    private void addSender() {
        String id = "sender-" + senderCounter.incrementAndGet();

        SenderWorker worker = new SenderWorker(id, queue);
        senders.put(id, worker);
        worker.startWorker();
    }

    private void addReceiver() {
        String id = "receiver-" + receiverCounter.incrementAndGet();

        ReceiverWorker worker = new ReceiverWorker(id, queue);
        receivers.put(id, worker);
        worker.startWorker();
    }

    public synchronized void stopWorkers(WorkerType type) {
        if (type == WorkerType.SENDER) {
            senders.values().forEach(AbstractWorker::stopWorker);
        } else {
            receivers.values().forEach(AbstractWorker::stopWorker);
        }
    }

    public synchronized void stopSystem() {
        senders.values().forEach(AbstractWorker::stopWorker);
        receivers.values().forEach(AbstractWorker::stopWorker);
    }

    public SystemStatusResponse getStatus() {
        SystemStatusResponse.QueueStatus queueStatus =
                new SystemStatusResponse.QueueStatus(
                        queue.size(),
                        queue.getCapacity(),
                        queue.getOccupancyPercentage()
                );

        return new SystemStatusResponse(
                queueStatus,
                countStates(senders.values()),
                countStates(receivers.values())
        );
    }

    private Map<WorkerState, Long> countStates(
            Collection<? extends AbstractWorker> workers) {

        Map<WorkerState, Long> counts = new EnumMap<>(WorkerState.class);

        for (WorkerState state : WorkerState.values()) {
            counts.put(state, 0L);
        }

        for (AbstractWorker worker : workers) {
            WorkerState state = worker.getState();
            counts.put(state, counts.get(state) + 1);
        }

        return counts;
    }
}