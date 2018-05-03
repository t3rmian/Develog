package io.github.t3r1jj.develog.repository.data;

import io.github.t3r1jj.develog.model.data.Note;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Set;

public interface NoteRepository extends MongoRepository<Note, ObjectId> {

    @Query(value = "{ '_id' : { $in : ?0 } }", fields = "{ 'tags': 1, '_id': 0 }")
    List<TagsProjection> findAllProjectedTagsById(Iterable<ObjectId> iterable);

    interface TagsProjection {
        Set<String> getTags();
    }

}
