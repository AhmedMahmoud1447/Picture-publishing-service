package com.ahmed.picturepublishingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class PicturePublishingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PicturePublishingServiceApplication.class, args);
    }

}
