import React from "react";
import { act } from "react-dom/test-utils";
import { shallow, configure} from "enzyme";
import Adapter from 'enzyme-adapter-react-16';
import { enableFetchMocks } from 'jest-fetch-mock';
enableFetchMocks();
import { PureAlertsContent as AlertsContent } from "./AlertsContent";
import AlertsList from "./AlertsList";
import { tabLabels } from './management_constants';
import * as helpers from './management_helpers';

configure({ adapter: new Adapter() });

const styles = { 
  root: {
    flexGrow: 1,
    maxWidth: 900,
    margin: 'auto',
  }
};

describe("componentDidMount", () => {
  // Mock data for helper function return value.
  const expectedAlerts = new Map();
  expectedAlerts.set(1234567890123456, {
    timestampDate: "Sun Dec 08 2019",
    anomalies: [{
      dimensionName: "Ramen", metricName: "Interest Over Time",
      timestampDate: "Sun Dec 29 2019"
    },
    { 
      dimensionName: "Ramen", metricName: "Interest Over Time",
      timestampDate: "Sun Dec 01 2019",
    }],
    status: "UNRESOLVED",
  });
  expectedAlerts.set(1987654321098765, {
    timestampDate: "Fri Dec 27 2019",
    anomalies: [{ 
      dimensionName: "Ramen", metricName: "Interest Over Time",
      timestampDate: "Wed Nov 27 2019"
    }],
    status: "RESOLVED",
  });
  const expectedUnchecked = [1234567890123456];
  const expectedChecked = [1987654321098765];

  it("should set state correctly with alerts information", async () => {
    const mock = jest.spyOn(helpers, "getAlertsData")
                  .mockReturnValue([expectedAlerts, expectedUnchecked, expectedChecked]);
    const wrapper = shallow(<AlertsContent classes={styles} />, { disableLifecycleMethods: true });
    
    await wrapper.instance().componentDidMount();

    expect(mock).toHaveBeenCalled();
    expect(wrapper.state('allAlerts')).toMatchObject(expectedAlerts);
    expect(wrapper.state('unchecked')).toMatchObject(expectedUnchecked);
    expect(wrapper.state('checked')).toMatchObject(expectedChecked);
  });

  it("should throw an error when unexpected data is received", async () => {
    const mock = jest.spyOn(helpers, "getAlertsData").mockReturnValue([expectedAlerts]);
    const wrapper = shallow(<AlertsContent classes={styles} />, { disableLifecycleMethods: true });
    const instance = wrapper.instance();

    expect(mock).toHaveBeenCalled();
    await expect(instance.componentDidMount()
    ).rejects.toEqual(new Error("getAlertsData() did not return the correct alerts data."));
  });
})

describe("handleTabs", () => {
  it("should display the correct tabpanel when the tab is changed", () => {
    const wrapper = shallow(<AlertsContent classes={styles} />, { disableLifecycleMethods: true });
    wrapper.setState({ tab: tabLabels.ALL }); 

    act(() => {
      const tabs = wrapper.find('WithStyles(ForwardRef(Tabs))');
      tabs.simulate("change", {}, tabLabels.RESOLVED);      
      wrapper.update();
    });

    expect(wrapper.state('tab')).toEqual(tabLabels.RESOLVED);
  });
});

describe("handleCheckbox", () => {
  // Mock data for the alerts, unresolved and resolved.
  const fakeAlerts = new Map();
  fakeAlerts.set(0, {
    timestamp: "2020-01-20",
    anomalies: 3,
  });
  fakeAlerts.set(1, {
    timestamp: "2019-09-19",
    anomalies: 2,
  });
  fakeAlerts.set(2, {
    timestamp: "2019-08-06",
    anomalies: 2,
  });
  const fakeUnresolvedIds = [0, 1];
  const fakeResolvedIds = [2];

  // Expected mock data.
  const editUnchecked = [1];
  const editChecked = [2, 0];
  const doubleChecked = [0, 2];

  const SERVLET_ROUTE = '/api/v1/alerts-data';
  const POST_DATA = {"body": "0 RESOLVED", "method": "POST",};

  let wrapper;

  beforeEach(() => {
    wrapper = shallow(<AlertsContent classes={styles} />, { disableLifecycleMethods: true });
    wrapper.setState({ 
      allAlerts: fakeAlerts,
      unchecked: fakeUnresolvedIds,
      checked: fakeResolvedIds,
    });
    fetch.resetMocks();
  });

  afterEach(() => {
    wrapper.unmount();
    jest.clearAllMocks();
  });

  it("should maintain the correct unresolved and resolved elements after click", () => {
    act(() => {
      const boxes = wrapper.find(AlertsList).at(0).dive().find('WithStyles(ForwardRef(Checkbox))');
      boxes.at(0).simulate('click');
    });

    expect(wrapper.state('unchecked')).toEqual(editUnchecked);
    expect(wrapper.state('checked')).toEqual(editChecked);
  });

  it("should throw error if alert is considered both unresolved and resolved", () => {
    wrapper.setState({ checked: doubleChecked });

    const boxes = wrapper.find(AlertsList).at(0).dive().find('WithStyles(ForwardRef(Checkbox))');
    function testCheckbox() { boxes.at(0).simulate('click'); }

    expect(testCheckbox).toThrowError('Misplaced alert');
  });

  it("should throw error if alert is neither unresolved nor resolved", () => {
    const boxes = wrapper.find(AlertsList).at(0).dive().find('WithStyles(ForwardRef(Checkbox))');
    wrapper.setState({ unchecked: editUnchecked });
    function testCheckbox() { boxes.at(0).simulate('click'); }

    expect(testCheckbox).toThrowError('Misplaced alert')
  });

  it("sends a POST request with the correct data to change alert status", () => {
    jest.spyOn(global, 'fetch');

    act(() => {
      const boxes = wrapper.find(AlertsList).at(0).dive().find('WithStyles(ForwardRef(Checkbox))');
      boxes.at(0).simulate('click');
    });

    expect(global.fetch).toHaveBeenCalledTimes(1);
    expect(global.fetch).toHaveBeenCalledWith(SERVLET_ROUTE, POST_DATA);
  });

  // TODO: write tests for dealing with error response from backend (e.g. 404 code).
});
