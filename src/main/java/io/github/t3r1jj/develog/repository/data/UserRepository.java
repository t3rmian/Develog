package io.github.t3r1jj.develog.repository.data;

import io.github.t3r1jj.develog.model.data.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = "SELECT pg_database_size('dev')", nativeQuery = true)
    Map<String, Object> getDbSize();

    @Query(value = "SELECT SUM(LENGTH(globalNote.body))+COALESCE(SUM(LENGTH(notes.body)), 0)+(SELECT COALESCE(SUM(LENGTH(tags.id.value)), 0) FROM u, tags tags WHERE u.id = tags.id.userId)" +
            " FROM users u LEFT JOIN u.globalNote globalNote LEFT JOIN u.notes notes WHERE u.id = :id")
    long getUserDataSize(@Param("id") Long id);

    @Query(value = "SELECT u, SUM(LENGTH(globalNote.body))+COALESCE(SUM(LENGTH(notes.body)), 0)+(SELECT COALESCE(SUM(LENGTH(tags.id.value)), 0) FROM u, tags tags WHERE u.id = tags.id.userId)" +
            " FROM users u LEFT JOIN u.globalNote globalNote LEFT JOIN u.notes notes" +
            " GROUP BY u"
    )
    List<Object[]> findAllUsersDataSize();

}
