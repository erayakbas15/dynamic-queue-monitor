package com.eray.dynamicqueuemonitor.queue;

import com.eray.dynamicqueuemonitor.model.WorkerState;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

public class BoundedQueue<T> {

    private final Queue<T> queue;
    private final int capacity;

    private final ReentrantLock lock;
    private final Condition notEmpty;
    private final Condition notFull;

    public BoundedQueue(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Queue capacity must be greater than zero");
        }

        this.capacity = capacity;
        this.queue = new ArrayDeque<>();
        this.lock = new ReentrantLock();

        this.notEmpty = lock.newCondition();
        this.notFull = lock.newCondition();
    }

    public void put(T item, Consumer<WorkerState> stateUpdater) throws InterruptedException {
        if (!lock.tryLock()) {
            stateUpdater.accept(WorkerState.BLOCKED);
            lock.lockInterruptibly();
        }

        try {
            while (queue.size() >= capacity) {
                stateUpdater.accept(WorkerState.WAITING);
                notFull.await();
            }

            stateUpdater.accept(WorkerState.RUNNABLE);

            queue.add(item);
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    public T take(Consumer<WorkerState> stateUpdater) throws InterruptedException {
        if (!lock.tryLock()) {
            stateUpdater.accept(WorkerState.BLOCKED);
            lock.lockInterruptibly();
        }

        try {
            while (queue.isEmpty()) {
                stateUpdater.accept(WorkerState.WAITING);
                notEmpty.await();
            }

            stateUpdater.accept(WorkerState.RUNNABLE);

            T item = queue.remove();
            notFull.signal();

            return item;
        } finally {
            lock.unlock();
        }
    }

    public int size() {
        lock.lock();

        try {
            return queue.size();
        } finally {
            lock.unlock();
        }
    }

    public int getCapacity() {
        return capacity;
    }

    public double getOccupancyPercentage() {
        return ((double) size() / capacity) * 100;
    }
}