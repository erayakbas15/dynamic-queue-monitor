package com.eray.dynamicqueuemonitor.worker;

import com.eray.dynamicqueuemonitor.model.WorkerState;

public abstract class AbstractWorker implements Runnable {

    private final String id;
    private volatile WorkerState state = WorkerState.TERMINATED;
    private volatile boolean running = false;

    private Thread thread;

    protected AbstractWorker(String id) {
        this.id = id;
    }

    public synchronized void startWorker() {
        if (running) {
            return;
        }

        running = true;
        state = WorkerState.RUNNABLE;

        thread = new Thread(this, id);
        thread.start();
    }

    public synchronized void stopWorker() {
        running = false;

        if (thread != null) {
            thread.interrupt();
        }
    }

    @Override
    public final void run() {
        try {
            while (running) {
                state = WorkerState.RUNNABLE;
                doWork();

                if (running) {
                    Thread.sleep(1000);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            running = false;
            state = WorkerState.TERMINATED;
        }
    }

    protected abstract void doWork() throws InterruptedException;

    protected void setState(WorkerState state) {
        this.state = state;
    }

    public String getId() {
        return id;
    }

    public WorkerState getState() {
        return state;
    }

    public boolean isRunning() {
        return running;
    }

    public synchronized void setPriority(int priority) {
        if (priority < Thread.MIN_PRIORITY || priority > Thread.MAX_PRIORITY) {
            throw new IllegalArgumentException("Priority must be between 1 and 10");
        }

        if (thread != null) {
            thread.setPriority(priority);
        }
    }

    public int getPriority() {
        if (thread == null) {
            return Thread.NORM_PRIORITY;
        }

        return thread.getPriority();
    }
}