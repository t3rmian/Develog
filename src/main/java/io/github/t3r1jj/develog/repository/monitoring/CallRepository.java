package io.github.t3r1jj.develog.repository.monitoring;

import io.github.t3r1jj.develog.model.monitor.Call;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CallRepository extends MongoRepository<Call, ObjectId> {
}
