import React from "react";
import { render, unmountComponentAtNode } from "react-dom";
import { BrowserRouter } from "react-router-dom";
import { act } from "react-dom/test-utils";
import NavBar from "./NavBar";
import { enableFetchMocks } from 'jest-fetch-mock';
enableFetchMocks();

let container = null;
beforeEach(() => {
  container = document.createElement("div");
  document.body.appendChild(container);
  fetch.resetMocks();
});

afterEach(() => {
  unmountComponentAtNode(container);
  container.remove();
  container = null;
});

describe("when the user is logged in", () => {
  beforeEach(async () => {
    // Fake logged in status.
    const fakeStatus = {
      isLoggedIn: true,
      logURL: "/logout"
    };
    fetch.mockResponseOnce(JSON.stringify(fakeStatus)); 

    await act(async () => {
      render(<BrowserRouter><NavBar /></BrowserRouter>, container);
    });
  });

  afterEach(() => {
    global.fetch.mockRestore();
  })

  it("displays Logout option with URL", () => {
    expect(container.querySelector("#login-button").textContent).toBe("Logout");
    expect(container.querySelector("#login-button").href).toBe("http://localhost/logout");
  });

  it("shows Alerts and Configs", () => {
    expect(container.querySelector('#alerts-button').textContent).toBe("Alerts");
    expect(container.querySelector('#configs-button').textContent).toBe("Configs");
  });
});

describe("when the user is logged out", () => {
  beforeEach(async () => {
    // Fake logged out status.
    const fakeStatus = {
      isLoggedIn: false,
      logURL: "/login"
    };
    fetch.mockResponseOnce(JSON.stringify(fakeStatus)); 

    await act(async () => {
      render(<BrowserRouter><NavBar /></BrowserRouter>, container);
    });
  });

  afterEach(() => {
    global.fetch.mockRestore();
  });

  it("displays Login option with URL", () => {
    expect(container.querySelector("#login-button").textContent).toBe("Login");
    expect(container.querySelector("#login-button").href).toBe("http://localhost/login");
  });

  it("hides Alerts and Configs", () => {
    expect(container.querySelector('#alerts-button')).toBe(null);
    expect(container.querySelector('#configs-button')).toBe(null);
  });
});
