package io.github.t3r1jj.develog.repository.data;

import io.github.t3r1jj.develog.model.data.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface UserRepository extends MongoRepository<User, Long> {

    @Query(value = "{}", fields = "{ globalNote: 0, noteIds: 0 }")
    List<User> findAllUsers();

    List<EmailProjection> findAllProjectedDistinctEmailBy();

    interface EmailProjection {
        String getEmail();
    }

}
