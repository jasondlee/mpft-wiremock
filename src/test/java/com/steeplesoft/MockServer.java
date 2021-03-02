package com.steeplesoft;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

import java.util.HashMap;
import java.util.Map;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class MockServer implements QuarkusTestResourceLifecycleManager {
    private WireMockServer wireMockServer;

    @Override
    public Map<String, String> start() {
        wireMockServer = new WireMockServer();
        wireMockServer.start();

        stubFor(get(urlEqualTo("/search"))
                .willReturn(aResponse()
                        .withBody("Error")));

        Map<String,String> props = new HashMap<>();
        props.put("com.steeplesoft.GoogleClient/mp-rest/url", wireMockServer.baseUrl());
        props.put("wiremock.url", wireMockServer.baseUrl());
        return props;
    }

    @Override
    public void stop() {
        wireMockServer.stop();
    }
}
