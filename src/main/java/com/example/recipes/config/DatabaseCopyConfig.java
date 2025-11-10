package com.example.recipes.config;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

@Component
public class DatabaseCopyConfig {

    @PostConstruct
    public void copyDatabase() {
        try {
            Path target = Paths.get(System.getProperty("user.home"), "recipes.db");

            // Copy DB to writable directory if not exists
            if (Files.notExists(target)) {
                try (InputStream is = getClass().getResourceAsStream("/data/recipes.db")) {
                    Files.copy(is, target);
                    System.out.println("✅ Copied SQLite DB to writable location: " + target);
                }
            } else {
                System.out.println("✅ SQLite DB already exists at: " + target);
            }

        } catch (Exception e) {
            System.out.println("⚠️ Failed to copy SQLite DB: " + e.getMessage());
        }
    }
}
