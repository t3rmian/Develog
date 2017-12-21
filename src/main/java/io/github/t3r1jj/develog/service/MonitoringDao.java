package io.github.t3r1jj.develog.service;

import com.mongodb.client.MongoDatabase;
import io.github.t3r1jj.develog.model.monitor.Event;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Service
public class MonitoringDao {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public MonitoringDao(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public void truncateEvents(int n) {
        Query query = new Query()
                .with(new Sort(Sort.Direction.ASC, "time"))
                .skip(n);
        mongoTemplate.findAllAndRemove(query, Event.class);
    }

    public long getMongoDbSize() {
        try {
            MongoDatabase db = mongoTemplate.getDb();
            Document document = db.runCommand(new BsonDocument("dbStats", new BsonInt32(1)).append("scale",
                    new BsonInt32(1024 * 1024)));
            return Double.valueOf(document.get("dataSize").toString()).longValue();
        } catch (Exception ex) {
            return -1;
        }
    }
}
