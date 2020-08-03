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

describe("homepage message", () => {
  it("displays login message when user is not logged in", () => {
    act(() => {
      render(<Home />, container);
    });
    expect(container.querySelector(".message").textContent).toBe("Please log in.");
  });

  it("displays welcome message when user is logged in", () => {
    act(() => {
      render(<Home isLoggedIn="true" />, container);
    });
    expect(container.querySelector(".message").textContent).toBe("Thank you for visiting our app!");
  });
});
