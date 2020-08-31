/** 
 * When authOn is true, the application will send a GET request to the LoginServlet
 * to get the login status and will only show the Alerts and Configs tabs if the user is
 * logged in. Set to true if authentication is desired.
 * When authOn is false, the application will not send any GET requests to the LoginServlet
 * and will show the Alerts and Configs tabs regardless of authentication.
 */
export const authOn = true;
