@LoginModule
Feature: Verify User Login Controller


  Scenario Outline: Verify User Sign In with No Auth
    Given Admin has the test data for "<ScenarioName>" from Excel with No Auth
    When Admin sends the post request for User Sign In
    Then Admin should receive the status code as defined in Excel
    Then the response should match the expected validation message from Excel

    Examples:
      | ScenarioName                      |
      | Postrequest_Valid credential      |