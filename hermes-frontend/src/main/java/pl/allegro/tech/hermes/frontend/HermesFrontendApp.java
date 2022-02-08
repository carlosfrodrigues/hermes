package pl.allegro.tech.hermes.frontend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class HermesFrontendApp {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(HermesFrontendApp.class);
        application.setWebApplicationType(WebApplicationType.NONE);
        application.run(args);
    }
}