package io.github.t3r1jj.develog.repository.monitoring;

import io.github.t3r1jj.develog.model.monitor.Error;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ErrorRepository extends MongoRepository<Error, ObjectId> {
}
