package io.github.t3r1jj.develog;


import org.junit.jupiter.api.Test;

class ApplicationTest {

    @Test
    void start() throws InterruptedException {
        Application.main(new String[]{});
        Thread.sleep(100);
    }

}
