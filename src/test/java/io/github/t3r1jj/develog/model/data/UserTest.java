package io.github.t3r1jj.develog.model.data;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserTest {

    @Test
    void infoEquals_DifferentEmail() {
        User user1 = User.builder().id(1L).email("a").build();
        User user2 = User.builder().id(2L).email("b").build();
        assertFalse(user1.infoEquals(user2));
    }

    @Test
    void infoEquals_DifferentName() {
        User user1 = User.builder().id(1L).name("a").build();
        User user2 = User.builder().id(2L).name("b").build();
        assertFalse(user1.infoEquals(user2));
    }

    @Test
    void infoEquals_SameInfoEmail() {
        User user1 = User.builder().id(1L).email("w").name("a").build();
        User user2 = User.builder().id(2L).email("w").name("a").build();
        assertTrue(user1.infoEquals(user2));
    }
}