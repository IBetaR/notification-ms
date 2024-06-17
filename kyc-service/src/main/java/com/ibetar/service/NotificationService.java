package com.ibetar.service;

import com.ibetar.entity.NotificationVO;
import com.ibetar.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final NotificationRepository repository;
    public void createNotification(NotificationVO notificationVO) {
        LOGGER.info("Creating notification with id: {} for profile {}.",
                notificationVO.getNotificationId(),
                notificationVO.getProfileName()
        );
        repository.save(notificationVO);
    }
}
