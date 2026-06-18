package dao;

import models.Produksi;
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

public class ProduksiDAO {

    public Map<String, String> getTopCardsData() {
        Map<String, String> data = new HashMap<>();
        data.put("total_produksi", "0");
        data.put("hasil_total", "0");
        data.put("operator_aktif", "0");

        String query = "SELECT COUNT(*) total_produksi, IFNULL(SUM(hasil_tahu), 0) hasil_total, COUNT(DISTINCT id_user) operator_aktif FROM produksi";
        try (Connection conn = DatabaseConfig.getKoneksi(); PreparedStatement ps = conn.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                data.put("total_produksi", FormatUtil.formatAngka(rs.getDouble("total_produksi")));
                data.put("hasil_total", FormatUtil.formatAngka(rs.getDouble("hasil_total")));
                data.put("operator_aktif", FormatUtil.formatAngka(rs.getDouble("operator_aktif")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public int getTableTotalRows(String keyword) {
        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        String query = "SELECT COUNT(*) FROM produksi p JOIN produk pr ON p.id_produk = pr.id_produk"
                + (hasKeyword ? " WHERE pr.nama LIKE ? OR p.batch LIKE ? OR p.status LIKE ?" : "");
        try (Connection conn = DatabaseConfig.getKoneksi(); PreparedStatement ps = conn.prepareStatement(query)) {
            if (hasKeyword) {
                String search = "%" + keyword.trim() + "%";
                ps.setString(1, search);
                ps.setString(2, search);
                ps.setString(3, search);
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
        String query = "SELECT p.id_produksi, p.batch, p.tanggal, pr.nama produk, p.hasil_tahu, p.status FROM produksi p "
                + "JOIN produk pr ON p.id_produk = pr.id_produk"
                + (hasKeyword ? " WHERE pr.nama LIKE ? OR p.batch LIKE ? OR p.status LIKE ?" : "")
                + " ORDER BY p.tanggal DESC LIMIT ? OFFSET ?";
        try (Connection conn = DatabaseConfig.getKoneksi(); PreparedStatement ps = conn.prepareStatement(query)) {
            int idx = 1;
            if (hasKeyword) {
                String search = "%" + keyword.trim() + "%";
                ps.setString(idx++, search);
                ps.setString(idx++, search);
                ps.setString(idx++, search);
            }
            ps.setInt(idx++, limit);
            ps.setInt(idx, offset);
            try (ResultSet rs = ps.executeQuery()) {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.forLanguageTag("id-ID"));
                while (rs.next()) {
                    data.add(new String[]{
                            String.valueOf(rs.getInt("id_produksi")),
                            rs.getString("batch"),
                            sdf.format(rs.getDate("tanggal")),
                            rs.getString("produk"),
                            FormatUtil.formatAngka(rs.getDouble("hasil_tahu")),
                            rs.getString("status"),
                            String.valueOf(rs.getInt("id_produksi"))
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public Produksi getProduksiById(int id) {
        String query = "SELECT p.id_produksi, p.tanggal, p.keterangan, p.hasil_tahu, p.status, pr.nama produk, p.batch, p.id_user "
                + "FROM produksi p JOIN produk pr ON p.id_produk = pr.id_produk WHERE p.id_produksi = ?";
        try (Connection conn = DatabaseConfig.getKoneksi(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Produksi produksi = new Produksi(rs.getInt("id_produksi"), rs.getDate("tanggal"), rs.getString("keterangan"), rs.getDouble("hasil_tahu"), rs.getString("produk"), rs.getString("status"));
                    return produksi;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Produk> getProdukList() {
        List<Produk> list = new ArrayList<>();
        String query = "SELECT id_produk, nama, satuan, harga_jual, jenis, stok FROM produk ORDER BY nama ASC";
        try (Connection conn = DatabaseConfig.getKoneksi(); PreparedStatement ps = conn.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Produk(rs.getInt("id_produk"), rs.getString("nama"), rs.getString("satuan"), rs.getDouble("harga_jual"), rs.getString("jenis"), rs.getDouble("stok")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
