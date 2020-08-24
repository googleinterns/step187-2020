import React from 'react';
import { shallow, configure } from "enzyme";
import Adapter from 'enzyme-adapter-react-16';
import { enableFetchMocks } from 'jest-fetch-mock';
enableFetchMocks();
import App from './App';

configure({ adapter: new Adapter() });

const LOGIN_URL = "/_ah/login?continue=%2F";
const LOGOUT_URL = "/_ah/logout?continue=%2F";
const LOGIN_MESSAGE = "logged in\n" + LOGOUT_URL;
const LOGOUT_MESSAGE = "stranger\n" + LOGIN_URL;

describe("getLoginStatus", () => {

  beforeEach(() => {
    fetch.resetMocks();
  });

  it("sets logged in state when user logs in", (done) => {
    fetch.mockResponseOnce(LOGIN_MESSAGE);
    const component = shallow(<App />);
    component.setState({ isLoggedIn: false, logURL: ""});

    setTimeout(() => {
      component.update()
      expect(component.state('isLoggedIn')).toEqual(true); 
      expect(component.state('logURL')).toEqual(LOGOUT_URL);
      done()
    }, 3000);
  });
  
  it("sets logged out state when user logs out", (done) => {
    fetch.mockResponseOnce(LOGOUT_MESSAGE);
    const component = shallow(<App />);
    component.setState({ isLoggedIn: false, logURL: ""});
 
    setTimeout(() => {
      component.update()
      expect(component.state('isLoggedIn')).toEqual(false); 
      expect(component.state('logURL')).toEqual(LOGIN_URL);
      done()
    }, 3000);
  });
});