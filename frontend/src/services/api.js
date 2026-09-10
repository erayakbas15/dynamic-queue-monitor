const API_URL = "http://localhost:8080/api";

export async function getStatus() {
  const response = await fetch(`${API_URL}/status`);
  return response.json();
}

export async function startSystem(senderCount, receiverCount, queueCapacity) {
  const response = await fetch(`${API_URL}/system/start`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      senderCount,
      receiverCount,
      queueCapacity,
    }),
  });

  return response.json();
}

export async function stopSystem() {
  const response = await fetch(`${API_URL}/system/stop`, {
    method: "POST",
  });

  return response.json();
}

export async function addThreads(type, count) {
  const response = await fetch(`${API_URL}/threads/add`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      type,
      count,
    }),
  });

  return response.json();
}

export async function stopThreadGroup(type) {
  const response = await fetch(`${API_URL}/threads/${type}/stop`, {
    method: "POST",
  });

  return response.json();
}