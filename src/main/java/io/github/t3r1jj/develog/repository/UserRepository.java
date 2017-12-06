package io.github.t3r1jj.develog.repository;


import io.github.t3r1jj.develog.domain.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, String> {
}
