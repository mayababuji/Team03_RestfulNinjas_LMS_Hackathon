package stepDefinitions;

import configReader.ConfigReader;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import specBuilder.RequestSpec;
import specBuilder.ResponseSpec;
import utils.ExcelReader;
import utils.SharedTestData;

import java.io.IOException;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.lessThan;

public class LoginStepDef extends SharedTestData {

    private Map<String, String> testData;
    private Response response;
    private RequestSpecification requestSpec;

    @Given("Admin has the test data for {string} from Excel with No Auth")
    public void admin_has_the_test_data_for_from_excel_with_no_auth(String scenarioName) throws IOException {

        testData = ExcelReader.readExcelData("Login", scenarioName);

        String requestBody = testData.get("Body");

        System.out.println("\n===== REQUEST BODY SENT TO API =====\n" +
                requestBody +
                "\n====================================\n");

        RequestSpec.logScenarioName(scenarioName);

        requestSpec = given()
                .spec(RequestSpec.getRequestSpecWithoutAuth())
                .body(requestBody);
    }

    @When("Admin sends the post request for  Sign In")
    public void admin_sends_the_post_request_for_sign_in() {

        String endpoint = testData.get("Endpoint");

        System.out.println("\n===== ENDPOINT URL =====");
        System.out.println(ConfigReader.get("base.url") + endpoint);
        System.out.println("========================\n");

        if (testData.get("ScenarioName").contains("InvalidContentType")) {
            requestSpec.contentType("text/plain");
        }

        response = requestSpec.when().post(endpoint);
    }

    @Then("Admin should receive the status code as  in Excel")
    public void admin_should_receive_the_status_code_as_in_excel() {

        int expectedStatusCode = Integer.parseInt(testData.get("ExpectedStatusCode"));
        String scenarioName = testData.get("ScenarioName");

        response.then().spec(ResponseSpec.status(expectedStatusCode));

        String contentType = response.getHeader("Content-Type");

        if (contentType != null && contentType.contains("application/json")) {

            // Response time check (functional + performance mix)
            response.then().time(lessThan(2000L));

            // Only validate schema + token for valid login
            if (response.getStatusCode() == 200 &&
                    "Valid credential".equals(scenarioName.trim())) {

                response.then().assertThat()
                        .body(matchesJsonSchemaInClasspath("schemas/Login/UserSignInSchema.json"));

                String capturedToken = response.jsonPath().getString("token");
                if (capturedToken != null) {
                    token = capturedToken;
                }
            }
        }
    }

    @Then("the response should match the expected validation message from Excel")
    public void the_response_should_match_the_expected_validation_message_from_excel() {

        String expectedMsg = testData.get("Expectedmessage");
        String actualBody = response.getBody().asString();
        System.out.println("\n===== expected MSG =====\n");
        System.out.println(expectedMsg);
        System.out.println("\n===== expected MSG =====\n");
        System.out.println("\n===== ACTUAL MSG =====\n");
        System.out.println(actualBody);
        System.out.println("\n===== ACTUAL MSG =====\n");

        Assert.assertTrue(
                actualBody.contains(expectedMsg),
                "\nExpected to find: [" + expectedMsg + "] \nBut returned: [" + actualBody + "]"
        );
    }

    @When("Admin sends a GET  InvalidMethod")
    public void admin_sends_a_get_invalid_method() {
        String endpoint = testData.get("Endpoint");
        response = requestSpec.when().get(endpoint);
    }

    @Then("Admin should receive the status code matches with Expected statuscode from excel")
    public void admin_should_receive_the_status_code_matches_with_expected_statuscode() {
        int expected = Integer.parseInt(testData.get("ExpectedStatusCode"));

        int actual = response.getStatusCode();

        System.out.println("\n===== expected CODE =====\n");
        System.out.println(expected);
        System.out.println("\n===== expected CODE =====\n");
        System.out.println("\n===== ACTUAL CODE =====\n");
        System.out.println(actual);
        System.out.println("\n===== ACTUAL CODE =====\n");

        Assert.assertEquals(actual, expected, "Status Code Mismatch Expected " + expected + " but got " + actual);
    }

    @When("Admin sends the post request for ForgotPassword")
    public void admin_sends_the_post_request_for_forgot_password() {
        String endpoint = testData.get("Endpoint");
        String scenario = testData.get("ScenarioName");
        if (testData.get("ScenarioName").contains("ForgotPwd_InvalidContentType")) {
            requestSpec.contentType("text/plain");
        }
        response = requestSpec.when().post(endpoint);
        if (response.getStatusCode() == 200 && scenario.contains("Valid")) {
            response.then().assertThat()
                    .body(matchesJsonSchemaInClasspath("schemas/Login/ForgotPasswordSchema.json"));
        }
    }

    @Then("the response should match the expected validation message from excel")
    public void the_response_should_match_the_expected_validation_message_from_Excel() {
        String expectedMsg = testData.get("Expectedmessage");
        String actualBody = response.getBody().asString();
        Assert.assertTrue(actualBody.contains(expectedMsg),
                "\nExpected to find: [" + expectedMsg + "] \nBut returned: [" + actualBody + "]");
    }


    @Given("Admin has the test data for {string} from Excel")
    public void admin_has_the_test_data_for_from_excel(String scenarioName) throws IOException {
        testData = ExcelReader.readExcelData("Login", scenarioName);
        RequestSpec.logScenarioName(scenarioName);

        if (scenarioName.equalsIgnoreCase("LogoutWithInvalidToken")) {
            String excelToken = testData.get("Token");

            requestSpec = given().spec(RequestSpec.getRequestSpecWithCustomToken(excelToken))
                    .body(testData.get("Body"));
        } else {
            requestSpec = given().spec(RequestSpec.getRequestSpec()).body(testData.get("Body"));
        }
    }
    @When("Admin sends GET for logoutInvalidMethod")
    public void admin_sends_get_for_logout_invalid_method() {
        String endpoint = testData.get("Endpoint");
        response = requestSpec.when().post(endpoint);
    }
}
