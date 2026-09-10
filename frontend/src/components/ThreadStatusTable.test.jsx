import { render, screen } from "@testing-library/react";
import "@testing-library/jest-dom";

import ThreadStatusTable from "./ThreadStatusTable";

test("shows sender and receiver states", () => {
  render(
    <ThreadStatusTable
      senders={{
        RUNNABLE: 2,
        WAITING: 1,
        BLOCKED: 0,
        TERMINATED: 0,
      }}
      receivers={{
        RUNNABLE: 1,
        WAITING: 0,
        BLOCKED: 0,
        TERMINATED: 2,
      }}
    />
  );

  expect(screen.getByText("RUNNABLE")).toBeInTheDocument();
  expect(screen.getByText("WAITING")).toBeInTheDocument();
  expect(screen.getByText("BLOCKED")).toBeInTheDocument();
  expect(screen.getByText("TERMINATED")).toBeInTheDocument();
});