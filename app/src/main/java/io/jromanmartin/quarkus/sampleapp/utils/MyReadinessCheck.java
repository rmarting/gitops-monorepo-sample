package io.jromanmartin.quarkus.sampleapp.utils;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Readiness;

import javax.inject.Inject;

@Readiness
public class MyReadinessCheck implements HealthCheck {

    @Inject
    ChaosBuilder chaosBuilder;

    @Override
    public HealthCheckResponse call() {
        HealthCheckResponseBuilder responseBuilder = HealthCheckResponse.named("Readiness health check");

        try {
            if (!chaosBuilder.isReadinessUp()) {
                throw new IllegalStateException("I am not ready");
            }
            responseBuilder.up().withData("status", "ready");
        } catch (IllegalStateException e) {
            responseBuilder.down().withData("status", "not ready");
        }

        return responseBuilder.build();
    }

}
