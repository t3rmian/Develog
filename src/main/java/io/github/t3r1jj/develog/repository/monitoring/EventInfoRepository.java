package io.github.t3r1jj.develog.repository.monitoring;

import io.github.t3r1jj.develog.model.monitoring.EventInfo;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EventInfoRepository extends MongoRepository<EventInfo, ObjectId> {
}
