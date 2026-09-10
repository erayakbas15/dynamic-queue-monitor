# Dynamic Queue Monitor

A multi-channel dynamic queue monitoring system developed with Spring Boot and React.

The application simulates Sender and Receiver threads communicating through a shared bounded queue. Thread states and queue metrics can be monitored from the frontend while the number of workers can be changed dynamically.

## Features

- Dynamic Sender and Receiver thread creation
- Thread-safe bounded queue
- Queue occupancy monitoring
- Sender and Receiver state monitoring
- Dynamic thread addition
- Stop all workers or a selected worker group
- Thread priority management
- Backend metrics updated every second
- React dashboard updated every second
- Backend and frontend tests
- OpenAPI API documentation

## Technologies

### Backend

- Java 21
- Spring Boot
- Maven
- Java Concurrency (`ReentrantLock`, `Condition`)
- JUnit
- MockMvc

### Frontend

- React
- Vite
- JavaScript
- Vitest
- React Testing Library

## Project Structure

```text
dynamic-queue-monitor/
├── backend/
│   ├── src/main/
│   └── src/test/
├── frontend/
│   └── src/
├── docs/
│   └── openapi.yaml
└── README.md
```

## Architecture

```text
React Frontend
      |
      | REST API
      v
SystemController
      |
      v
ThreadManagerService
      |
      +-----------------------+
      |                       |
 SenderWorker             ReceiverWorker
      |                       |
      +-----> BoundedQueue <---+
               |
          ReentrantLock
          notEmpty
          notFull
```

The shared bounded queue is implemented using `ArrayDeque`, `ReentrantLock` and two `Condition` objects.

A Sender waits when the queue is full, while a Receiver waits when the queue is empty.

The application uses the following worker states:

- `RUNNABLE`: The worker is active.
- `WAITING`: The worker is waiting because the queue is full or empty.
- `BLOCKED`: The worker is waiting to acquire the queue lock.
- `TERMINATED`: The worker has been stopped.

These are application-level monitoring states and are not intended to directly represent `java.lang.Thread.State`.

## Running the Project

### Requirements

- Java 21
- Node.js
- npm

### Backend

#### Windows PowerShell

From the project root:

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

#### Linux / macOS

```bash
cd backend
./mvnw spring-boot:run
```

The backend runs at:

```text
http://localhost:8080
```

### Frontend

Open a second terminal from the project root:

```powershell
cd frontend
npm install
npm run dev
```

The frontend runs at:

```text
http://localhost:5173
```

## API Endpoints

### Start System

```http
POST /api/system/start
```

Example request:

```json
{
  "senderCount": 3,
  "receiverCount": 2,
  "queueCapacity": 10
}
```

### Stop System

```http
POST /api/system/stop
```

Stops all Sender and Receiver workers.

### Add Threads

```http
POST /api/threads/add
```

Example request:

```json
{
  "type": "SENDER",
  "count": 2
}
```

Supported worker types:

```text
SENDER
RECEIVER
```

### Stop Thread Group

```http
POST /api/threads/{type}/stop
```

Example:

```http
POST /api/threads/RECEIVER/stop
```

### Change Thread Priority

```http
PATCH /api/threads/{type}/{id}/priority
```

Example:

```http
PATCH /api/threads/SENDER/sender-1/priority
```

Request body:

```json
{
  "priority": 8
}
```

Java thread priority values are between 1 and 10.

Thread priority is treated as a scheduling hint and is not used for application correctness.

### Get System Status

```http
GET /api/status
```

Example response:

```json
{
  "queue": {
    "size": 5,
    "capacity": 5,
    "occupancyPercentage": 100.0
  },
  "senders": {
    "RUNNABLE": 0,
    "WAITING": 3,
    "BLOCKED": 0,
    "TERMINATED": 0
  },
  "receivers": {
    "RUNNABLE": 0,
    "WAITING": 0,
    "BLOCKED": 0,
    "TERMINATED": 0
  }
}
```

Backend metrics are collected every second. The React frontend polls the status endpoint once per second to display the latest snapshot.

## Concurrency Design

The bounded queue uses two condition variables:

- `notEmpty` is used by Receivers waiting for new data.
- `notFull` is used by Senders waiting for available queue capacity.

Queue conditions are checked using `while` loops so that a worker checks the shared condition again after being awakened.

Worker state and running flags use `volatile` fields because they may be written by worker threads and read from HTTP request threads.

`ConcurrentHashMap` is used to keep Sender and Receiver workers, while `AtomicInteger` is used to generate worker identifiers.

## Design Decisions

### Custom Bounded Queue

Java provides ready-to-use blocking queue implementations. For this case study, the bounded queue was implemented using `ReentrantLock` and `Condition` so that the synchronization behavior can be observed and controlled directly.

### REST Polling

The required monitoring interval is one second. For this scope, REST polling was preferred instead of WebSocket to keep the communication model simple.

If the number of clients or the update frequency increased significantly, Server-Sent Events or WebSocket could be considered.

### In-Memory State

No database is used because the application represents a live concurrency simulation and does not require persistent state.

## Testing

### Backend

Run:

```powershell
cd backend
.\mvnw.cmd test
```

The backend includes tests for:

- Spring application startup
- Queue insertion and removal
- Queue occupancy calculation
- Invalid queue capacity
- System startup
- Status endpoint
- Dynamic Sender creation
- Priority API
- Worker priority changes

### Frontend

Run:

```powershell
cd frontend
npm test
```

The frontend includes component tests for queue metrics and thread-state visualization.

Production build:

```powershell
npm run build
```

## OpenAPI

The REST API definition is available at:

```text
docs/openapi.yaml
```