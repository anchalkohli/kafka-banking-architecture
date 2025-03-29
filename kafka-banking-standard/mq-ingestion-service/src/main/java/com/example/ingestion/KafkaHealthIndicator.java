package com.example.health;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DescribeClusterResult;
import org.apache.kafka.common.KafkaFuture;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Component
public class KafkaHealthIndicator implements HealthIndicator {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Override
    public Health health() {
        Map<String, Object> config = Collections.singletonMap("bootstrap.servers", bootstrapServers);

        try (AdminClient adminClient = AdminClient.create(config)) {
            DescribeClusterResult clusterResult = adminClient.describeCluster();
            KafkaFuture<String> clusterId = clusterResult.clusterId();
            clusterId.get(); // wait for response
            return Health.up().withDetail("kafka", "Connected").build();
        } catch (Exception ex) {
            return Health.down().withDetail("kafka", "Failed: " + ex.getMessage()).build();
        }
    }
}
