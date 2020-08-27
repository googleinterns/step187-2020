import { getAlertsData, getSpecificAlertData } from "./management_helpers";
import { enableFetchMocks } from 'jest-fetch-mock';
enableFetchMocks();

describe("getAlertsData", () => {
  const fakeAlerts = [{
    anomalies: [
      { dataPoints: {"2019-11-24": {value: 79}, }, 
        dimensionName: "Ramen", metricName: "Interest Over Time",
        timestampDate: {date: {year: 2019, month: 12, day: 29}},
        relatedDataList: [{
          dataPoints: {"2019-11-24": {value: 46}, }, 
          dimensionName: "Udon", metricName: "Interest Over Time",
          username: "bob@",
          },
        ]
      },
      { dataPoints: {"2019-10-27": {value: 53}, }, 
        dimensionName: "Ramen", metricName: "Interest Over Time",
        timestampDate: {date: {year: 2019, month: 12, day: 1}},
        relatedDataList: [{
          dataPoints: {"2019-10-27": {value: 56}, }, 
          dimensionName: "Udon", metricName: "Interest Over Time",
          username: "bob@",
          },
        ]
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
        timestampDate: {date: {year: 2019, month: 11, day: 27}},
        relatedDataList: [{
          dataPoints: {"2019-10-24": {value: 78}, }, 
          dimensionName: "Udon", metricName: "Interest Over Time",
          username: "bob@",
          },
        ]
      },
    ],
    id: {
      isPresent: true,
      value: 1987654321098765,
    },
    status: "RESOLVED",
    timestampDate: {date: {year: 2019, month: 12, day: 27}},
  }];

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

  beforeEach(() => {
    fetch.resetMocks();
  });

  it("correctly organizes alert information into data structures based on alert data", async () => {
    fetch.mockResponseOnce(JSON.stringify(fakeAlerts)); 

    const results = await getAlertsData();

    expect(results).toMatchObject([expectedAlerts, expectedUnchecked, expectedChecked])
  });
});

describe("getSpecificAlertData", () => {
  const fakeAlert = {
    anomalies: [
      { dataPoints: {"2019-10-24": {value: 46}, }, 
        dimensionName: "Ramen", metricName: "Interest Over Time",
        timestampDate: {date: {year: 2019, month: 11, day: 27}},
        relatedDataList: [{
            dataPoints: {"2019-10-24": {value: 78}, }, 
            dimensionName: "Udon", metricName: "Interest Over Time",
            username: "bob@",
          },
        ]
      },
    ],
    id: {
      isPresent: true,
      value: 1987654321098765,
    },
    status: "RESOLVED",
    timestampDate: {date: {year: 2019, month: 12, day: 27}},
  };

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

  const REQUEST_ID = 1987654321098765;

  beforeEach(() => {
    fetch.resetMocks();
  });

  it("returns the requested alert information", async () => {
    fetch.mockResponseOnce(JSON.stringify(fakeAlert)); 

    const result = await getSpecificAlertData(REQUEST_ID);

    expect(result).toMatchObject(expectedAlert);
  });
});
