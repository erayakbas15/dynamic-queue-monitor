import { useState } from "react";

function ControlPanel({
  onStart,
  onStop,
  onAddThreads,
  onStopGroup,
}) {
  const [senderCount, setSenderCount] = useState(2);
  const [receiverCount, setReceiverCount] = useState(2);
  const [queueCapacity, setQueueCapacity] = useState(10);

  return (
    <section className="card">
      <h2>System Controls</h2>

      <div className="form-row">
        <label>
          Senders
          <input
            type="number"
            min="0"
            value={senderCount}
            onChange={(e) => setSenderCount(Number(e.target.value))}
          />
        </label>

        <label>
          Receivers
          <input
            type="number"
            min="0"
            value={receiverCount}
            onChange={(e) => setReceiverCount(Number(e.target.value))}
          />
        </label>

        <label>
          Queue Capacity
          <input
            type="number"
            min="1"
            value={queueCapacity}
            onChange={(e) => setQueueCapacity(Number(e.target.value))}
          />
        </label>
      </div>

      <div className="button-row">
        <button
          onClick={() =>
            onStart(senderCount, receiverCount, queueCapacity)
          }
        >
          Start System
        </button>

        <button onClick={onStop}>
          Stop System
        </button>
      </div>

      <div className="button-row">
        <button onClick={() => onAddThreads("SENDER", 1)}>
          Add Sender
        </button>

        <button onClick={() => onAddThreads("RECEIVER", 1)}>
          Add Receiver
        </button>

        <button onClick={() => onStopGroup("SENDER")}>
          Stop Senders
        </button>

        <button onClick={() => onStopGroup("RECEIVER")}>
          Stop Receivers
        </button>
      </div>
    </section>
  );
}

export default ControlPanel;