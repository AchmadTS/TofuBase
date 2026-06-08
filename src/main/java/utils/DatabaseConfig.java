package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/tofubase";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

    private static Connection koneksi;

    public static Connection getKoneksi() {
        if (koneksi == null) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                koneksi = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                System.out.println("KONEKSI BERHASIL");
            } catch (ClassNotFoundException e) {
                System.err.println("ERROR: Driver MySQL tidak ditemukan! Build pom.xml dulu");
                e.printStackTrace();
            } catch (SQLException e) {
                System.err.println("ERROR: Gagal terhubung ke db");
                System.err.println("Hidupkan XAMPP & cek nama database");
                e.printStackTrace();
            }
        }
        return koneksi;
    }

    // Cuma untuk tes koneksi
    public static void main(String[] args) {
        getKoneksi();
    }
}
