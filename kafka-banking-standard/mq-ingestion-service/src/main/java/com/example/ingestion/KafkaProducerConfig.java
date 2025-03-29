package com.example.ingestion;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.jms.TextMessage;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.ConnectionFactory;
import javax.jms.Connection;
import javax.jms.MessageConsumer;

@Service
public class IngestionService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ConnectionFactory mqConnectionFactory;
    private final String kafkaTopic;
    private final String mqQueue;

    public IngestionService(KafkaTemplate<String, String> kafkaTemplate,
                            ConnectionFactory mqConnectionFactory,
                            @Value("${app.kafka.topic}") String kafkaTopic,
                            @Value("${ibm.mq.queue}") String mqQueue) {
        this.kafkaTemplate = kafkaTemplate;
        this.mqConnectionFactory = mqConnectionFactory;
        this.kafkaTopic = kafkaTopic;
        this.mqQueue = mqQueue;
    }

    @Scheduled(fixedRate = 2000)
    public void pollFromMqAndSendToKafka() {
        try (Connection connection = mqConnectionFactory.createConnection()) {
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue queue = session.createQueue(mqQueue);
            MessageConsumer consumer = session.createConsumer(queue);
            Message message = consumer.receive(1000);

            if (message instanceof TextMessage textMessage) {
                String payload = textMessage.getText();
                kafkaTemplate.send(kafkaTopic, payload);
                System.out.println("Published to Kafka: " + payload);
            }

            session.close();
            connection.close();

        } catch (Exception e) {
            System.err.println("Failed to read from MQ or publish to Kafka: " + e.getMessage());
        }
    }
}
