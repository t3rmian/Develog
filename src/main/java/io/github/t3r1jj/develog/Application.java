package io.github.t3r1jj.develog;

import io.github.t3r1jj.develog.component.UserUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@SpringBootApplication
@ComponentScan
@EnableScheduling
@EnableAutoConfiguration
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }


    @Configuration
    static class Security extends WebSecurityConfigurerAdapter {
        private final UserUpdater userUpdater;

        @Autowired
        public Security(UserUpdater userUpdater) {
            this.userUpdater = userUpdater;
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .authorizeRequests()
                    .antMatchers("/", "/owr/*", "/about/*", "/img/*", "/webjars/material-design-icons/**", "/error/*").permitAll()
                    .anyRequest().authenticated()
                    .and()
                    .formLogin()
                    .loginPage("/")
                    .and()
                    .logout()
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                    .logoutSuccessUrl("/")
                    .clearAuthentication(true)
                    .invalidateHttpSession(true)
                    .and()
                    .oauth2Login()
                    .successHandler(userUpdater);
        }
    }
}
