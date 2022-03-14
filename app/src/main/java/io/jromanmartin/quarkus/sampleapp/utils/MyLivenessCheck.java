package io.jromanmartin.quarkus.sampleapp.utils;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Liveness;

import javax.inject.Inject;

@Liveness
public class MyLivenessCheck implements HealthCheck {

    @Inject
    ChaosBuilder chaosBuilder;

    @Override
    public HealthCheckResponse call() {
        HealthCheckResponseBuilder responseBuilder = HealthCheckResponse.named("Liveness health check");

        try {
            if (!chaosBuilder.isLivenessUp()) {
                throw new IllegalStateException("I am not alive");
            }
            responseBuilder.up().withData("status", "alive");
        } catch (IllegalStateException e) {
            responseBuilder.down().withData("status", "dead");
        }

        return responseBuilder.build();
    }

}
