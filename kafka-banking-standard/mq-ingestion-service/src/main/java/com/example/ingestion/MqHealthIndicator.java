import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;

@Component
public class MqHealthIndicator implements HealthIndicator {

    private final ConnectionFactory connectionFactory;

    public MqHealthIndicator(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public Health health() {
        try (Connection connection = connectionFactory.createConnection()) {
            connection.start();
            return Health.up().withDetail("mq", "Connected").build();
        } catch (Exception e) {
            return Health.down().withDetail("mq", "Failed: " + e.getMessage()).build();
        }
    }
}
