package ro.unibuc.hello.monitoring;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import ro.unibuc.hello.client.TheMovieDbClient;

@Component
public class TmdbHealthIndicator implements HealthIndicator {
    private TheMovieDbClient theMovieDbClient;
    @Override
    public Health health() {
        try {
            final Integer code = theMovieDbClient.healthRequestCode();
            if (code < 300) {
                return Health.up().withDetail("service", "Available").build();
            } else {
                return Health.down().withDetail("service", "Failed - Status Code: " + code).build();
            }
        } catch (Exception e) {
            return Health.down().withDetail("service", "Failed - Exception: " + e.getMessage()).build();
        }
    }
}
