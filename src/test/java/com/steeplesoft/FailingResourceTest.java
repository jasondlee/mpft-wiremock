package com.steeplesoft;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.reset;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import java.net.URISyntaxException;

import com.github.tomakehurst.wiremock.stubbing.Scenario;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
@QuarkusTestResource(MockServer.class)
public class FailingResourceTest {

    public static final String SCENARIO_NAME = "Failing Resource";
    public static final String[] STATES = new String[]{"one", "two", "three", "four", "five"};
    public static final String REQUEST_URL = "/search?q=microprofile";

    @Test
    public void testSuccessfulRetry() throws URISyntaxException {
        reset();
        stubFor(get(urlEqualTo(REQUEST_URL))
                .inScenario(SCENARIO_NAME)
                .whenScenarioStateIs(Scenario.STARTED)
                .willReturn(aResponse().withStatus(500))
                .willSetStateTo(STATES[0]));
        stubFor(get(urlEqualTo(REQUEST_URL))
                .inScenario(SCENARIO_NAME)
                .whenScenarioStateIs(STATES[0])
                .willReturn(aResponse().withStatus(500))
                .willSetStateTo(STATES[1]));
        stubFor(get(urlEqualTo(REQUEST_URL))
                .inScenario(SCENARIO_NAME)
                .whenScenarioStateIs(STATES[1])
                .willReturn(aResponse().withBody("success")));

        given()
                .when()
                .get("/failing")
                .then()
                .statusCode(200)
                .body(is("success"));
    }

    @Test
    public void testUnsuccessfulRetry() {
        reset();
        stubFor(get(urlEqualTo(REQUEST_URL))
                .inScenario(SCENARIO_NAME)
                .whenScenarioStateIs(Scenario.STARTED)
                .willReturn(aResponse().withStatus(500)));

        given()
                .when()
                .get("/failing")
                .then()
                .statusCode(200)
                .body(is("fallback"));
    }
}
