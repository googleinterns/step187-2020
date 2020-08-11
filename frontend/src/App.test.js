import React from 'react';
import { shallow, configure } from "enzyme";
import Adapter from 'enzyme-adapter-react-16';
import { enableFetchMocks } from 'jest-fetch-mock';
enableFetchMocks();
import App from './App';

configure({ adapter: new Adapter() });

const LOGIN_URL = "/_ah/login?continue=%2F";
const LOGIN_MESSAGE = "logged in\n/_ah/logout?continue=%2F";
const LOGOUT_URL = "/_ah/logout?continue=%2F";
const LOGOUT_MESSAGE = "stranger\n/_ah/logout?continue=%2F";

// TODO: write the tests below.
describe("getLoginStatus", () => {
  const getLoginStatus = jest.spyOn(App.prototype, 'getLoginStatus');

  beforeEach(() => {
    fetch.resetMocks();
  });

  it("sets logged in state when user is logged in", async () => {
    fetch.mockResponse(LOGIN_MESSAGE);
    // getLoginStatus is called in componentDidMount, which is called by shallow.
    const component = shallow(<App />);
    component.setState({ isLoggedIn: false, logURL: ""});
    expect(getLoginStatus).toHaveBeenCalled();

    setTimeout(() => {
      component.update()
      expect(component.state('isLoggedIn')).toEqual(true); 
      expect(component.state('logURL')).toEqual(LOGOUT_URL);
    }, 3000);
  });
  
  it("sets logged out state when user is logged out", () => {
    fetch.mockResponse(LOGOUT_MESSAGE);
    const component = shallow(<App />);
    component.setState({ isLoggedIn: false, logURL: ""});
    expect(getLoginStatus).toHaveBeenCalled();

    setTimeout(() => {
      component.update()
      expect(component.state('isLoggedIn')).toEqual(false); 
      expect(component.state('logURL')).toEqual(LOGIN_URL);
    }, 3000);
  });
});
