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

        if (SharedTestData.programId == 0) {
            SharedTestData.programId = actualResponse.getProgramId();
        } else {
            SharedTestData.programIdList.add(actualResponse.getProgramId());
        }

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
                    // { "message": "..." }
                    Assert.assertEquals(message, expectedMsg);
                } else {
                    //  {{ "programName": "..."} or { "programDescription": "..." }
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

        // Always validate status code
        response.then().log().all().statusCode(expectedStatusCode);

        // NEGATIVE CASE → do NOT validate schema or list
        if (expectedStatusCode != 200) {

            String expectedMsg = data.get("ExpectedMessage").trim();
            String actualBody = response.getBody().asString().trim();

            // For plain text responses like "Invalid endpoint"
            Assert.assertTrue(
                    actualBody.contains(expectedMsg),
                    "Expected message: " + expectedMsg + " but got: " + actualBody
            );

            return;
        }

        // POSITIVE CASE → validate schema + list
        response.then()
                .body(matchesJsonSchemaInClasspath("schemas/Program/GetAllProgramsSchema.json"))
                .body("", Matchers.instanceOf(List.class))
                .body("size()", Matchers.greaterThan(0));

        // Additional validations for positive case
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

}
