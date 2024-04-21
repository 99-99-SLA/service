package ro.unibuc.hello.monitoring;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import static org.springframework.boot.actuate.health.Status.*;

@Component
public class TmdbHealthMetric {

    @Autowired
    private MeterRegistry meterRegistry;

    @Autowired
    private TmdbHealthIndicator healthIndicator;

    @PostConstruct
    public void initGauge() {
        Gauge.builder("external_service_health", this, value -> {
                    Health health = healthIndicator.health();
                    return health.getStatus().equals(UP) ? 1 : 0;
                })
                .description("Health status of the external service: 1 (up), 0 (down)")
                .register(meterRegistry);
    }
}
