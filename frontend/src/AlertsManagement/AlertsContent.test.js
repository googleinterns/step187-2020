import React from "react";
import { act } from "react-dom/test-utils";
import { shallow, configure} from "enzyme";
import Adapter from 'enzyme-adapter-react-16';
import { enableFetchMocks } from 'jest-fetch-mock';
enableFetchMocks();
import { PureAlertsContent as AlertsContent } from "./AlertsContent";
import AlertsList from "./AlertsList";

configure({ adapter: new Adapter() });

const styles = { 
  root: {
    flexGrow: 1,
    maxWidth: 900,
    margin: 'auto',
  }
};

describe("handleTabs", () => {
  const tabLabels = {
    UNRESOLVED: 0,
    RESOLVED: 1,
    ALL: 2,
  };

  it("should display the right tabpanel when the tab is changed", () => {
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
  })

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

describe("fetch alerts", () => {
  // Fake alert JSON data.
  const fakeAlerts = [{
    anomalies: [
      { dataPoints: {"2019-11-24": {value: 79}, }, 
        dimensionName: "Ramen", metricName: "Interest Over Time",
        timestampDate: {date: {year: 2019, month: 12, day: 29}}
      },
      { dataPoints: {"2019-10-27": {value: 53}, }, 
        dimensionName: "Ramen", metricName: "Interest Over Time",
        timestampDate: {date: {year: 2019, month: 12, day: 1}}
      },
    ],
    id: {
      isPresent: true,
      value: 1234567890123456,
    },
    status: "UNRESOLVED",
    timestampDate: {date: {year: 2019, month: 12, day: 8}},
  }, 
  {
    anomalies: [
      { dataPoints: {"2019-10-24": {value: 46}, }, 
        dimensionName: "Ramen", metricName: "Interest Over Time",
        timestampDate: {date: {year: 2019, month: 11, day: 27}}
      },
    ],
    id: {
      isPresent: true,
      value: 1987654321098765,
    },
    status: "RESOLVED",
    timestampDate: {date: {year: 2019, month: 12, day: 27}},
  }];

  // Expected allAlerts, unchecked, and checked.
  const expectedAlerts = new Map();
  expectedAlerts.set(1234567890123456, {
    timestamp: "Sun Dec 08 2019",
    anomalies: 2,
  });
  expectedAlerts.set(1987654321098765, {
    timestamp: "Fri Dec 27 2019",
    anomalies: 1,
  });
  const expectedUnchecked = [1234567890123456];
  const expectedChecked = [1987654321098765]

  beforeEach(() => {
    fetch.resetMocks();
  });

  it("correctly sets alert information in state based on alert data", async () => {
    fetch.mockResponseOnce(JSON.stringify(fakeAlerts)); 

    let component;
    await act(async () => {
      component = shallow(<AlertsContent classes={styles} />);
    });
    
    component.update()
    expect(component.state('allAlerts')).toEqual(expectedAlerts);
    expect(component.state('unchecked')).toEqual(expectedUnchecked);
    expect(component.state('checked')).toEqual(expectedChecked);
  });
});
