package com.steeplesoft;

import java.net.URISyntaxException;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Path("/failing")
public class FailingResource {

    @Inject
    @RestClient
    GoogleClient client;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Retry(maxRetries = 2)
    @Fallback(fallbackMethod = "doWorkFallback")
    public String hello() throws URISyntaxException {
        return client.search("microprofile");
    }

    private String doWorkFallback() {
        return "fallback";
    }
}
