package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/tofubase";
    private static final String DB_USER = "postgres";
    private static final String DB_PASS = "$aTs130425.";

    public static Connection getKoneksi() {
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);

        } catch (ClassNotFoundException e) {
            System.err.println("ERROR: Driver PostgreSQL tidak ditemukan! Reload Maven/pom.xml dulu.");
            e.printStackTrace();

        } catch (SQLException e) {
            System.err.println("ERROR: Gagal terhubung ke database.");
            System.err.println("Pastikan service PostgreSQL sudah berjalan dan cek kredensial (user/pass/nama db)!");
            e.printStackTrace();
        }
        return null;
    }
}
