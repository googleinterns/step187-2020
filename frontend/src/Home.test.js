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
  afterEach(() => {
    global.fetch.mockRestore();
  });

  it("displays login message when user is not logged in", async () => {
    // Fake logged out status.
    const fakeStatus = {
      isLoggedIn: false,
      logURL: "/"
    };
    jest.spyOn(global, "fetch").mockImplementation(() =>
      Promise.resolve({
        json: () => Promise.resolve(fakeStatus)
      })
    );

    await act(async () => {
      render(<Home />, container);
    });

    expect(container.querySelector(".message").textContent).toBe("Please log in.");
  });

  it("displays welcome message when user is logged in", async () => {
    // Fake logged in status.
    const fakeStatus = {
      isLoggedIn: true,
      logURL: "/"
    };
    jest.spyOn(global, "fetch").mockImplementation(() =>
      Promise.resolve({
        json: () => Promise.resolve(fakeStatus)
      })
    );

    await act(async () => {
      render(<Home />, container);
    });
    
    expect(container.querySelector(".message").textContent).toBe("Thank you for visiting our app!");
  });
});
