package io.jromanmartin.quarkus.sampleapp.utils;

import io.quarkus.scheduler.Scheduled;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import java.util.Random;

@ApplicationScoped
public class ChaosBuilder {

    private static final Logger LOG = Logger.getLogger(ChaosBuilder.class);

    @ConfigProperty(name = "chaos.readiness.enable", defaultValue = "false")
    boolean chaosReadinessEnable;

    @ConfigProperty(name = "chaos.liveness.enable", defaultValue = "false")
    boolean chaosLivenessEnable;

    /**
     * Random generator
     */
    Random random = new Random();

    /**
     * Current liveness status
     */
    boolean livenessUp = true;

    /**
     * Current readiness status
     */
    boolean readinessUp = true;

    /**
     * Builds liveness chaos
     */
    @Scheduled(cron = "{chaos.liveness.cron}")
    void buildingLivenessChaos() {
        if (chaosLivenessEnable) {
            livenessUp = random.nextBoolean();

            LOG.infov("Liveness status is {0}", (livenessUp ? "UP" : "DOWN"));
        }
    }

    @Scheduled(cron = "{chaos.readiness.cron}")
    void buildingReadinessChaos() {
        if (chaosReadinessEnable) {
            readinessUp = random.nextBoolean();

            LOG.infov("Readiness status is {0}", (readinessUp ? "UP" : "DOWN"));
        }
    }

    /**
     * @return the livenessUp
     */
    public boolean isLivenessUp() {
        return livenessUp;
    }

    /**
     * @return the readinessUp
     */
    public boolean isReadinessUp() {
        return readinessUp;
    }

}
