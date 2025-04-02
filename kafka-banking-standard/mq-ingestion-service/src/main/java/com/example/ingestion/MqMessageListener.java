package com.example.ingestion.listener;

import com.example.ingestion.service.IngestionService;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class MqMessageListener {

    private static final Logger log = LoggerFactory.getLogger(MqMessageListener.class);
    private final IngestionService ingestionService;

    public MqMessageListener(IngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    @JmsListener(destination = "${ibm.mq.queue}", containerFactory = "jmsListenerContainerFactory")
    public void onMessage(String message) {
        log.info("Received message from MQ: {}", message);

        try {
            ingestionService.processAndSendToKafka(message);
        } catch (Exception e) {
            log.error("Failed to process MQ message", e);
            // optional: trigger DLQ fallback from here if needed
        }
    }
}
