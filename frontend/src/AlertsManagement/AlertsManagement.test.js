import React from "react";
import { act } from "react-dom/test-utils";
import { shallow, configure } from "enzyme";
import Adapter from 'enzyme-adapter-react-16';
import AlertsManagement from "./AlertsManagement";
import { PureAlertsContent as AlertsContent } from "./AlertsContent";
import AlertsList from "./AlertsList";
import { tabLabels } from './management_constants';

configure({ adapter: new Adapter() });

const styles = { 
  root: {
    flexGrow: 1,
    maxWidth: 900,
    margin: 'auto',
  }
};

describe("handleTabs", () => {
  it("should display the right tabpanel when the tab is changed", () => {
    const wrapper = shallow(<AlertsManagement />, { disableLifecycleMethods: true });
    wrapper.setState({ tab: tabLabels.ALL }); 
    const child = shallow(<AlertsContent classes={styles} tab={wrapper.state('tab')}
      handleTabs={wrapper.instance().handleTabs} unchecked={[]} checked={[]} allAlerts={new Map()} 
    />);
    
    act(() => {
      const tabs = child.find('WithStyles(ForwardRef(Tabs))');
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

  let wrapper;
  let child;

  beforeEach(() => {
    wrapper = shallow(<AlertsManagement />, { disableLifecycleMethods: true });
    wrapper.setState({ 
      tab: tabLabels.UNRESOLVED,
      allAlerts: fakeAlerts,
      unchecked: fakeUnresolvedIds,
      checked: fakeResolvedIds,
    });
    child = shallow(<AlertsContent classes={styles} 
      tab={wrapper.state('tab')} allAlerts={wrapper.state('allAlerts')} 
      unchecked={wrapper.state('unchecked')} checked={wrapper.state('checked')} 
      handleTabs={wrapper.instance().handleTabs}
      handleCheckbox={wrapper.instance().handleCheckbox} 
    />);
  });

  afterEach(() => {
    wrapper.unmount();
    child.unmount();
  })

  it("should maintain the correct unresolved and resolved elements after click", () => {
    act(() => {
      const boxes = child.find(AlertsList).at(0).dive().find('WithStyles(ForwardRef(Checkbox))');
      boxes.at(0).simulate('click');
    });

    expect(wrapper.state('unchecked')).toEqual(editUnchecked);
    expect(wrapper.state('checked')).toEqual(editChecked);
  });

  it("should throw error if alert is considered both unresolved and resolved", () => {
    wrapper.setState({ checked: doubleChecked });
    child.setProps({ checked: wrapper.state('checked') })

    const boxes = child.find(AlertsList).at(0).dive().find('WithStyles(ForwardRef(Checkbox))');
    function testCheckbox() { boxes.at(0).simulate('click'); }

    expect(testCheckbox).toThrowError('Misplaced alert');
  });

  it("should throw error if alert is neither unresolved nor resolved", () => {
    const boxes = child.find(AlertsList).at(0).dive().find('WithStyles(ForwardRef(Checkbox))');
    wrapper.setState({ unchecked: editUnchecked });
    function testCheckbox() { boxes.at(0).simulate('click'); }

    expect(testCheckbox).toThrowError('Misplaced alert')
  });

  // TODO: write test for sending POST request to servlet
});

