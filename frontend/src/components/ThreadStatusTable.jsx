const states = ["RUNNABLE", "WAITING", "BLOCKED", "TERMINATED"];

function ThreadStatusTable({ senders, receivers }) {
  return (
    <section className="card">
      <h2>Thread Status</h2>

      <table>
        <thead>
          <tr>
            <th>State</th>
            <th>Senders</th>
            <th>Receivers</th>
          </tr>
        </thead>

        <tbody>
          {states.map((state) => (
            <tr key={state}>
              <td>{state}</td>
              <td>{senders[state] ?? 0}</td>
              <td>{receivers[state] ?? 0}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </section>
  );
}

export default ThreadStatusTable;