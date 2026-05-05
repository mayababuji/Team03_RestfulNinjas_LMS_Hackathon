@programmodule
Feature: Validate Program Module


  Scenario Outline: Verify if admin is able to create a Program
    Given Admin has a valid authorization token set
    When Admin sends POST request to create program with different payload for "<ScenarioName>" from dataSheet
    Then Admin verifies the response payload with expected output from the data sheet

    Examples:
      | ScenarioName                                                    |
    |CreateProgram_Invalid_Endpoint                              |
      | CreateProgram_InvalidMethod                                |
      | CreateProgram_ProgramName_Less_Than_4_Characters            |
      | CreateProgram_ProgramName_Greater_Than_25_Characters        |
      | CreateProgram_ProgramDescription_Less_Than_4_Characters     |
      | CreateProgram_ProgramDescription_Greater_Than_25_Characters |
      |CreateProgram_with_Missing_ProgramName                   |
      |CreateProgram_with_Missing_ProgramStatus                     |
      |CreateProgram_with_Special Characters_ProgramName            |
      |CreateProgram_with_trailing_space_ProgramName                |
      | Create_NewProgram                                               |

  Scenario Outline: Verify if Admin  updates a ProgramByProgramId
    Given Admin has a valid authorization token set
    When Admin sends PUT request to update programById with payload for "<ScenarioName>"
    Then Admin verifies the response payload for Update Program ByProgramId

    Examples:
      | ScenarioName                                                       |
      |UpdateProgramById_with_Valid_ProgramName  |
      |UpdateProgramByID_InvalidMethod|
      |UpdateProgramById_Invalid_Endpoint|
    |UpdateProgramById_InvalidProgramId|
    |UpdateProgramById_ProgramName_LessThan_4_Characters|
    |UpdateProgramById_ProgramDescription_LessThan_4_Characters|

  Scenario Outline: Verify if Admin retrieves a program with valid program ID
    Given Admin has a valid authorization token set
    When Admin sends GET request to get program with payload for "<ScenarioName>"
    Then Admin recieves the response payload with expected output from the excel sheet for Get Program

    Scenarios:
      | ScenarioName                          |
      | GetProgramById_Valid_ProgramId  |
    |GetProgramById_Invalid_Endpoint|
    |GetProgramById_Invalid_Method|
    |GetProgramById_Invalid_ProgramId|

  Scenario Outline: Verify if admin for GET All Programs
    Given Admin has a valid authorization token set
    When Admin sends GET request to get all programs for "<ScenarioName>"
    Then Admin verifies the response payload with expected output for Get All Programs

    Examples:
      | ScenarioName                      |
      | GetPrograms_Invalid_Endpoint |
    |GetPrograms_Invalid_Method    |
    |Get_All_Programs              |

  Scenario Outline: Verify if Admin retrieves all programs users
    Given Admin has a valid authorization token set
    When Admin sends GET request to get all programs for users with "<ScenarioName>"
    Then Admin verifies the response payload with expected output for Get All Programs with Users

    Examples:
      | ScenarioName                                  |
      | GetAllProgramsForUsers_Invalid_Endpoint |
      | GetAllProgramsWithUsers_Valid_Endpoint               |
      | GetPrograms_Invalid_Method   |



