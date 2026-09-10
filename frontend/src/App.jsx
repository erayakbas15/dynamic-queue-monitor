import { useEffect, useState } from "react";
import "./App.css";

import ControlPanel from "./components/ControlPanel";
import QueueStatus from "./components/QueueStatus";
import ThreadStatusTable from "./components/ThreadStatusTable";

import {
  addThreads,
  getStatus,
  startSystem,
  stopSystem,
  stopThreadGroup,
} from "./services/api";

const emptyStatus = {
  queue: {
    size: 0,
    capacity: 10,
    occupancyPercentage: 0,
  },
  senders: {
    RUNNABLE: 0,
    WAITING: 0,
    BLOCKED: 0,
    TERMINATED: 0,
  },
  receivers: {
    RUNNABLE: 0,
    WAITING: 0,
    BLOCKED: 0,
    TERMINATED: 0,
  },
};

function App() {
  const [status, setStatus] = useState(emptyStatus);
  const [error, setError] = useState("");

  async function refreshStatus() {
    try {
      const data = await getStatus();
      setStatus(data);
      setError("");
    } catch {
      setError("Could not connect to backend.");
    }
  }

  useEffect(() => {
    refreshStatus();

    const interval = setInterval(refreshStatus, 1000);

    return () => clearInterval(interval);
  }, []);

  async function handleStart(senderCount, receiverCount, queueCapacity) {
    try {
      const data = await startSystem(
        senderCount,
        receiverCount,
        queueCapacity
      );
      setStatus(data);
      setError("");
    } catch {
      setError("Failed to start system.");
    }
  }

  async function handleStop() {
    try {
      const data = await stopSystem();
      setStatus(data);
    } catch {
      setError("Failed to stop system.");
    }
  }

  async function handleAddThreads(type, count) {
    try {
      const data = await addThreads(type, count);
      setStatus(data);
    } catch {
      setError("Failed to add thread.");
    }
  }

  async function handleStopGroup(type) {
    try {
      const data = await stopThreadGroup(type);
      setStatus(data);
    } catch {
      setError("Failed to stop thread group.");
    }
  }

  return (
    <main className="container">
      <header>
        <h1>Dynamic Queue Monitor</h1>
        <p>Sender and Receiver Thread Monitoring System</p>
      </header>

      {error && <div className="error">{error}</div>}

      <ControlPanel
        onStart={handleStart}
        onStop={handleStop}
        onAddThreads={handleAddThreads}
        onStopGroup={handleStopGroup}
      />

      <div className="dashboard">
        <QueueStatus queue={status.queue} />

        <ThreadStatusTable
          senders={status.senders}
          receivers={status.receivers}
        />
      </div>
    </main>
  );
}

export default App;