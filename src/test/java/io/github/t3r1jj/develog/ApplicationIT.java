package io.github.t3r1jj.develog;


import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("integration")
class ApplicationIT {

    @Test
    void start() throws InterruptedException {
        Application.main(new String[]{});
        Thread.sleep(100);
    }

}
