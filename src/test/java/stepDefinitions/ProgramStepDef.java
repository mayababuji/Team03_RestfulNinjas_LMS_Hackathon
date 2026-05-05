package stepDefinitions;

import io.restassured.path.json.JsonPath;
import org.hamcrest.Matchers;
import pojo.CreateProgramRequest;
import pojo.CreateProgramResponse;
import httpRequest.ProgramRequestParser;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.Assert;
import specBuilder.RequestSpec;
import utils.ExcelReader;
import utils.SharedTestData;


import java.io.IOException;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.testng.Assert.assertTrue;



public class ProgramStepDef extends SharedTestData {

    private RequestSpecification requestSpec;
    private Map<String, String> data;
    private Response response;
    private static CreateProgramRequest programInput;


    @Given("Admin has a valid authorization token set")
    public void admin_has_a_valid_authorization_token_set() {
        requestSpec = RequestSpec.getRequestSpec();
    }

    @When("Admin sends POST request to create program with different payload for {string} from dataSheet")
    public void admin_sends_post_request_to_create_program_with_different_payload_for_from_data_sheet(
            String scenarioNameFeature) throws IOException {
        System.out.println("Scenario FEATURE IS Name: " + scenarioNameFeature);

        data = ExcelReader.readExcelData("Program", scenarioNameFeature);

        if (data == null) {
            throw new RuntimeException("Test data not found for: " + scenarioNameFeature);
        }

        if (!scenarioNameFeature.equalsIgnoreCase(data.get("ScenarioName"))) {
            return;
        }

        // Parse request body
        programInput = ProgramRequestParser.createProgramParseData(data.get("Body"));


// Handle missing programName scenario
        if (scenarioNameFeature.equalsIgnoreCase("CreateProgram_with_Missing_ProgramName")) {
            programInput.setProgramName(null);
        } else {
            // Generate unique program name
            String uniqueProgramName = programInput.getProgramName() + RandomStringUtils.randomAlphabetic(2);
            programInput.setProgramName(uniqueProgramName);
            SharedTestData.programName = uniqueProgramName;
        }

        // Build request
        requestSpec = given().spec(requestSpec).body(programInput);

        // Unified dynamic endpoint logic
        String httpMethod = data.get("Method");
        String endPoint = data.get("Endpoint");

        if (endPoint.contains("{programId}")) {
            endPoint = endPoint.replace("{programId}", String.valueOf(SharedTestData.programId));
        }

        // Send request
        response = requestSpec.log().all()
                .when().request(httpMethod, endPoint)
                .then().log().all()
                .extract().response();
    }


    @Then("Admin verifies the response payload with expected output from the data sheet")
    public void admin_verifies_the_response_payload_with_expected_output_from_the_data_sheet() {

        int expectedStatus = Integer.parseInt(data.get("ExpectedStatusCode"));
        response.then().log().all().statusCode(expectedStatus);

        if (expectedStatus != 201) {
            validateStatus(expectedStatus);
            return;
        }

        // Schema validation
        response.then().assertThat()
                .body(matchesJsonSchemaInClasspath("schemas/Program/CreateProgramSchema.json"));

        // Deserialize response
        CreateProgramResponse actualResponse = response.as(CreateProgramResponse.class);

        // Store programName & programId globally
        SharedTestData.programName = actualResponse.getProgramName();
        SharedTestData.programNameList.add(actualResponse.getProgramName());
        System.out.println("##########SharedTestData.programName in createPROGRAM##########");
        System.out.println(SharedTestData.programName);
        System.out.println("##########SharedTestData.programName in createPROGRAM##########");
        System.out.println("##########SharedTestData.programIdList in createPROGRAM##########");
        System.out.println(SharedTestData.programIdList);
        System.out.println("##########SharedTestData.programIdList in createPROGRAM##########");


        int createdId = actualResponse.getProgramId();

// Always store the latest programId
        SharedTestData.programId = createdId;

// Always add to the list
        SharedTestData.programIdList.add(createdId);

        System.out.println("Saved Program ID: " + createdId);
        System.out.println("All Program IDs: " + SharedTestData.programIdList);


        // Field validations
        Assert.assertEquals(actualResponse.getProgramDescription(), programInput.getProgramDescription(),
                "ProgramDescription is not matching");

        Assert.assertEquals(actualResponse.getProgramName(), programInput.getProgramName(),
                "ProgramName is not matching");

        assertTrue(actualResponse.getProgramId() > 0, "ProgramId should not be negative value");
    }

    private void validateStatus(int expectedStatus) {

        String expectedMsg = data.get("ExpectedMessage");
        switch (expectedStatus) {
            case 400:
                String message = response.jsonPath().getString("message");

                if (message != null) {

                    Assert.assertEquals(message, expectedMsg);
                } else {

                    Map<String, String> bodyMap = response.jsonPath().getMap("");
                    String firstValue = bodyMap.values().iterator().next();
                    System.out.println("firstValuefirstValuefirstValue");
                    System.out.println(firstValue);
                    System.out.println("firstValuefirstValuefirstValue");
                    System.out.println("expectedMsg");
                    System.out.println(expectedMsg);
                    System.out.println("expectedMsg");
                    Assert.assertEquals(firstValue, expectedMsg);
                }
                break;

            case 405:
                Assert.assertEquals(response.jsonPath().getString("message"), expectedMsg);
                break;

            case 404:
            default:
                break;
        }
    }

    @When("Admin sends GET request to get all programs for {string}")
    public void admin_sends_get_request_to_get_all_programs_for(
            String scenarioNameFromFeature) throws IOException {

        String scenarioName = scenarioNameFromFeature;
        data = ExcelReader.readExcelData("Program", scenarioName);

        if (data != null) {
            String dataSheetTestname = data.get("ScenarioName");

            if (scenarioNameFromFeature.equalsIgnoreCase(dataSheetTestname)) {
                requestSpec = given().spec(requestSpec);

                String httpMethod = data.get("Method");
                String endPoint = data.get("Endpoint");

                response = requestSpec.log().all().when().request(httpMethod, endPoint).then().log().all().extract()
                        .response();
            }
        } else {
            throw new RuntimeException("Test data not found for: " + scenarioNameFromFeature);
        }
    }

    @Then("Admin verifies the response payload with expected output for Get All Programs")
    public void admin_verifies_the_response_payload_with_expected_output_for_get_all_programs() {

        int expectedStatusCode = Integer.parseInt(data.get("ExpectedStatusCode"));


        response.then().log().all().statusCode(expectedStatusCode);


        if (expectedStatusCode != 200) {

            String expectedMsg = data.get("ExpectedMessage").trim();
            String actualBody = response.getBody().asString().trim();


            Assert.assertTrue(
                    actualBody.contains(expectedMsg),
                    "Expected message: " + expectedMsg + " but got: " + actualBody
            );

            return;
        }


        response.then()
                .body(matchesJsonSchemaInClasspath("schemas/Program/GetAllProgramsSchema.json"))
                .body("", Matchers.instanceOf(List.class))
                .body("size()", Matchers.greaterThan(0));


        JsonPath json = response.jsonPath();
        List<Map<String, Object>> array = json.getList("$");

        int programIdCount = 0;
        for (Map<String, Object> element : array) {
            int resProgram = (Integer) element.get("programId");
            if (programIdList.contains(resProgram)) {
                programIdCount++;
            }
        }

        Assert.assertEquals(programIdCount, programIdList.size());
    }

    @When("Admin sends GET request to get program with payload for {string}")
    public void admin_sends_get_request_to_get_program_with_payload_for(
            String scenarioNameFromFeature) throws IOException {
        String scenario = scenarioNameFromFeature;
        data = ExcelReader.readExcelData("Program", scenario);
        if (data != null) {
            String dataSheetTestname = data.get("ScenarioName");

            if (scenario.equalsIgnoreCase(dataSheetTestname)) {

                requestSpec = given().spec(requestSpec);

                String httpMethod = data.get("Method");
                String endPoint = data.get("Endpoint");
                if (endPoint.contains("{programId}")) {
                    endPoint = endPoint.replace("{programId}", String.valueOf(SharedTestData.programId));
                }
                response = requestSpec.log().all().when().request(httpMethod, endPoint).then().log().all().extract()
                        .response();

            }
        } else {
            throw new RuntimeException("Test data not found for: " + scenario);
        }


    }

    @Then("Admin recieves the response payload with expected output from the excel sheet for Get Program")
    public void admin_recieves_the_response_payload_with_expected_output_from_the_excel_sheet_for_get_program() {
        int expectedStatus = Integer.parseInt(data.get("ExpectedStatusCode"));
        String expectedMessage = data.get("ExpectedMessage");
        response.then().log().all().statusCode(expectedStatus);
        if (expectedStatus == 200) {
            response.then().assertThat()
                    .body(matchesJsonSchemaInClasspath("schemas/Program/GetAllByProgramID.json"));

            int actualId = response.jsonPath().getInt("programId");
            Assert.assertEquals(actualId, SharedTestData.programId);
        } else if (expectedStatus == 404) {
            response.then().statusLine(Matchers.containsString(expectedMessage));
        }
    }

    @When("Admin sends GET request to get all programs for users with {string}")
    public void admin_sends_get_request_to_get_all_programs_with_users_for_from_data_sheet(
            String scenarioNameFromFeature) throws IOException {

        String scenarioName = scenarioNameFromFeature;
        data = ExcelReader.readExcelData("Program", scenarioName);

        if (data != null) {
            String dataSheetTestname = data.get("ScenarioName");

            if (scenarioNameFromFeature.equalsIgnoreCase(dataSheetTestname)) {
                requestSpec = given().spec(requestSpec);
                String httpMethod = data.get("Method");
                String endPoint = data.get("Endpoint");
                response = requestSpec.log().all().when().request(httpMethod, endPoint).then().log().all().extract()
                        .response();
            }
        } else {
            throw new RuntimeException("Test data not found for: " + scenarioNameFromFeature);
        }
    }
    @Then("Admin verifies the response payload with expected output for Get All Programs with Users")
    public void admin_verifies_the_response_payload_with_expected_output_for_get_all_programs_with_users() {
        response.then().log().all();
        int expectedStatus = Integer.parseInt(data.get("ExpectedStatusCode"));
        Assert.assertEquals(response.getStatusCode(), expectedStatus, "Status code mismatch!");

    }

    @When("Admin sends PUT request to update programById with payload for {string}")
    public void admin_sends_put_request_to_update_program_by_id_with_payload_for_using_data_sheet(String scenario)
            throws IOException {
        String scenarioName = scenario;
        data = ExcelReader.readExcelData("Program", scenarioName);
        if (data != null) {
            String dataSheetTestname = data.get("ScenarioName");
            if (scenarioName.equalsIgnoreCase(dataSheetTestname)) {
                programInput = ProgramRequestParser.createProgramParseData(data.get("Body"));
                requestSpec = given().spec(requestSpec).body(programInput);

                String httpMethod = data.get("Method");
                String endPoint = data.get("Endpoint");
                System.out.println("##########SharedTestData.programIdList##########");
                System.out.println(SharedTestData.programIdList);
                System.out.println("##########SharedTestData.programIdList##########");
                if (endPoint.contains("{programId}")) {

                    if (SharedTestData.programIdList == null || SharedTestData.programIdList.isEmpty()) {
                        Assert.fail("Test stopped: No Program IDs available in SharedTestData.");
                    }

                    int lastIndex = SharedTestData.programIdList.size() - 1;
                    int programId = SharedTestData.programIdList.get(lastIndex);

                    endPoint = endPoint.replace("{programId}", String.valueOf(programId));
                }

                response = requestSpec.log().all().when().request(httpMethod, endPoint).then().log().all().extract()
                        .response();
            }
        }
    }

    @Then("Admin verifies the response payload for Update Program ByProgramId")
    public void admin_verifies_the_response_payload_for_update_program_by_program_id() {
        int expectedStatus = Integer.parseInt(data.get("ExpectedStatusCode"));
        String expectedMessage = data.get("ExpectedMessage");

        response.then().log().all().statusCode(expectedStatus).statusLine(Matchers.containsString(expectedMessage));

        if (expectedStatus == 200) {

            CreateProgramResponse actualResponse = response.as(CreateProgramResponse.class);
            response.then().assertThat()
                    .body(matchesJsonSchemaInClasspath("schemas/Program/UpdateProgramByProgramID.json"));
            response.then().body("programId",
                    Matchers.equalTo(SharedTestData.programIdList.get(programIdList.size() - 1)));
            Assert.assertEquals(actualResponse.getProgramName(), programInput.getProgramName(),
                    "Program Name is not matching after update.");
            Assert.assertEquals(actualResponse.getProgramStatus(), programInput.getProgramStatus(),
                    "Program Status is not matching after update.");
            Assert.assertNotNull(actualResponse.getCreationTime(), "Creation Time should not be null after update.");
            Assert.assertNotNull(actualResponse.getLastModTime(),
                    "Last Modification Time should not be null after update.");
        } else {
            switch (expectedStatus) {

                case 400:
                    String badReq = response.jsonPath().getString("message");
                    Assert.assertNotNull(badReq, "Bad request ");
                    break;
                case 404:
                    break;
                case 405:
                    String methodNotAllowed = response.jsonPath().getString("message");
                    Assert.assertEquals(methodNotAllowed, "Method Not Allowed");
                    break;
                default:
                    break;
            }
        }
    }

}
