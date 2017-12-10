package io.github.t3r1jj.develog.model.data;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class DataTest {

    @Test
    void noteEqualsContract() {
        EqualsVerifier.forClass(Note.class);
    }

    @Test
    void userEqualsContract() {
        EqualsVerifier.forClass(User.class);
    }

    @Test
    void tagEqualsContract() {
        EqualsVerifier.forClass(Tag.class);
    }

    @Test
    void tagIdEqualsContract() {
        EqualsVerifier.forClass(Tag.TagId.class);
    }

}