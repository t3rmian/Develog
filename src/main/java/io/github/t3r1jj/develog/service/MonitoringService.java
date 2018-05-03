package io.github.t3r1jj.develog.service;

import com.mongodb.client.MongoDatabase;
import io.github.t3r1jj.develog.model.data.User;
import io.github.t3r1jj.develog.model.monitor.Event;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MonitoringService {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public MonitoringService(MongoTemplate mongoTemplate) {
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

    public long getUsersDataSize() {
        try {
            MongoDatabase db = mongoTemplate.getDb();
            Document document = db.runCommand(new Document("collStats", "users").append("scale", new BsonInt32(1024 * 1024)));
            return Double.valueOf(document.get("size").toString()).longValue();
        } catch (Exception ex) {
            return -1;
        }
    }

    public long getNotesDataSize() {
        try {
            MongoDatabase db = mongoTemplate.getDb();
            Document document = db.runCommand(new Document("collStats", "notes").append("scale", new BsonInt32(1024 * 1024)));
            return Double.valueOf(document.get("size").toString()).longValue();
        } catch (Exception ex) {
            return -1;
        }
    }

    /**
     * @return number of characters written by user (notes body, tags) in thousands [kB]
     */

    public Map<User, Long> findAllUsersDataSize(List<User> users) {
        MongoDatabase db = mongoTemplate.getDb();
        Bson command = new Document("eval", "db.users.aggregate(\n" +
                "    [\n" +
                "        {$unwind: {\n" +
                "                path: \"$noteIds\",\n" +
                "                preserveNullAndEmptyArrays: true\n" +
                "            }\n" +
                "        },\n" +
                "        {$lookup: {from: \"note\",localField: \"noteIds\",foreignField: \"_id\",as: \"note\"}},\n" +
                "        {$project: {note: {$setUnion: [[\"$globalNote\"],\"$note\"]}}},\n" +
                "        {$unwind: {\n" +
                "                path: \"$note\",\n" +
                "                preserveNullAndEmptyArrays: true\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            $unwind: {\n" +
                "                path: \"$note.tags\",\n" +
                "                preserveNullAndEmptyArrays: true\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            $project: {\n" +
                "                \"body_length\": { \n" +
                "                    $cond: [{$not: [\"$note.body\"]}, 0, {$strLenBytes: \"$note.body\"}]\n" +
                "                },\n" +
                "                \"tags_length\": { \n" +
                "                    $cond: [{$not: [\"$note.tags\"]}, 0, {$strLenBytes: \"$note.tags\"}]\n" +
                "                }\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            $group: {\n" +
                "                _id: {_id: \"$_id\", body_length:\"$body_length\", noteId: \"$note._id\" },\n" +
                "                \"tags_length\": {$sum: \"$tags_length\"}\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            $group: {\n" +
                "                _id: \"$_id._id\",\n" +
                "                \"body_length\": {$sum: \"$_id.body_length\"},\n" +
                "                \"tags_length\": {$sum: \"$tags_length\"},\n" +
                "            }\n" +
                "        }\n" +
                "    ]\n" +
                ")");
        Document result = db.runCommand(command);
        if ("1.0".equals(result.get("ok").toString())) {
            Document returnedDocument = ((Document) result.get("retval"));
            Collection<Document> documents = (Collection<Document>) returnedDocument.get("_batch");
            Map<Long, Long> sizes = documents.stream().map(it -> {
                long bodyLength = Double.valueOf(it.get("body_length").toString()).longValue();
                long tagsLength = Double.valueOf(it.get("tags_length").toString()).longValue();
                Long id = it.getLong("_id");
                return new HashMap.SimpleImmutableEntry<>(id, (bodyLength + tagsLength) / 1000);
            }).collect(Collectors.toMap(HashMap.Entry::getKey, HashMap.Entry::getValue));
            return users.stream().map(it -> new HashMap.SimpleImmutableEntry<>(it, sizes.getOrDefault(it.getId(), 0L)))
                    .collect(Collectors.toMap(HashMap.Entry::getKey, HashMap.Entry::getValue));
        }
        return new HashMap<>();
    }

    /**
     * @return number of characters written by user (notes body, tags) in thousands [kB]
     */
    public Long getUserDataSize(User user) {
        long globalNoteSize = user.getGlobalNote().getBody().length() + user.getGlobalNote().getTags().stream().mapToLong(String::length).sum();
        MongoDatabase db = mongoTemplate.getDb();
        if (user.getNoteIds().isEmpty()) {
            return globalNoteSize / 1000;
        }
        String ids = String.join(",", user.getNoteIds().stream().map(it -> "ObjectId(\"" + it.toString() + "\")").collect(Collectors.toList()));
        Bson command = new Document("eval", "db.notes.aggregate(\n" +
                "    [\n" +
                "        { $match: { \"_id\": { $in: [" + ids + "] } } },\n" +
                "        {\n" +
                "            $unwind: {\n" +
                "                path: \"$tags\",\n" +
                "                preserveNullAndEmptyArrays: true\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            $project: {\n" +
                "                \"body_length\": { $strLenBytes: \"$body\" },\n" +
                "                \"tags_length\": { \n" +
                "                    $cond: [{$not: [\"$tags\"]}, 0, {$strLenBytes: \"$tags\"}]\n" +
                "                }\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            $group: {\n" +
                "                _id: {_id: \"$_id\", body_length:\"$body_length\"},\n" +
                "                \"tags_length\": {$sum: \"$tags_length\"}\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            $group: {\n" +
                "                _id: null,\n" +
                "                \"body_length\": {$sum: \"$_id.body_length\"},\n" +
                "                \"tags_length\": {$sum: \"$tags_length\"},\n" +
                "            }\n" +
                "        }\n" +
                "    ]\n" +
                ")");
        Document result = db.runCommand(command);
        if ("1.0".equals(result.get("ok").toString())) {
            Document returnedDocument = ((Document) result.get("retval"));
            Collection<Document> documents = (Collection<Document>) returnedDocument.get("_batch");
            if (documents.iterator().hasNext()) {
                returnedDocument = documents.iterator().next();
                long bodyLength = Double.valueOf(returnedDocument.get("body_length").toString()).longValue();
                long tagsLength = Double.valueOf(returnedDocument.get("tags_length").toString()).longValue();
                return (bodyLength + tagsLength + globalNoteSize) / 1000;
            }
        }
        return (long) -1e10;
    }

    public class NotesReport {

        private String _id;

        private long bodyLength;
        private long tagsLength;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public long getBodyLength() {
            return bodyLength;
        }

        public void setBodyLength(long bodyLength) {
            this.bodyLength = bodyLength;
        }

        public long getTagsLength() {
            return tagsLength;
        }

        public void setTagsLength(long tagsLength) {
            this.tagsLength = tagsLength;
        }
    }
}
