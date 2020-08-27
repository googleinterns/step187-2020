import React from "react";
import { act } from "react-dom/test-utils";
import { shallow, configure} from "enzyme";
import Adapter from 'enzyme-adapter-react-16';
import { enableFetchMocks } from 'jest-fetch-mock';
enableFetchMocks();
import AlertInfo from "./AlertInfo";
import { UNRESOLVED_STATUS } from './management_constants';
import * as helpers from './management_helpers';

configure({ adapter: new Adapter() });

// Mock result alert data.
const expectedAlert = {
  id: 1987654321098765, 
  timestampDate: "Fri Dec 27 2019",
  anomalies: [{ 
    dataPoints: new Map([ ["2019-10-24", 46] ]), 
    dimensionName: "Ramen", metricName: "Interest Over Time",
    timestampDate: "Wed Nov 27 2019",
    relatedDataList: [{
      dataPoints: new Map([ ["2019-10-24", 78] ]),
      dimensionName: "Udon", metricName: "Interest Over Time",
      username: "bob@",
      },
    ]
  }],
  status: "RESOLVED",
};

describe("componentDidMount", () => {
  
  const REQUEST_ID = 1987654321098765;

  it("should set state correctly with fetched alert", async () => {
    const mock = jest.spyOn(helpers, "getSpecificAlertData").mockReturnValue(expectedAlert);
    const wrapper = shallow(<AlertInfo />, { disableLifecycleMethods: true });
    wrapper.setProps({
      match: {
        params: {
          alertId: REQUEST_ID,
        }
      }
    });

    await wrapper.instance().componentDidMount();

    expect(mock).toHaveBeenCalled();
    expect(wrapper.state('alert')).toMatchObject(expectedAlert);
  });

  it("should throw an error when no data is received", async () => {
    const mock = jest.spyOn(helpers, "getSpecificAlertData").mockReturnValue(null);
    const wrapper = shallow(<AlertInfo />, { disableLifecycleMethods: true });
    wrapper.setProps({
      match: {
        params: {
          alertId: REQUEST_ID,
        }
      }
    });
    const instance = wrapper.instance();

    expect(mock).toHaveBeenCalled();
    await expect(instance.componentDidMount()
    ).rejects.toEqual(new Error("Could not find alert with id " + REQUEST_ID));
  });
})

describe("handleStatusChange", () => {

  const SERVLET_ROUTE = '/api/v1/alerts-data';
  const POST_DATA = {"body": "1987654321098765 UNRESOLVED", "method": "POST",};

  beforeEach(() => {
    fetch.resetMocks();
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  it("sends a POST request with the correct data to change alert status", () => {
    jest.spyOn(global, 'fetch');
    const wrapper = shallow(<AlertInfo />, { disableLifecycleMethods: true });
    wrapper.setState({ alert: expectedAlert });

    act(() => {
      wrapper.find('#status-button').simulate('click');
    });

    expect(global.fetch).toHaveBeenCalledTimes(1);
    expect(global.fetch).toHaveBeenCalledWith(SERVLET_ROUTE, POST_DATA);
    expect(wrapper.state('alert').status).toEqual(UNRESOLVED_STATUS);
  });

  // TODO: write tests for dealing with error response from backend (e.g. 404 code).
});
