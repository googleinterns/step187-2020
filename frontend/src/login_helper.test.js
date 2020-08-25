import { getLoginStatus } from "./login_helper";
import { enableFetchMocks } from 'jest-fetch-mock';
enableFetchMocks();

describe("fetch login status", () => {
  // Fake login status.
  const fakeStatus = {
    isLoggedIn: true,
    logURL: "/"
  };

  beforeEach(() => {
    fetch.resetMocks();
  });

  it("correctly returns login status information", async () => {
    fetch.mockResponseOnce(JSON.stringify(fakeStatus)); 

    const results = await getLoginStatus();

    expect(results).toMatchObject(fakeStatus)
  });
});
