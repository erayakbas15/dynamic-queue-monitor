package com.eray.dynamicqueuemonitor.worker;

import com.eray.dynamicqueuemonitor.queue.BoundedQueue;

public class ReceiverWorker extends AbstractWorker {

    private final BoundedQueue<String> queue;

    public ReceiverWorker(String id, BoundedQueue<String> queue) {
        super(id);
        this.queue = queue;
    }

    @Override
    protected void doWork() throws InterruptedException {
        queue.take(this::setState);
    }
}