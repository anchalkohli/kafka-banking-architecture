package com.example.ingestion;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class IngestionService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String topic;

    public IngestionService(KafkaTemplate<String, String> kafkaTemplate,
                            @Value("${app.kafka.topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void sendToKafka(String message, String region) {
        String enrichedMessage = "[Region: " + region + "] " + message;
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, enrichedMessage);
        kafkaTemplate.send(record);
    }
}
