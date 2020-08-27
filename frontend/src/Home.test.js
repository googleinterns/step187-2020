import React from "react";
import { render, unmountComponentAtNode } from "react-dom";
import { act } from "react-dom/test-utils";
import { enableFetchMocks } from 'jest-fetch-mock';
enableFetchMocks();
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
  beforeEach(() => {
    fetch.resetMocks();
  });

  it("displays login message when user is not logged in", async () => {
    // Fake logged out status.
    const fakeStatus = {
      isLoggedIn: false,
      logURL: "/"
    };
    fetch.mockResponseOnce(JSON.stringify(fakeStatus)); 

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
    fetch.mockResponseOnce(JSON.stringify(fakeStatus)); 

    await act(async () => {
      render(<Home />, container);
    });
    
    expect(container.querySelector(".message").textContent).toBe("Thank you for visiting our app!");
  });
});
