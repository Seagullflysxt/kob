package com.kob.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BackendApplicationTests {

    @Test
    void contextLoads() {
        PasswordEncoder pe = new BCryptPasswordEncoder();
        System.out.println(pe.encode("psxt"));
        System.out.println(pe.encode("pc"));
        System.out.println(pe.encode("pd"));
        System.out.println(pe.encode("pe"));
        System.out.println(pe.encode("pf"));
    }

}
