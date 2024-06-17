package com.ibetar.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibetar.entity.NotificationVO;
import com.ibetar.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.KStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.function.Consumer;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaListenerService {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final ObjectMapper objectMapper;
    private final NotificationService notificationService;

    @Bean
    @Qualifier("listenKycNotifications")
    public Consumer<KStream<Object, String>> listenKycNotifications() {
        LOGGER.info("Listening for KYC notifications from Kafka...");

        return input -> {
            input.foreach((key, value) -> {
                if (isValidMessage(value)) {
                    processNotificationMessage(value);
                } else {
                    LOGGER.info("Ignoring empty or invalid message: {}", value);
                }
            });
        };
    }

    private boolean isValidMessage(String messageJson) {
        return messageJson != null && !messageJson.trim().isEmpty() && !messageJson.equals("{}");
    }

    private void processNotificationMessage(String messageJson) {
        try {
            LOGGER.info("Notification found from Kafka: {}", messageJson);

            Map<String, Object> message = parseMessageJson(messageJson);
            NotificationVO notification = objectMapper.convertValue(
                            message.get("notification"),
                            NotificationVO.class
                    );

            LOGGER.debug("Notification retrieved from JSON with id {}", notification.getNotificationId());
            notificationService.createNotification(notification);

        } catch (JsonProcessingException e) {
            LOGGER.error("Error processing JSON message: {}", e.getMessage());
            throw new RuntimeException("Failed to process notification message", e);
        }
    }

    private Map<String, Object> parseMessageJson(String messageJson) throws JsonProcessingException {
        LOGGER.debug("Parsing JSON message from Kafka...");
        return objectMapper.readValue(messageJson, new TypeReference<>() {});
    }

}