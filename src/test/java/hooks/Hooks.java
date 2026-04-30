package hooks;
import configReader.ConfigReader;
import io.cucumber.java.Before;
import io.restassured.RestAssured;

public class Hooks {
    @Before(order = 0)
    public void setupBaseUri() {
        RestAssured.baseURI = ConfigReader.get("base.url");
    }

}
