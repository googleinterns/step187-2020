import React from "react";
import { render, unmountComponentAtNode } from "react-dom";
import { act } from "react-dom/test-utils";
import AlertsContent from "./AlertsContent";

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

// TODO: add tests for AlertsContent component.
// Some tests to include: test handleCheckbox and handleTabs functions:
// it will display the right tabpanel when the corresponding tab is clicked on,
// it will have the correct checked elements when a checkbox is checked,
// it will have the correct unchecked elements when a checkbox is unchecked.
