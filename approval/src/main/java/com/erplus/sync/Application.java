package com.erplus.sync;

import com.erplus.sync.utils.ForwardPortUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {


    public static void main(String[] args) {
        ForwardPortUtils.forwardMysqlPort();
        ForwardPortUtils.forwardEsPort();
        SpringApplication.run(Application.class, args);
    }

}
