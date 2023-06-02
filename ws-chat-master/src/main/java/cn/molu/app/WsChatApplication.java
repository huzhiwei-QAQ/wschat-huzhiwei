package cn.molu.app;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;





@SpringBootApplication
public class WsChatApplication {
    private static final Logger log = LogManager.getLogger(WsChatApplication.class);
    public static void main(String[] args) {
        SpringApplication.run(WsChatApplication.class, args);
        log.info("App project start successfully !!!");
        log.info("http://localhost:8080");
    }
}