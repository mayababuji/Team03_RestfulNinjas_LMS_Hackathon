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

  Scenario Outline: Verify if admin for GET All Programs
    Given Admin has a valid authorization token set
    When Admin sends GET request to get all programs for "<ScenarioName>"
    Then Admin verifies the response payload with expected output for Get All Programs

    Examples:
      | ScenarioName                      |
      | GetPrograms_Invalid_Endpoint |
    |GetPrograms_Invalid_Method    |
    |Get_All_Programs              |
