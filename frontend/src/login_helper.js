/**
 * Fetch login status from backend. Return Object containing login status and login or logout URL.
 */
export async function getLoginStatus() {
  const loginResponse = await fetch("/api/v1/login");
  if (!loginResponse.ok) throw new Error('Error getting login status: ' + loginResponse.status);
  const data = await loginResponse.json();
  return ({
    isLoggedIn: data.isLoggedIn,
    logURL: data.logURL
  });
}
