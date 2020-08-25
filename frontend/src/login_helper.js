/**
 * Fetch login status from backend. Return Object containing login status and login or logout URL.
 */
export async function getLoginStatus() {
  const loginResponse = await fetch("/api/v1/login").then(response => response.json());
  return ({
    isLoggedIn: loginResponse.isLoggedIn,
    logURL: loginResponse.logURL,
  });
}
