package com.example.ingestion;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class IngestionService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Value("${app.kafka.topic}")
    private String kafkaTopic;

    public void processAndSendToKafka(String message) {
    int attempts = 0;
    int maxAttempts = 3;
    long backoffMs = 2000;

    while (attempts < maxAttempts) {
        try {
            kafkaTemplate.send(kafkaTopic, message).get(); //synchronous send
            log.info("Message sent successfully to topic: {}", kafkaTopic);
            return;
        } catch (Exception ex) {
            attempts++;
            log.warn("Attempt {} failed to publish to Kafka topic [{}]", attempts, kafkaTopic);

            if (attempts < maxAttempts) {
                try {
                    Thread.sleep(backoffMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    // All retries failed — send to DLQ
    String dlqTopic = kafkaTopic.replace("raw", "dlq");
    try {
        kafkaTemplate.send(dlqTopic, message).get();
        log.warn("Message sent to DLQ after retries: {}", dlqTopic);
        dlqCounter.increment();  // metric
    } catch (Exception dlqEx) {
        log.error("Failed to send message to DLQ topic [{}]", dlqTopic, dlqEx);
    }
}
        }
    }
}
