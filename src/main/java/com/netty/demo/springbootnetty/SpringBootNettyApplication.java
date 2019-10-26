package com.netty.demo.springbootnetty;

import com.netty.demo.springbootnetty.server.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBootNettyApplication implements CommandLineRunner {

    @Autowired
    private Server server;

    public static void main(String[] args) {
        SpringApplication.run(SpringBootNettyApplication.class, args);
    }

    @Override
    public void run(String... args) {
        Thread thread = new Thread() {
            public void run() {
                server.start();
            }
        };
        thread.run();
    }

}
