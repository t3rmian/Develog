package io.github.t3r1jj.develog;


import org.junit.jupiter.api.Test;

class ApplicationIT {

    @Test
    void start() throws InterruptedException {
        Thread runner = new Thread(() -> {  // Workaround for SilentExitException caused by spring-boot-devtools restart
            Application.main(new String[]{});
        });
        runner.start();
        runner.join();
    }

}
