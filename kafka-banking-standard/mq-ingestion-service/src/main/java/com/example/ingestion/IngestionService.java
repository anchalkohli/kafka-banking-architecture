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
        try {
            kafkaTemplate.send(kafkaTopic, message).get(); // synchronous send
            log.info("Message sent to topic: {}", kafkaTopic);
        } catch (Exception ex) {
            log.error("Kafka publish failed for topic [{}]. Sending to DLQ...", kafkaTopic, ex);

            // Route to DLQ
            String dlqTopic = kafkaTopic.replace("raw", "dlq");
            try {
                kafkaTemplate.send(dlqTopic, message).get();
                log.warn("Message rerouted to DLQ: {}", dlqTopic);
            } catch (Exception dlqEx) {
                log.error("DLQ publish also failed for topic [{}]", dlqTopic, dlqEx);
            }
        }
    }
}
