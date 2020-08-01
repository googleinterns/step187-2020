import React from "react";
import { render, unmountComponentAtNode } from "react-dom";
import { act } from "react-dom/test-utils";
import Home from "./Home";

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

it("renders correct welcome message", () => {
  act(() => {
    render(<Home />, container);
  });
  expect(container.querySelector(".message").textContent).toBe("Please log in.");

  act(() => {
    render(<Home isLoggedIn="true" />, container);
  });
  expect(container.querySelector(".message").textContent).toBe("Thank you for visiting our app!");
});