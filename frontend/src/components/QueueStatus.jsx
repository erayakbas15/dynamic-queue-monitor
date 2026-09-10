function QueueStatus({ queue }) {
  const percentage = Math.round(queue.occupancyPercentage);

  return (
    <section className="card">
      <h2>Queue Status</h2>

      <div className="queue-info">
        <span>
          {queue.size} / {queue.capacity}
        </span>
        <span>{percentage}%</span>
      </div>

      <div className="progress">
        <div
          className="progress-fill"
          style={{ width: `${percentage}%` }}
        />
      </div>
    </section>
  );
}

export default QueueStatus;