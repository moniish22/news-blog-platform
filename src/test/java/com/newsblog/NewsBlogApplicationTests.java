package com.newsblog;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class NewsBlogApplicationTests {
    @Test
    void contextLoads() {
        // Starts the whole application against an in-memory H2 database and seeds demo data.
    }
}
