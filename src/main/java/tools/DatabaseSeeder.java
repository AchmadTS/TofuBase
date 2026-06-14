package tools;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Statement;
import utils.DatabaseConfig;

public class DatabaseSeeder {

    private static final String SEEDER_PATH = "src/main/java/database/seeder/";
    private static final String[] TABLES = {
        "laporan_keuangan",
        "pengeluaran",
        "pemasukan",
        "record_penjualan",
        "penjualan",
        "record_produksi",
        "produksi",
        "inventaris",
        "bahan_baku",
        "admin",
        "owner",
        "staff",
        "produk",
        "supplier",
        "pelanggan",
        "users"
    };

    private static final String[] SEEDERS = {
        "userSeeder.sql",
        "supplierSeeder.sql",
        "pelangganSeeder.sql",
        "produkSeeder.sql",
        "adminSeeder.sql",
        "ownerSeeder.sql",
        "staffSeeder.sql",
//        "bahanBakuSeeder.sql",
//        "produksiSeeder.sql",
//        "recordProduksiSeeder.sql",
//        "penjualanSeeder.sql",
//        "recordPenjualanSeeder.sql",
//        "pemasukanSeeder.sql",
//        "pengeluaranSeeder.sql",
//        "laporanKeuanganSeeder.sql",
//        "inventarisSeeder.sql"
    };

    public static void main(String[] args) {
        try (
            Connection conn = DatabaseConfig.getKoneksi(); Statement stmt = conn.createStatement()) {
            System.out.println("=================================");
            System.out.println("KONEKSI BERHASIL");
            System.out.println("=================================");

            // Disable FK
            stmt.execute("SET FOREIGN_KEY_CHECKS = 0");

            System.out.println("\nMengosongkan tabel...");

            for (String table : TABLES) {
                stmt.execute("TRUNCATE TABLE " + table);
                System.out.println("✓ " + table);
            }

            // Enable FK
            stmt.execute("SET FOREIGN_KEY_CHECKS = 1");

            System.out.println("\n=================================");
            System.out.println("MENJALANKAN SEEDER");
            System.out.println("=================================");

            for (String seeder : SEEDERS) {

                Path filePath = Paths.get(SEEDER_PATH + seeder);

                System.out.println("\nMenjalankan Seeder : " + seeder);

                String sql = Files.readString(filePath);

                stmt.execute(sql);

                System.out.println("✓ Berhasil");
            }

            System.out.println("\n=================================");
            System.out.println("SEMUA SEEDER BERHASIL");
            System.out.println("=================================");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
