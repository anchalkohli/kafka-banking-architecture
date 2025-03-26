package com.example.ingestion;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/messages")
public class IngestionController {

    private final IngestionService ingestionService;
    private final String region;

    public IngestionController(IngestionService ingestionService,
                               @Value("${app.kafka.region}") String region) {
        this.ingestionService = ingestionService;
        this.region = region;
    }

    @PostMapping
    public String ingestMessage(@RequestBody String message) {
        ingestionService.sendToKafka(message, region);
        return "Message sent to Kafka for region: " + region;
    }
}
