package com.example.idatt2106_2022_05_backend.integration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.Nested;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class RentalIntegrationTest {


    @Nested

    @Test
    public void rentalSaved_WhenForeignKeysCorrect() {

        // Test rental
        // Test service

    }

    @Test
    public void rentalNotSaved_WhenForeignKeysWrong() {

    }


    @Test
    public void rentalCanBeCancelled_Before24Hrs() {

    }

    @Test
    public void rentalCanNotBeCancelled_After24Hrs() {

    }
}
