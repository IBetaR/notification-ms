package com.ibetar.repository;

import com.ibetar.entity.NotificationVO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface NotificationRepository extends MongoRepository<NotificationVO, UUID> {
}
