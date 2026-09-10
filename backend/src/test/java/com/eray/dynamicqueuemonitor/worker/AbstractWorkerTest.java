package com.eray.dynamicqueuemonitor.worker;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AbstractWorkerTest {

    @Test
    void shouldChangeThreadPriority() throws InterruptedException {
        TestWorker worker = new TestWorker("test-worker");

        worker.startWorker();
        worker.setPriority(8);

        assertEquals(8, worker.getPriority());

        worker.stopWorker();
    }

    private static class TestWorker extends AbstractWorker {

        TestWorker(String id) {
            super(id);
        }

        @Override
        protected void doWork() throws InterruptedException {
            Thread.sleep(100);
        }
    }
}