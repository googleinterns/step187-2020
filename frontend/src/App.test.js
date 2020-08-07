import React from 'react';
import { render, unmountComponentAtNode } from "react-dom";
import { act } from "react-dom/test-utils";
import { shallow, configure } from "enzyme";
import Adapter from 'enzyme-adapter-react-16';
import App from './App';

configure({ adapter: new Adapter() });

const LOGIN_STATUS = "logged in\n";
const LOGIN_URL = "/_ah/login?continue=%2F";
const LOGIN_MESSAGE = "logged in\n/_ah/logout?continue=%2F";
const LOGOUT_STATUS = "stranger\n";
const LOGOUT_URL = "/_ah/logout?continue=%2F";

// TODO: write the tests below.
// describe("getLoginStatus", () => {
//   it("sets logged in state when user is logged in", () => {
//   });
  
//   it("sets logged out state when user is logged out", () => {
//   });
// });
