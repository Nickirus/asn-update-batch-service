package com.nikitov.asn.update.service.batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@SpringBootApplication
@RefreshScope
public class AsnUpdateServiceBatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(AsnUpdateServiceBatchApplication.class, args);
    }
}
