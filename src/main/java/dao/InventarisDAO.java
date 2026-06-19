package dao;

import models.Inventaris;
import utils.DatabaseConfig;
import utils.FormatUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventarisDAO {

    public Map<String, String> getTopCardsData() {
        Map<String, String> data = new HashMap<>();
        data.put("total_cek", "0");
        data.put("terakhir_cek", "-");
        data.put("aktivitas_bulanan", "0");

        String query = "SELECT COUNT(*) total_cek, IFNULL(MAX(tanggal_cek), '-') terakhir_cek, "
                + "SUM(CASE WHEN MONTH(tanggal_cek) = MONTH(CURDATE()) AND YEAR(tanggal_cek) = YEAR(CURDATE()) THEN 1 ELSE 0 END) aktivitas_bulanan "
                + "FROM inventaris";
        try (Connection conn = DatabaseConfig.getKoneksi(); PreparedStatement ps = conn.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                data.put("total_cek", FormatUtil.formatAngka(rs.getDouble("total_cek")));
                data.put("terakhir_cek", rs.getString("terakhir_cek"));
                data.put("aktivitas_bulanan", FormatUtil.formatAngka(rs.getDouble("aktivitas_bulanan")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public int getTableTotalRows(String keyword) {
        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        String query = "SELECT COUNT(*) FROM inventaris" + (hasKeyword ? " WHERE keterangan LIKE ? OR CAST(id_inventaris AS CHAR) LIKE ?" : "");
        try (Connection conn = DatabaseConfig.getKoneksi(); PreparedStatement ps = conn.prepareStatement(query)) {
            if (hasKeyword) {
                String search = "%" + keyword.trim() + "%";
                ps.setString(1, search);
                ps.setString(2, search);
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
        String query = "SELECT id_inventaris, tanggal_cek, keterangan FROM inventaris"
                + (hasKeyword ? " WHERE keterangan LIKE ? OR CAST(id_inventaris AS CHAR) LIKE ?" : "")
                + " ORDER BY tanggal_cek DESC LIMIT ? OFFSET ?";
        try (Connection conn = DatabaseConfig.getKoneksi(); PreparedStatement ps = conn.prepareStatement(query)) {
            int idx = 1;
            if (hasKeyword) {
                String search = "%" + keyword.trim() + "%";
                ps.setString(idx++, search);
                ps.setString(idx++, search);
            }
            ps.setInt(idx++, limit);
            ps.setInt(idx, offset);
            try (ResultSet rs = ps.executeQuery()) {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.forLanguageTag("id-ID"));
                while (rs.next()) {
                    data.add(new String[]{
                            String.valueOf(rs.getInt("id_inventaris")),
                            sdf.format(rs.getDate("tanggal_cek")),
                            rs.getString("keterangan"),
                            String.valueOf(rs.getInt("id_inventaris"))
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public Inventaris getInventarisById(int id) {
        String query = "SELECT id_inventaris, tanggal_cek, keterangan FROM inventaris WHERE id_inventaris = ?";
        try (Connection conn = DatabaseConfig.getKoneksi(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Inventaris(rs.getInt("id_inventaris"), rs.getDate("tanggal_cek"), rs.getString("keterangan"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insertInventaris(String keterangan) {
        String query = "INSERT INTO inventaris (tanggal_cek, keterangan) VALUES (CURDATE(), ?)";
        try (Connection conn = DatabaseConfig.getKoneksi(); 
            PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, keterangan);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
