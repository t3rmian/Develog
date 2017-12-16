package io.github.t3r1jj.develog.repository.monitoring;

import io.github.t3r1jj.develog.model.monitor.Event;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface EventRepository extends MongoRepository<Event, ObjectId> {
    List<Event> findAllByOrderByTimeAsc();
}
