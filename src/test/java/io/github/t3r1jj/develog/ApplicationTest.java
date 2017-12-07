package io.github.t3r1jj.develog;


import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@Tag("unit")
class ApplicationTest {

    @Test
    void start() {
        ArrayList mockedObject = mock(ArrayList.class);
        mockedObject.get(2);
        verify(mockedObject).get(2);
    }

}
