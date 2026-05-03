@BatchModule
Feature: Validate Batch Module


  Scenario Outline: Verify admin creates batch with both mandatory and optional fields
    Given Admin creates POST batch data for "<ScenarioName>" from excel sheet
    When Admin sends HTTPS request to the endpoint 
    Then Admin receives expected status code from excel, validate POST batch response

    Examples:
      | ScenarioName                                  |
      | Mandatory_Optional_Valid              	      |