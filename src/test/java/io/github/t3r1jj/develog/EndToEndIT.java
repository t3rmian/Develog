package io.github.t3r1jj.develog;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@Configuration
class EndToEndIT {

//    @Autowired
//    private URI siteBase;

//    @Autowired
//    private WebDriver webDriver;
//
//    @Bean
//    public WebDriver webDriver() {
//        return new FirefoxDriver();
//    }

//    @Bean
//    public URI siteBase() throws URISyntaxException {
//        return new URI("http://localhost:8080");
//    }
//
//    @Test
//    @Disabled
//    void testWeSeeHelloWorld() {
//        webDriver.get(siteBase.toString());
//        assertTrue(webDriver.getPageSource().contains("Hello World"));
//    }

}
