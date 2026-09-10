package com.eray.dynamicqueuemonitor.controller;

import com.eray.dynamicqueuemonitor.service.ThreadManagerService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SystemControllerTest {

    private ThreadManagerService threadManager;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        threadManager = new ThreadManagerService();
        SystemController controller = new SystemController(threadManager);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    @AfterEach
    void tearDown() {
        threadManager.stopSystem();
    }

    @Test
    void shouldStartSystem() throws Exception {
        mockMvc.perform(post("/api/system/start")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "senderCount": 2,
                                  "receiverCount": 1,
                                  "queueCapacity": 5
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.queue.capacity").value(5));
    }

    @Test
    void shouldReturnSystemStatus() throws Exception {
        threadManager.startSystem(1, 1, 4);

        mockMvc.perform(get("/api/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.queue.capacity").value(4))
                .andExpect(jsonPath("$.senders").exists())
                .andExpect(jsonPath("$.receivers").exists());
    }

    @Test
    void shouldAddSenderThreads() throws Exception {
        threadManager.startSystem(1, 0, 5);

        mockMvc.perform(post("/api/threads/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "type": "SENDER",
                                  "count": 2
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.senders.RUNNABLE").value(3));
    }

    @Test
    void shouldChangeWorkerPriority() throws Exception {
        threadManager.startSystem(1, 0, 5);

        mockMvc.perform(patch("/api/threads/SENDER/sender-1/priority")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "priority": 8
                                }
                                """))
                .andExpect(status().isOk());
    }
}