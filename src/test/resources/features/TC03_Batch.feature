@BatchModule
Feature: Validate Batch Module

Background: 
Given Admin sets authorization to Bearer Token

Rule: Create Batch (POST)

  Scenario Outline: Verify POST Batch creation "<ScenarioName>" 
    Given Admin creates POST batch data for "<ScenarioName>" from excel sheet
    When Admin sends HTTPS request to the endpoint 
    Then Admin receives expected status code from excel, validate POST batch response

    Examples:
      | ScenarioName                    |
      | Mandatory_Optional_Fields       |
      | Mandatory_Only_Fields			|
      | Optional_Only_Fields			|
      | ProgramName_Mismatch            |
      | Inactive_ProgramId              |
      | ProgramId_NotExist              |
      | BatchName_No_Underscore         |
      | BatchName_Hyphen                |
      | BatchName_Char_Suffix           |
      | BatchName_Special_Suffix        |
      | BatchName_Too_Long              |
      | BatchName_Too_Short             |
      | BatchName_Duplicate             |
      | Description_Too_Short           |
      | Description_Too_Long            |
      | Status_Random_Chars             |
      | Status_Numbers                  |
      | Status_Special                  |
      | Classes_NonNumeric              |
      | Classes_Too_Small               |
      | Classes_Too_Large               |
      | Invalid_Endpoint                |
      | Invalid_Method                  |
      | Invalid_ContentType             |
      | No_Authorization                |
      
Rule: Get all batches(GET)
  
  Scenario Outline: Verify GET all batches "<ScenarioName>"
    Given Admin creates GET request for "<ScenarioName>"
    When Admin sends HTTPS request to the endpoint
    Then Admin receives expected status code from Excel
  
  	Examples:
	  	| ScenarioName	 				|
	  	| Get_All_Valid					|
	  	| Get_All_Invalid_EP			|
		| Get_All_Invalid_ContentType	|
	  	| Get_All_No_Auth				|
	  	| Get_All_Invalid_Method		|
  
Rule: Get Batch By Id(GET)
  
  Scenario Outline: Verify GET batch by id "<ScenarioName>"
	  Given Admin creates GET batch by id request for "<ScenarioName>"
	  When Admin sends HTTPS request to the endpoint
	  Then Admin receives expected status code from Excel
  
	  Examples:
	  	| ScenarioName	 						|
	  	| Get_Batch_By_Id_Valid					| 

Rule: Get Batch By Name(GET)
  Scenario Outline: Verify GET batch by name "<ScenarioName>"
	  Given Admin creates GET batch by name request for "<ScenarioName>"
	  When Admin sends HTTPS request to the endpoint
	  Then Admin receives expected status code from Excel
  
	  Examples:
	  	| ScenarioName	 						|
	  	| Get_Batch_By_Name_Valid					| 

Rule: Get Batch By ProgramId(GET) 	
   Scenario Outline: Verify GET batch by programid "<ScenarioName>"
	  Given Admin creates GET batch by programid request for "<ScenarioName>"
	  When Admin sends HTTPS request to the endpoint
	  Then Admin receives expected status code from Excel
  
	  Examples:
	  	| ScenarioName	 						|
	  	| Get_Batch_By_ProgramId_Valid					| 

Rule: Update Batch By BatchId (PUT)  	
   Scenario Outline: Verify PUT batch by id "<ScenarioName>"
	  Given Admin creates PUT batch by id request for "<ScenarioName>"
	  When Admin sends HTTPS request to the endpoint
	  Then Admin receives expected status code from Excel
  
	  Examples:
	  	| ScenarioName	 						|
	  	| Update_Batch_By_Id_Valid					| 
	  	
Rule: Delete Batch By BatchId (DELETE)  	
   Scenario Outline: Verify DELETE batch by id "<ScenarioName>"
	  Given Admin creates DELETE batch by id request for "<ScenarioName>"
	  When Admin sends HTTPS request to the endpoint
	  Then Admin receives expected status code from Excel
  
	  Examples:
	  	| ScenarioName	 						|
	  	| Delete_Batch_By_Id_Valid				|	