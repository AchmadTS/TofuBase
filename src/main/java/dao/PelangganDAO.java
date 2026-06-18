package dao;

import models.Pelanggan;
import utils.DatabaseConfig;
import utils.FormatUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PelangganDAO {

    public Map<String, String> getTopCardsData() {
        Map<String, String> data = new HashMap<>();
        data.put("total_pelanggan", "0");
        data.put("omset_tertinggi", "Rp 0");
        data.put("pembelian_rata", "Rp 0");

        String query = "SELECT COUNT(*) total_pelanggan, IFNULL(MAX(total), 0) omset_tertinggi, IFNULL(AVG(total), 0) pembelian_rata "
                + "FROM penjualan";
        try (Connection conn = DatabaseConfig.getKoneksi(); PreparedStatement ps = conn.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                data.put("total_pelanggan", FormatUtil.formatAngka(rs.getDouble("total_pelanggan")));
                data.put("omset_tertinggi", "Rp " + FormatUtil.formatAngka(rs.getDouble("omset_tertinggi")));
                data.put("pembelian_rata", "Rp " + FormatUtil.formatAngka(rs.getDouble("pembelian_rata")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public int getTableTotalRows(String keyword) {
        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        String query = "SELECT COUNT(*) FROM pelanggan" + (hasKeyword ? " WHERE nama LIKE ? OR CAST(id_pelanggan AS CHAR) LIKE ? OR no_telp LIKE ?" : "");
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
        String query = "SELECT id_pelanggan, nama, alamat, no_telp, email FROM pelanggan" 
                + (hasKeyword ? " WHERE nama LIKE ? OR CAST(id_pelanggan AS CHAR) LIKE ? OR no_telp LIKE ?" : "")
                + " ORDER BY nama ASC LIMIT ? OFFSET ?";
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
                while (rs.next()) {
                    data.add(new String[] {
                            String.valueOf(rs.getInt("id_pelanggan")),
                            rs.getString("nama"),
                            rs.getString("alamat"),
                            rs.getString("no_telp"),
                            rs.getString("email"),
                            String.valueOf(rs.getInt("id_pelanggan"))
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public Pelanggan getPelangganById(int id) {
        String query = "SELECT id_pelanggan, nama, alamat, no_telp, email FROM pelanggan WHERE id_pelanggan = ?";
        try (Connection conn = DatabaseConfig.getKoneksi(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Pelanggan(rs.getInt("id_pelanggan"), rs.getString("nama"), rs.getString("alamat"), rs.getString("no_telp"), rs.getString("email"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
