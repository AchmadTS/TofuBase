package dao;

import models.Produk;
import utils.DatabaseConfig;
import utils.FormatUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProdukDAO {

    private Boolean jenisColumnAvailable;

    private boolean isJenisColumnAvailable() {
        if (jenisColumnAvailable != null) {
            return jenisColumnAvailable;
        }
        try (Connection conn = DatabaseConfig.getKoneksi()) {
            if (conn == null) {
                jenisColumnAvailable = false;
                return false;
            }
            try (ResultSet rs = conn.getMetaData().getColumns(null, null, "produk", "jenis")) {
                jenisColumnAvailable = rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
            jenisColumnAvailable = false;
        }
        return jenisColumnAvailable;
    }

    public Map<String, String> getTopCardsData() {
        Map<String, String> data = new HashMap<>();
        data.put("total_produk", "0");
        data.put("stok_total", "0");
        data.put("jenis_produk", "0");
        data.put("nilai_stok", "Rp 0");

        String query = "SELECT COUNT(*) total_produk, SUM(stok) stok_total, SUM(stok * harga_jual) nilai_stok FROM produk";
        try (Connection conn = DatabaseConfig.getKoneksi(); PreparedStatement ps = conn.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                data.put("total_produk", FormatUtil.formatAngka(rs.getDouble("total_produk")));
                data.put("stok_total", FormatUtil.formatAngka(rs.getDouble("stok_total")));
                double nilai = rs.getDouble("nilai_stok");
                data.put("nilai_stok", "Rp " + FormatUtil.formatAngka(nilai));
            }
            if (conn != null) {
                String jenisQuery = isJenisColumnAvailable()
                        ? "SELECT COUNT(DISTINCT jenis) AS jenis_produk FROM produk"
                        : "SELECT COUNT(DISTINCT nama) AS jenis_produk FROM produk";
                try (PreparedStatement psJenis = conn.prepareStatement(jenisQuery); ResultSet rsJenis = psJenis.executeQuery()) {
                    if (rsJenis.next()) {
                        data.put("jenis_produk", FormatUtil.formatAngka(rsJenis.getDouble("jenis_produk")));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public int getTableTotalRows(String keyword) {
        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        boolean hasJenis = hasKeyword && isJenisColumnAvailable();
        String query = "SELECT COUNT(*) FROM produk" + (hasKeyword ? " WHERE nama LIKE ? OR CAST(id_produk AS CHAR) LIKE ?" + (hasJenis ? " OR jenis LIKE ?" : "") : "");
        try (Connection conn = DatabaseConfig.getKoneksi(); PreparedStatement ps = conn.prepareStatement(query)) {
            if (hasKeyword) {
                String search = "%" + keyword.trim() + "%";
                ps.setString(1, search);
                ps.setString(2, search);
                if (hasJenis) {
                    ps.setString(3, search);
                }
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<String[]> getTablePageData(int limit, int offset, String keyword) {
        List<String[]> data = new ArrayList<>();
        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        boolean hasJenis = isJenisColumnAvailable();
        String base = hasJenis
                ? "SELECT id_produk, nama, jenis, satuan, harga_jual, stok FROM produk"
                : "SELECT id_produk, nama, '' AS jenis, satuan, harga_jual, stok FROM produk";
        String where = hasKeyword ? " WHERE nama LIKE ? OR CAST(id_produk AS CHAR) LIKE ?" + (hasJenis ? " OR jenis LIKE ?" : "") : "";
        String order = " ORDER BY nama ASC LIMIT ? OFFSET ?";
        try (Connection conn = DatabaseConfig.getKoneksi(); PreparedStatement ps = conn.prepareStatement(base + where + order)) {
            int idx = 1;
            if (hasKeyword) {
                String search = "%" + keyword.trim() + "%";
                ps.setString(idx++, search);
                ps.setString(idx++, search);
                if (hasJenis) {
                    ps.setString(idx++, search);
                }
            }
            ps.setInt(idx++, limit);
            ps.setInt(idx, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String id = String.valueOf(rs.getInt("id_produk"));
                    String nama = rs.getString("nama");
                    String jenis = rs.getString("jenis");
                    String satuan = rs.getString("satuan");
                    String harga = "Rp " + FormatUtil.formatAngka(rs.getDouble("harga_jual"));
                    String stok = FormatUtil.formatAngka(rs.getDouble("stok"));
                    data.add(new String[]{id, nama, jenis, satuan, harga, stok, id});
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public Produk getProdukById(int idProduk) {
        boolean hasJenis = isJenisColumnAvailable();
        String query = hasJenis
                ? "SELECT id_produk, nama, satuan, harga_jual, jenis, stok FROM produk WHERE id_produk = ?"
                : "SELECT id_produk, nama, satuan, harga_jual, '' AS jenis, stok FROM produk WHERE id_produk = ?";
        try (Connection conn = DatabaseConfig.getKoneksi(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, idProduk);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Produk(rs.getInt("id_produk"), rs.getString("nama"), rs.getString("satuan"), rs.getDouble("harga_jual"), rs.getString("jenis"), rs.getDouble("stok"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
