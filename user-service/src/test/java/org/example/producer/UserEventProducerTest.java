package org.example.producer;

import org.example.dto.UserNotificationEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserEventProducerTest {

    @Mock
    private KafkaTemplate<String, UserNotificationEvent> kafkaTemplate;

    @InjectMocks
    private UserEventProducer producer;

    @Test
    void send_shouldSendEventToCorrectTopicWithEmailAsKey() {

        UserNotificationEvent event = new UserNotificationEvent();
        event.setEmail("test@yandex.ru");
        event.setOperation(UserNotificationEvent.Operation.CREATE);

        producer.send(event);

        ArgumentCaptor<UserNotificationEvent> eventCaptor =
                ArgumentCaptor.forClass(UserNotificationEvent.class);

        verify(kafkaTemplate).send(
                eq("user-notifications"),
                eq("test@yandex.ru"),
                eventCaptor.capture()
        );

        UserNotificationEvent capturedEvent = eventCaptor.getValue();
        assertEquals(UserNotificationEvent.Operation.CREATE, capturedEvent.getOperation());
        assertEquals("test@yandex.ru", capturedEvent.getEmail());
    }
}