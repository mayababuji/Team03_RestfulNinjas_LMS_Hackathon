package stepDefinitions;

import dtoRequest.CreateProgramRequest;
import dtoResponse.CreateProgramResponse;
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
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.testng.Assert.assertTrue;

public class ProgramStepDef  extends SharedTestData {
    private static RequestSpecification requestSpec;
    private Map<String, String> data;
    private Response response;
    private static CreateProgramRequest programInput;

    @Given("Admin has a valid authorization token set")
    public void admin_has_a_valid_authorization_token_set() {
        requestSpec = RequestSpec.getRequestSpec();
    }
    @When("Admin sends POST request to create program with different payload for {string} from dataSheet")
    public void admin_sends_post_request_to_create_program_with_different_payload_for_from_data_sheet(
            String scenarioNameFromFeature) throws IOException {

        data = ExcelReader.readExcelData("Program", scenarioNameFromFeature);

        if (data != null) {
            String dataSheetTestName = data.get("ScenarioName");
            if (scenarioNameFromFeature.equalsIgnoreCase(dataSheetTestName)) {
                programInput = ProgramRequestParser.createProgramParseData(data.get("Body"));

                requestSpec = given().spec(requestSpec).body(programInput);
                // Generate unique programName
                String baseName = programInput.getProgramName();
                String randomSuffix = RandomStringUtils.randomAlphabetic(5);
                String uniqueProgramName = baseName + randomSuffix;
                programInput.setProgramName(uniqueProgramName);

                // Store globally in SharedTestData for Batch creation
                SharedTestData.programName = uniqueProgramName;

                requestSpec = given().spec(requestSpec).body(programInput);
                String httpMethod = data.get("Method");
                String endPoint = data.get("Endpoint");

                response = requestSpec.log().all().when().request(httpMethod, endPoint).then().log().all().extract()
                        .response();
            }
        } else {
            throw new RuntimeException("Test data not found for: " + scenarioNameFromFeature);
        }
    }

    @Then("Admin verifies the response payload with expected output from the data sheet")
    public void admin_verifies_the_response_payload_with_expected_output_from_the_data_sheet() {

        int expectedStatus = Integer.parseInt(data.get("ExpectedStatusCode"));
        response.then().log().all().statusCode(expectedStatus);
        if (expectedStatus == 201) {

            response.then().log().all().assertThat()
                    .body(matchesJsonSchemaInClasspath("schemas/Program/CreateProgramSchema.json"));


            CreateProgramResponse actualResponse = response.as(CreateProgramResponse.class);
            String programName = actualResponse.getProgramName();
            SharedTestData.programName = programName;
            SharedTestData.programNameList.add(programName);

            if (SharedTestData.programId == 0) {
                SharedTestData.programId = actualResponse.getProgramId();
            } else {
                SharedTestData.programIdList.add(actualResponse.getProgramId());
            }

            Assert.assertEquals(actualResponse.getProgramDescription(), programInput.getProgramDescription(),
                    "ProgramDescription is not matching");
            Assert.assertEquals(actualResponse.getProgramName(), programInput.getProgramName(),
                    "ProgramName is not matching");
            int createdProgramId = actualResponse.getProgramId();
            assertTrue(createdProgramId > 0, "ProgramId should not be negative value");
        } else {
            switch (expectedStatus) {

                case 400:
                    String badReq = response.jsonPath().getString("message");
                    Assert.assertNotNull(badReq, "Bad request ");
                    break;
                case 404:
                    break;
                case 405:
                    String methodNotAllowed = response.jsonPath().getString("error");
                    Assert.assertEquals(methodNotAllowed, "Method Not Allowed");
                    break;
                default:
                    break;
            }
        }
    }
}
