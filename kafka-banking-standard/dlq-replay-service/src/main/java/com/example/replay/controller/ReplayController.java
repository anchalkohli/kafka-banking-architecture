package com.example.replay.controller;

import com.example.replay.service.KafkaReplayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/replay")
@RequiredArgsConstructor
public class ReplayController {

    private final KafkaReplayService kafkaReplayService;

    @PostMapping("/{region}")
    public ResponseEntity<String> replay(@PathVariable String region) {
        String sourceTopic = "dlq-payments-" + region.toLowerCase();
        String targetTopic = "raw-payments-" + region.toLowerCase();
        kafkaReplayService.replay(sourceTopic, targetTopic);
        return ResponseEntity.ok("Replay triggered for region: " + region.toUpperCase());
    }
}
