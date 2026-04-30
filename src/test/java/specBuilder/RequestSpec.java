package specBuilder;

import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;


import configReader.ConfigReader;
import utils.SharedTestData;

public class RequestSpec {

    private static PrintStream logStream;

    public static RequestSpecification getRequestSpec() {
        initializeLogStream();
        String currentToken = SharedTestData.token;
        return new io.restassured.builder.RequestSpecBuilder().setBaseUri(ConfigReader.get("base.url"))
                .addHeader("Authorization", "Bearer " + currentToken).addHeader("Content-Type", "application/json")
                .addFilter(RequestLoggingFilter.logRequestTo(logStream))
                .addFilter(ResponseLoggingFilter.logResponseTo(logStream)).build();
    }

    public static RequestSpecification getRequestSpecWithoutAuth() {
        initializeLogStream();
        return new io.restassured.builder.RequestSpecBuilder().setBaseUri(ConfigReader.get("base.url"))
                .addHeader("Content-Type", "application/json").addFilter(RequestLoggingFilter.logRequestTo(logStream))
                .addFilter(ResponseLoggingFilter.logResponseTo(logStream)).build();
    }

    public static RequestSpecification getRequestSpecWithCustomToken(String customToken) {
        initializeLogStream();
        return new io.restassured.builder.RequestSpecBuilder().setBaseUri(ConfigReader.get("base.url"))
                .addHeader("Authorization", "Bearer " + customToken).addHeader("Content-Type", "application/json")
                .addFilter(RequestLoggingFilter.logRequestTo(logStream))
                .addFilter(ResponseLoggingFilter.logResponseTo(logStream)).build();
    }

    public static RequestSpecification getRequestSpecInvalidAuth() {
        initializeLogStream();
        return new io.restassured.builder.RequestSpecBuilder().setBaseUri(ConfigReader.get("base.url")).addHeader("Authorization", "Bearer ")
                .addHeader("Content-Type", "application/json").addFilter(RequestLoggingFilter.logRequestTo(logStream))
                .addFilter(ResponseLoggingFilter.logResponseTo(logStream)).build();
    }

    // Initialize the file stream once
    private static void initializeLogStream() {
        if (logStream == null) {
            synchronized (RequestSpec.class) {
                if (logStream == null) {
                    try {
                        String filePath = ConfigReader.get("LogFilePath");
                        File logFile = new File(filePath);
                        if (logFile.getParentFile() != null && !logFile.getParentFile().exists()) {
                            logFile.getParentFile().mkdirs();
                        }
                        logStream = new PrintStream(new FileOutputStream(filePath, false));
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to initialize log file", e);
                    }
                }
            }
        }
    }

    public static void logScenarioName(String scenarioName) {
        initializeLogStream();
        logStream.println("\n==================================================");
        logStream.println("SCENARIO: " + scenarioName);
        logStream.println("==================================================\n");
        logStream.flush();
    }

}

