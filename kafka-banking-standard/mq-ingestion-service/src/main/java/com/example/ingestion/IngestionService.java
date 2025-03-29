package com.example.ingestion;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

@Service
public class IngestionService {

    private static final Logger log = LoggerFactory.getLogger(IngestionService.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Counter dlqCounter;

    @Value("${app.kafka.topic}")
    private String kafkaTopic;

    public IngestionService(KafkaTemplate<String, String> kafkaTemplate, MeterRegistry meterRegistry) {
        this.kafkaTemplate = kafkaTemplate;
        this.dlqCounter = Counter.builder("mq.ingestion.dlq.messages")
                .description("Count of messages routed to DLQ")
                .register(meterRegistry);
    }

    public void processAndSendToKafka(String message) {
        int attempts = 0;
        int maxAttempts = 3;
        long backoffMs = 2000;

        while (attempts < maxAttempts) {
            try {
                kafkaTemplate.send(new ProducerRecord<>(kafkaTopic, message)).get();
                log.info("Message sent successfully to topic: {}", kafkaTopic);
                return;
            } catch (Exception ex) {
                attempts++;
                log.warn("Attempt {} failed for topic [{}]: {}", attempts, kafkaTopic, ex.getMessage());

                if (attempts < maxAttempts) {
                    try {
                        Thread.sleep(backoffMs);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }

        // All retries failed — send to DLQ
        String dlqTopic = kafkaTopic.replace("raw", "dlq");
        try {
            kafkaTemplate.send(dlqTopic, message).get();
            dlqCounter.increment();
            log.warn("Message sent to DLQ: {}", dlqTopic);
        } catch (Exception dlqEx) {
            log.error("Failed to send message to DLQ [{}]: {}", dlqTopic, dlqEx.getMessage());
        }
    }
}
