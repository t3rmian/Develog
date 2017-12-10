package io.github.t3r1jj.develog.repository;


import io.github.t3r1jj.develog.model.data.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
