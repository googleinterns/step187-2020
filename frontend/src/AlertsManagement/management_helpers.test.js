import { getAlertsData } from "./management_helpers";
import { enableFetchMocks } from 'jest-fetch-mock';
enableFetchMocks();

describe("fetch alerts", () => {
  // Fake alert JSON data. TODO: add ID property in the future.
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
    status: "RESOLVED",
    timestampDate: {date: {year: 2019, month: 12, day: 27}},
  }];

  // Expected allAlerts, unchecked, and checked.
  const expectedAlerts = new Map();
  expectedAlerts.set(0, {
    timestampDate: "Sun Dec 08 2019",
    anomalies: [{
      dataPoints: new Map([ ["2019-11-24", 79], ]), 
      dimensionName: "Ramen", metricName: "Interest Over Time",
      timestampDate: "Sun Dec 29 2019"
    },
    { 
      dataPoints: new Map([ ["2019-10-27", 53] ]), 
      dimensionName: "Ramen", metricName: "Interest Over Time",
      timestampDate: "Sun Dec 01 2019",
    }],
    status: "UNRESOLVED",
  });
  expectedAlerts.set(1, {
    timestampDate: "Fri Dec 27 2019",
    anomalies: [{ 
      dataPoints: new Map([ ["2019-10-24", 46] ]), 
      dimensionName: "Ramen", metricName: "Interest Over Time",
      timestampDate: "Wed Nov 27 2019"
    }],
    status: "RESOLVED",
  });
  const expectedUnchecked = [0];
  const expectedChecked = [1]

  beforeEach(() => {
    fetch.resetMocks();
  });

  it("correctly organizes alert information into data structures based on alert data", async () => {
    fetch.mockResponseOnce(JSON.stringify(fakeAlerts)); 

    const results = await getAlertsData();

    expect(results).toMatchObject([expectedAlerts, expectedUnchecked, expectedChecked])
  });
});
