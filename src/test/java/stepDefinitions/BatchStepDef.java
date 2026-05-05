package stepDefinitions;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

import java.io.IOException;
import java.util.Map;

import org.testng.Assert;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import pojo.CreateBatchRequest;
import pojo.CreateBatchResponse;
import specBuilder.RequestSpec;
import utils.ExcelReader;
import utils.ScenarioContext;
import utils.SharedTestData;

public class BatchStepDef extends SharedTestData {

	private RequestSpecification requestSpec;
	private Response response;
	private Map<String, String> data;
	private final ScenarioContext scenarioContext;

	public BatchStepDef(ScenarioContext scenarioContext) {
		this.scenarioContext = scenarioContext;
	}

	@Given("Admin sets authorization to Bearer Token")
	public void admin_sets_authorization_to_bearer_token() {
		requestSpec = RequestSpec.getRequestSpec();
	}

	@Given("Admin creates POST batch data for {string} from excel sheet")
	public void admin_creates_post_batch_data_for_from_excel_sheet(String scenario) throws IOException {

		data = ExcelReader.readExcelData("Batch", scenario);

		// Build request body
		CreateBatchRequest batchData = new CreateBatchRequest();
		batchData.setBatchDescription(data.get("batchDescription"));
		batchData.setBatchStatus(data.get("batchStatus"));

		// Handle non-numeric classes
		try {
			batchData.setBatchNoOfClasses(Integer.parseInt(data.get("batchNoOfClasses")));
		} catch (Exception e) {
			batchData.setBatchNoOfClasses(null);
		}

		// Override program ID from Excel if exists, otherwise take it from
		// SharedTestData
		try {
			batchData.setProgramId(Integer.parseInt(data.get("programId")));
		} catch (Exception e) {
			batchData.setProgramId(SharedTestData.programId);
		}

		// Override program name from Excel if exists, otherwise take it from
		// SharedTestData
		String programName = data.get("programName");
		System.out.println(programName);
		if (programName == null) {
			batchData.setprogramName(SharedTestData.programName);
		}

		// Batch name from Excel - prefix with programName from SharedTestData
		String batchName = SharedTestData.programName + data.get("batchName");
		batchData.setBatchName(batchName);
		SharedTestData.batchName = batchName;

		RequestSpecification spec = RequestSpec.getRequestSpec();

		System.out.println("Setting overrides for request spec");

		// Authorization override
		String auth = data.get("authorization");

		if ("None".equalsIgnoreCase(auth)) {
			// Build spec WITHOUT Authorization header
			System.out.println("Overriding Authorization to None");
			spec = RequestSpec.getRequestSpecWithoutAuth();
		} else {
			// Normal spec with token
			spec = RequestSpec.getRequestSpec();
		}

		// Content-Type override
		String contentType = data.get("contentType");
		if (contentType != null) {
			System.out.println("Overriding contentType to " + "text/" + contentType);
			spec.contentType("text/" + contentType);

			requestSpec = given().spec(spec).basePath(data.get("Endpoint")).body(batchData.toString());
		} else {
			// Build final request
			requestSpec = given().spec(spec).basePath(data.get("Endpoint")).body(batchData);
		}

		scenarioContext.setContext("BATCH_NAME", batchName);
	}

	@When("Admin sends HTTPS request to the endpoint")
	public void admin_sends_https_request_to_the_endpoint() {

		String method = data.get("Method");

		switch (method.toUpperCase()) {
		case "POST":
			response = requestSpec.when().log().all().post();
			break;
		case "GET":
			response = requestSpec.when().log().all().get();
			break;
		case "PUT":
			response = requestSpec.when().log().all().put();
			break;
		case "DELETE":
			response = requestSpec.when().log().all().delete();
			break;
		default:
			throw new RuntimeException("Invalid method in Excel: " + method);
		}
	}

	@Then("Admin receives expected status code from excel, validate POST batch response")
	public void admin_receives_expected_status_code_from_excel_validate_post_batch_response() {

		int expectedStatus = Integer.parseInt(data.get("expectedStatus"));

		// Always validate status
		response.then().log().all().statusCode(expectedStatus);

		if (expectedStatus == 201) {

			response.then().body(matchesJsonSchemaInClasspath("schemas/batch/CreateBatchSchema.json"));

			CreateBatchResponse batchResponse = response.as(CreateBatchResponse.class);

			SharedTestData.batchId = batchResponse.getBatchId();
			SharedTestData.batchName = batchResponse.getBatchName();

			String expectedBatchName = (String) scenarioContext.getContext("BATCH_NAME");
			Assert.assertEquals(batchResponse.getBatchName(), expectedBatchName,
					"Batch Name mismatch in positive scenario");
		} else {
			System.out.println("Skipping schema validation for negative scenario: " + data.get("ScenarioName"));
		}
	}

	@Given("Admin creates GET request for {string}")
	public void admin_creates_get_request_for(String scenario) throws IOException {

		data = ExcelReader.readExcelData("Batch", scenario);

		RequestSpecification spec = RequestSpec.getRequestSpec();

		// Authorization handling
		if ("None".equalsIgnoreCase(data.get("authorization"))) {
			spec = RequestSpec.getRequestSpecWithoutAuth();
		} else {
			spec = RequestSpec.getRequestSpec();
		}

		System.out.println("Checking override for content type");
		if (data.get("contentType") != null) {
			System.out.println("Overriding content type to text/" + data.get("contentType") );
			spec.contentType("invalid/" + data.get("contentType"));
		}

		requestSpec = given().spec(spec).basePath(data.get("Endpoint"));
		
	}

	@Then("Admin receives expected status code from Excel")
	public void admin_receives_expected_status_code_from_excel() {

		int expectedStatus = Integer.parseInt(data.get("expectedStatus"));

		// Validate status code
		response.then().log().all().statusCode(expectedStatus);

		System.out.println("Validated status code: " + expectedStatus);
	}
	
	@Given("Admin creates GET batch by id request for {string}")
	public void admin_creates_get_batch_by_id_request_for(String scenario) throws IOException {
	    
		data = ExcelReader.readExcelData("Batch", scenario);
		

		RequestSpecification spec = RequestSpec.getRequestSpec();

		// Authorization handling
		if ("None".equalsIgnoreCase(data.get("authorization"))) {
			spec = RequestSpec.getRequestSpecWithoutAuth();
		} else {
			spec = RequestSpec.getRequestSpec();
		}

		System.out.println("Checking override for content type");
		if (data.get("contentType") != null) {
			System.out.println("Overriding content type to text/" + data.get("contentType") );
			spec.contentType("invalid/" + data.get("contentType"));
		}

		requestSpec = given().spec(spec).basePath(data.get("Endpoint")).pathParam("batchId", String.valueOf(SharedTestData.batchId));		
		
	}
}
