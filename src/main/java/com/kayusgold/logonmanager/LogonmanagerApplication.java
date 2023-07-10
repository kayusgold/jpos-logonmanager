package com.kayusgold.logonmanager;

import org.jpos.q2.Q2;
import org.jpos.q2.iso.QMUX;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LogonmanagerApplication implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(LogonmanagerApplication.class, args);
    }

    private static QMUX mux;

    @Override
    public void run(String... args) throws Exception {
        Q2 q2 = new Q2();

        //start q2 in thread
        Thread t = new Thread(q2);
        t.start();

    }
}
