import { render, screen } from "@testing-library/react";
import "@testing-library/jest-dom";

import QueueStatus from "./QueueStatus";

test("shows queue size and occupancy", () => {
  render(
    <QueueStatus
      queue={{
        size: 5,
        capacity: 10,
        occupancyPercentage: 50,
      }}
    />
  );

  expect(screen.getByText("5 / 10")).toBeInTheDocument();
  expect(screen.getByText("50%")).toBeInTheDocument();
});