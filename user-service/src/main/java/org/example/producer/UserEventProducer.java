package org.example.producer;

import org.example.dto.UserNotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserEventProducer {

    private static final String TOPIC = "user-notifications";
    private final KafkaTemplate<String, UserNotificationEvent> kafkaTemplate;

    public void send(UserNotificationEvent event) {
        kafkaTemplate.send(TOPIC, event.getEmail(), event);
    }
}