package org.radrso.workflow.provider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

/**
 * Created by raomengnan on 17-1-18.
 */
@ImportResource("/dubbo-provider.xml")
@SpringBootApplication(scanBasePackages = {"org.radrso.workflow.provider"})
@EnableAutoConfiguration
public class ProviderLancher {

    public static void main(String[] args) {
        System.out.println("[DEBUG] " + ProviderLancher.class.getResource("/"));
        SpringApplication.run(ProviderLancher.class, args);

    }
}
