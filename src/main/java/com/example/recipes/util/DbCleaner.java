package com.example.recipes.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Utility to remove any recipe titled "Sweet Potato Pie" from the SQLite DB.
 * This is a one-off tool you can run from the project root after building
 * dependencies. It connects to jdbc:sqlite:recipes.db and deletes matching rows.
 */
public class DbCleaner {

    public static void main(String[] args) throws Exception {
        String url = "jdbc:sqlite:recipes.db";
        try (Connection c = DriverManager.getConnection(url)) {
            c.setAutoCommit(true);
            String countSql = "SELECT COUNT(*) FROM recipes WHERE lower(title) = ?";
            try (PreparedStatement ps = c.prepareStatement(countSql)) {
                ps.setString(1, "sweet potato pie");
                try (ResultSet rs = ps.executeQuery()) {
                    int found = rs.next() ? rs.getInt(1) : 0;
                    System.out.println("Found records: " + found);
                }
            }

            String delSql = "DELETE FROM recipes WHERE lower(title) = ?";
            try (PreparedStatement ps = c.prepareStatement(delSql)) {
                ps.setString(1, "sweet potato pie");
                int deleted = ps.executeUpdate();
                System.out.println("Deleted records: " + deleted);
            }
        }
    }
}
