import React from "react";
import { render, unmountComponentAtNode } from "react-dom";
import { act } from "react-dom/test-utils";
import { shallow, configure} from "enzyme";
import Adapter from 'enzyme-adapter-react-16';
import { createShallow } from '@material-ui/core/test-utils';
import { PureAlertsContent as AlertsContent } from "./AlertsContent";
import AlertsList from "./AlertsList";

configure({ adapter: new Adapter(), disableLifecycleMethods: true });

describe("handleTabs", () => {
  const tabLabels = {
    UNRESOLVED: 0,
    RESOLVED: 1,
    ALL: 2,
  };

  const styles = { 
    root: {
      flexGrow: 1,
      maxWidth: 900,
      margin: 'auto',
    }
  };

  it("should display the right tabpanel when the tab is changed", () => {
    const wrapper = shallow(<AlertsContent classes={styles} />);
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
  const fakeUnresolved = [0, 1];
  const fakeResolved = [2];

  // Expected mock data.
  const editUnchecked = [1];
  const editChecked = [2, 0];
  const doubleChecked = [0, 2];

  const styles = { 
    root: {
      flexGrow: 1,
      maxWidth: 900,
      margin: 'auto',
    }
  };

  let wrapper;

  beforeEach(() => {
    wrapper = shallow(<AlertsContent classes={styles} />);
    wrapper.setState({ 
      allAlerts: fakeAlerts,
      unchecked: fakeUnresolved,
      checked: fakeResolved,
    });
  });

  afterEach(() => {
    wrapper.unmount();
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

  // TODO: write test for sending POST request to servlet
});

// TODO: write tests for fetching alerts (in componentDidMount).
describe("fetch alerts", () => {

});
