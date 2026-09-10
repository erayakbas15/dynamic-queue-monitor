package com.eray.dynamicqueuemonitor.worker;

import com.eray.dynamicqueuemonitor.queue.BoundedQueue;

public class SenderWorker extends AbstractWorker {

    private final BoundedQueue<String> queue;
    private long sequence = 0;

    public SenderWorker(String id, BoundedQueue<String> queue) {
        super(id);
        this.queue = queue;
    }

    @Override
    protected void doWork() throws InterruptedException {
        sequence++;

        String data = getId() + "-data-" + sequence;

        queue.put(data, this::setState);
    }
}