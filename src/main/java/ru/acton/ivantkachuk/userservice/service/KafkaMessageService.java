package ru.acton.ivantkachuk.userservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.acton.ivantkachuk.userservice.entity.UserEvent;

@Service
@RequiredArgsConstructor
public class KafkaMessageService {

    @Value("${spring.kafka.topics.user-events.name}")
    private String topic;

    @Autowired
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendMessage( UserEvent event) {
        kafkaTemplate.send(topic,event.email(), event);
    }
}
