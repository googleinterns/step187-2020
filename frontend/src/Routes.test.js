import React from "react";
import { MemoryRouter } from "react-router-dom";
import { configure, mount } from "enzyme";
import Adapter from 'enzyme-adapter-react-16';
import AlertConfiguration from './AlertConfiguration/AlertConfigurationComponents/AlertConfiguration';
import AlertsManagement from './AlertsManagement/AlertsManagement';
import App from './App';
import ErrorPage from './ErrorPage';
import Routes from "./Routes";

configure({ adapter: new Adapter() });

describe("full app routing", () => {
  it("should display App component on home route", () => {
    const wrapper = mount(
      <MemoryRouter initialEntries={[ '/' ]} initialIndex={0}>
        <Routes />
      </MemoryRouter>
    );

    expect(wrapper.find(App)).toHaveLength(1);
    expect(wrapper.find(AlertConfiguration)).toHaveLength(0);
    expect(wrapper.find(AlertsManagement)).toHaveLength(0);
    expect(wrapper.find(ErrorPage)).toHaveLength(0);
  });

  it("should display AlertsManagement component on alerts route", () => {
    const wrapper = mount(
      <MemoryRouter initialEntries={[ '/alerts' ]} initialIndex={0}>
        <Routes />
      </MemoryRouter>
    );

    expect(wrapper.find(App)).toHaveLength(0);
    expect(wrapper.find(AlertsManagement)).toHaveLength(1);
    expect(wrapper.find(AlertConfiguration)).toHaveLength(0);
    expect(wrapper.find(ErrorPage)).toHaveLength(0);
  });

  it("should display AlertConfiguration component on configs route", () => {
    const wrapper = mount(
      <MemoryRouter initialEntries={[ '/configs' ]} initialIndex={0}>
        <Routes />
      </MemoryRouter>
    );

    expect(wrapper.find(App)).toHaveLength(0);
    expect(wrapper.find(AlertsManagement)).toHaveLength(0);
    expect(wrapper.find(AlertConfiguration)).toHaveLength(1);
    expect(wrapper.find(ErrorPage)).toHaveLength(0);
  });

  it("should redirect to ErrorPage on invalid route", () => {
    const wrapper = mount(
      <MemoryRouter initialEntries={[ '/random' ]} initialIndex={0}>
        <Routes />
      </MemoryRouter>
    );

    expect(wrapper.find(App)).toHaveLength(0);
    expect(wrapper.find(AlertsManagement)).toHaveLength(0);
    expect(wrapper.find(AlertConfiguration)).toHaveLength(0);
    expect(wrapper.find(ErrorPage)).toHaveLength(1);
  })
});
