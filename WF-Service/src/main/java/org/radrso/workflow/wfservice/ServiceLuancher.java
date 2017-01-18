package org.radrso.workflow.wfservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by raomengnan on 17-1-18.
 */
@SpringBootApplication(scanBasePackages = {"org.radrso.workflow.wfservice.repositories"})
@EnableAutoConfiguration
public class ServiceLuancher {
    public static void main(String[] args) {
        SpringApplication.run(ServiceLuancher.class, args);
    }
}
