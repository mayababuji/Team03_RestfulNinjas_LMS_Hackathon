@LoginModule
Feature: Validate User Login Module


  Scenario Outline: Verify User Sign In with No Auth
    Given Admin has the test data for "<ScenarioName>" from Excel with No Auth
    When Admin sends the post request for  Sign In
    Then Admin should receive the status code as  in Excel
    Then the response should match the expected validation message from Excel

    Examples:
      | ScenarioName                      |
      |Valid credential                   |
      |InvalidContentType                 |
      |InvalidBaseURL                     |
      |InvalidEndPoint                    |
      |InvalidBaseURL                     |
      |SpecialCharEmail                   |
      |SpecialCharPassword                |
      |CaseSensitivePassword              |
      |CaseSensitiveEmail                 |
    |NumbersEmail                       |
    |NullPassword                       |
|NumberPassword                    |
|NullEmail                          |
    |NullBody                           |

  Scenario: Verify login with Invalid Method
    Given Admin has the test data for "InvalidMethod" from Excel with No Auth
    When Admin sends a GET  InvalidMethod
    Then Admin should receive the status code matches with Expected statuscode from excel

  Scenario Outline: Verify Forgot Password functionality
    Given Admin has the test data for "<ScenarioName>" from Excel with No Auth
    When Admin sends the post request for ForgotPassword
    Then Admin should receive the status code as  in Excel
    Then the response should match the expected validation message from excel

    Examples:
      | ScenarioName                      |
      | ForgotPwd_ValidCredential    |
      | ForgotPwd_InvalidEndpoint    |
      | ForgotPwd_Unregistered  |
      | ForgotPwd_InvalidAdminEmail  |
      | ForgotPwd_NullBody           |
      | ForgotPwd_InvalidContentType |

  Scenario: Verify  if Admin logs out with invalid token
    Given Admin has the test data for "LogoutWithInvalidToken" from Excel
    When Admin sends GET for logoutInvalidMethod
    Then Admin should receive the status code matches with Expected statuscode from excel

  Scenario: Verify Admin logs out with no authorization
    Given Admin has the test data for "Logout_With_NoAuth" from Excel with No Auth
    When Admin sends a GET HTTPS request for Logout  with No auth
    Then Admin should receive the status code matches with Expected statuscode from excel

  Scenario: Verify Admin logs out with invalid method
    Given Admin has the test data for "LogoutWithInvalidToken" from Excel
    When Admin sends a POST request for Logout
    Then Admin should receive the status code matches with Expected statuscode from excel

  Scenario: Verify Admin resets password with invalid method
    Given Admin has the test data for "Reset_Password_Invalid_Method" from Excel
    When Admin sends a GET request for Reset Password Reset_Password_Invalid_Method
    Then Admin should receive the status code matches with Expected statuscode from excel



