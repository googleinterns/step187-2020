import React from "react";
import { configure, mount } from "enzyme";
import Adapter from 'enzyme-adapter-react-16';
import AlertsContent from './AlertsContent';
import AlertInfo from './AlertInfo';
import ManagementRoutes from "./ManagementRoutes";
import { MemoryRouter } from "react-router-dom";

configure({ adapter: new Adapter() });

describe("navigating in AlertsManagement", () => {
  it("should display AlertsContent component on route", () => {
    const wrapper = mount(
      <MemoryRouter initialEntries={[ '/alerts' ]} initialIndex={0}>
        <ManagementRoutes />
      </MemoryRouter>
    );

    expect(wrapper.find(AlertsContent)).toHaveLength(1);
    expect(wrapper.find(AlertInfo)).toHaveLength(0);
  });

  it("should display AlertInfo component given parameter", () => {
    const wrapper = mount(
      <MemoryRouter initialEntries={[ '/alerts/1' ]} initialIndex={0}>
        <ManagementRoutes />
      </MemoryRouter>
    );

    expect(wrapper.find(AlertsContent)).toHaveLength(0);
    expect(wrapper.find(AlertInfo)).toHaveLength(1);
    expect(wrapper.find(AlertInfo).props().match.params.alertId).toEqual('1');
  });
});
