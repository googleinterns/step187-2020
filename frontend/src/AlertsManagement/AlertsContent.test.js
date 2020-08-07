import React from "react";
import { render, unmountComponentAtNode } from "react-dom";
import { act } from "react-dom/test-utils";
import { shallow, configure} from "enzyme";
import Adapter from 'enzyme-adapter-react-16';
import { createShallow } from '@material-ui/core/test-utils';
import { PureAlertsContent as AlertsContent } from "./AlertsContent";
import AlertsList from "./AlertsList";

configure({ adapter: new Adapter() });

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
  const editUnchecked = [1, 2, 3];
  const editChecked = [4, 5, 6, 0];
  const missingChecked = [4, 5, 6];
  const doubleChecked = [0, 4, 5, 6];

  const styles = { 
    root: {
      flexGrow: 1,
      maxWidth: 900,
      margin: 'auto',
    }
  };

  it("should maintain the correct unresolved and resolved elements after click", () => {
    const wrapper = shallow(<AlertsContent classes={styles} />);

    act(() => {
      const boxes = wrapper.find(AlertsList).at(0).dive().find('WithStyles(ForwardRef(Checkbox))');
      boxes.at(0).simulate('click');
    });

    expect(wrapper.state('unchecked')).toEqual(editUnchecked);
    expect(wrapper.state('checked')).toEqual(editChecked);
  });

  it("should throw error if alert is considered both unresolved and resolved", () => {
   const wrapper = shallow(<AlertsContent classes={styles} />);
    wrapper.setState({ checked: doubleChecked });
    const boxes = wrapper.find(AlertsList).at(0).dive().find('WithStyles(ForwardRef(Checkbox))');

    function testCheckbox() { boxes.at(0).simulate('click'); }

    expect(testCheckbox).toThrowError('Misplaced alert')
  });

  it("should throw error if alert is neither unresolved nor resolved", () => {
    const wrapper = shallow(<AlertsContent classes={styles} />);
    wrapper.setState({ checked: missingChecked });
    const boxes = wrapper.find(AlertsList).at(0).dive().find('WithStyles(ForwardRef(Checkbox))');
    wrapper.setState({ unchecked: editUnchecked });

    function testCheckbox() { boxes.at(0).simulate('click'); }

    expect(testCheckbox).toThrowError('Misplaced alert')
  });

  // TODO: write test for sending POST request to servlet
});
