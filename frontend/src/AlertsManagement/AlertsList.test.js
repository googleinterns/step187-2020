import React from "react";
import { unmountComponentAtNode } from "react-dom";
import AlertsList from "./AlertList";

let container = null;
beforeEach(() => {
  container = document.createElement("div");
  document.body.appendChild(container);
});

afterEach(() => {
  unmountComponentAtNode(container);
  container.remove();
  container = null;
});

// TODO: add tests for AlertsList component.
// Some tests to include: it displays the resolved alerts when display is set to resolved,
// it displays the unresolved alerts when display is set to unresolved,
// it displays all alerts when display is set to all.
