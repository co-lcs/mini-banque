package io.github.co_lcs.mini_banque.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

@Configuration
public class AppConfig {

    @Bean
    public Clock clock(){
        return Clock.system(ZoneId.of("Europe/Paris")); // Paris parce que je suis en France
    }
}
