package com.eray.dynamicqueuemonitor.controller;

import com.eray.dynamicqueuemonitor.dto.AddThreadsRequest;
import com.eray.dynamicqueuemonitor.dto.StartSystemRequest;
import com.eray.dynamicqueuemonitor.dto.SystemStatusResponse;
import com.eray.dynamicqueuemonitor.model.WorkerType;
import com.eray.dynamicqueuemonitor.service.ThreadManagerService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class SystemController {

    private final ThreadManagerService threadManager;

    public SystemController(ThreadManagerService threadManager) {
        this.threadManager = threadManager;
    }

    @PostMapping("/system/start")
    public SystemStatusResponse startSystem(
            @Valid @RequestBody StartSystemRequest request) {

        threadManager.startSystem(
                request.senderCount(),
                request.receiverCount(),
                request.queueCapacity()
        );

        return threadManager.getStatus();
    }

    @PostMapping("/system/stop")
    public SystemStatusResponse stopSystem() {
        threadManager.stopSystem();
        return threadManager.getStatus();
    }

    @PostMapping("/threads/add")
    public SystemStatusResponse addThreads(
            @Valid @RequestBody AddThreadsRequest request) {

        threadManager.addWorkers(request.type(), request.count());

        return threadManager.getStatus();
    }

    @PostMapping("/threads/{type}/stop")
    public SystemStatusResponse stopThreads(
            @PathVariable WorkerType type) {

        threadManager.stopWorkers(type);

        return threadManager.getStatus();
    }

    @GetMapping("/status")
    public SystemStatusResponse getStatus() {
        return threadManager.getStatus();
    }
}