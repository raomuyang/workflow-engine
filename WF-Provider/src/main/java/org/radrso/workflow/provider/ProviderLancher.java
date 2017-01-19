package org.radrso.workflow.provider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by raomengnan on 17-1-18.
 */
@ImportResource("classpath:dubbo-provider.xml")
@SpringBootApplication(scanBasePackages = {"org.radrso.workflow.provider"})
@RestController
@EnableAutoConfiguration
public class ProviderLancher {

    @RequestMapping("/")
    String home() {
        return "Hello World!";
    }
    public static void main(String[] args) {
        SpringApplication.run(ProviderLancher.class, args);

    }
}
