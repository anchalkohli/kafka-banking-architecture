package com.example.replay.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.*;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaReplayService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ConsumerFactory<String, String> consumerFactory;

    public void replay(String sourceTopic, String targetTopic) {
        log.info("Starting DLQ replay: {} → {}", sourceTopic, targetTopic);

        Consumer<String, String> consumer = consumerFactory.createConsumer();
        consumer.subscribe(Collections.singletonList(sourceTopic));
        consumer.poll(Duration.ofMillis(500)); // force assignment

        boolean done = false;
        while (!done) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(2));
            if (records.isEmpty()) {
                done = true;
            }

            for (ConsumerRecord<String, String> record : records) {
                try {
                    kafkaTemplate.send(targetTopic, record.key(), record.value());
                    log.info("Replayed offset={} key={} → {}", record.offset(), record.key(), targetTopic);
                } catch (Exception e) {
                    log.error("Replay failed at offset={} key={}", record.offset(), record.key(), e);
                }
            }
        }

        consumer.close();
        log.info("Replay complete.");
    }
}
