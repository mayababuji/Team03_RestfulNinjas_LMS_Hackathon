@BatchModule
Feature: Validate Batch Module


  Scenario Outline: Check if Admin is able to create batch with valid batch name and description
    Given Admin create POST request with valid data for "<scenario>" from excel sheet
    When Admin sends POST request to create program batch
    Then Admin receives created status with response body

    Examples:
      | scenario                                  |
      | CreateBatch_Valid_batchName               |