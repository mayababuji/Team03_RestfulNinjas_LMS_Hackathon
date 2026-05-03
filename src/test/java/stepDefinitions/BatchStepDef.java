package stepDefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import pojo.CreateBatchRequest;
import pojo.CreateBatchResponse;
import specBuilder.RequestSpec;
import utils.ExcelReader;
import utils.ScenarioContext;
import utils.SharedTestData;

import java.io.IOException;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class BatchStepDef extends SharedTestData {

    private RequestSpecification requestSpec;
    private Response response;
    private Map<String, String> data;
    private final ScenarioContext scenarioContext;

    public BatchStepDef(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
    }

    @Given("Admin creates POST batch data for {string} from excel sheet")
    public void admin_creates_post_batch_data_for_from_excel_sheet(String scenario) throws IOException {
        
        data = ExcelReader.readExcelData("Batch", scenario);

        // Parse request body
        CreateBatchRequest batchData = new CreateBatchRequest();
        
        batchData.setBatchDescription(data.get("batchDescription"));
        batchData.setBatchStatus(data.get("batchStatus"));
        //batchData.setBatchNoOfClasses(Integer.parseInt(data.get("batchNoOfClasses")));
        batchData.setBatchNoOfClasses(Integer.parseInt(data.get("batchNoOfClasses")));


        // Inject programId from SharedTestData
        batchData.setProgramId(SharedTestData.programId);
        //batchData.setProgramName(SharedTestData.programName);

        // Auto-generate batchName
        String batchName = SharedTestData.programName + "_01";
        batchData.setBatchName(batchName);
        SharedTestData.batchName = batchName;

        // Build request
        requestSpec = given()
                .spec(RequestSpec.getRequestSpec())
                .basePath(data.get("Endpoint"))
                .body(batchData);

        // Store for later validation
        scenarioContext.setContext("BATCH_NAME", batchData.getBatchName());
    }

    @When("Admin sends HTTPS request to the endpoint")
    public void admin_sends_https_request_to_the_endpoint() {
        response = requestSpec.when().log().all().post();
    }

    @Then("Admin receives expected status code from excel, validate POST batch response")
    public void admin_receives_expected_status_code_from_excel_validate_post_batch_response() {

        response.then().log().all()
                .statusCode(Integer.parseInt(data.get("expectedStatus")))
                .body(matchesJsonSchemaInClasspath("schemas/batch/CreateBatchSchema.json"));

        CreateBatchResponse batchResponse = response.as(CreateBatchResponse.class);

        // Store batchId and batchName globally
        if (batchIds.isEmpty()) {
            batchId = batchResponse.getBatchId();
            batchName = batchResponse.getBatchName();
        }

        batchIds.add(batchResponse.getBatchId());

        // Validate batchName
        String expectedBatchName = (String) scenarioContext.getContext("BATCH_NAME");
        Assert.assertEquals(batchResponse.getBatchName(), expectedBatchName,
                "Batch Name doesn't match in response");
    }
}
