package dao;

import models.Pengeluaran;
import utils.DatabaseConfig;
import utils.FormatUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PengeluaranDAO {

    public Map<String, String> getTopCardsData() {
        Map<String, String> data = new HashMap<>();
        data.put("total_pengeluaran", "Rp 0");
        data.put("kategori_utama", "-");
        data.put("jumlah_transaksi", "0");

        String query = "SELECT IFNULL(SUM(jumlah), 0) total, COUNT(*) jumlah, "
                + "IFNULL((SELECT kategori FROM pengeluaran GROUP BY kategori ORDER BY SUM(jumlah) DESC LIMIT 1), '-') kategori_utama "
                + "FROM pengeluaran";
        try (Connection conn = DatabaseConfig.getKoneksi(); PreparedStatement ps = conn.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                data.put("total_pengeluaran", "Rp " + FormatUtil.formatAngka(rs.getDouble("total")));
                data.put("jumlah_transaksi", FormatUtil.formatAngka(rs.getDouble("jumlah")));
                data.put("kategori_utama", rs.getString("kategori_utama"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public int getTableTotalRows(String keyword) {
        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        String query = "SELECT COUNT(*) FROM pengeluaran" + (hasKeyword ? " WHERE kategori LIKE ? OR deskripsi LIKE ? OR CAST(id_pengeluaran AS CHAR) LIKE ?" : "");
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
        String query = "SELECT id_pengeluaran, tanggal, kategori, deskripsi, jumlah FROM pengeluaran"
                + (hasKeyword ? " WHERE kategori LIKE ? OR deskripsi LIKE ? OR CAST(id_pengeluaran AS CHAR) LIKE ?" : "")
                + " ORDER BY tanggal DESC LIMIT ? OFFSET ?";
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
                            String.valueOf(rs.getInt("id_pengeluaran")),
                            sdf.format(rs.getDate("tanggal")),
                            rs.getString("kategori"),
                            rs.getString("deskripsi"),
                            "Rp " + FormatUtil.formatAngka(rs.getDouble("jumlah")),
                            String.valueOf(rs.getInt("id_pengeluaran"))
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public Pengeluaran getPengeluaranById(int id) {
        String query = "SELECT id_pengeluaran, tanggal, kategori, deskripsi, jumlah FROM pengeluaran WHERE id_pengeluaran = ?";
        try (Connection conn = DatabaseConfig.getKoneksi(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Pengeluaran(rs.getInt("id_pengeluaran"), rs.getDate("tanggal"), rs.getString("kategori"), rs.getString("deskripsi"), rs.getDouble("jumlah"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insertPengeluaran(Pengeluaran pengeluaran) {
        String query = "INSERT INTO pengeluaran (tanggal, kategori, deskripsi, jumlah) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getKoneksi(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setDate(1, new java.sql.Date(pengeluaran.getTanggal().getTime()));
            ps.setString(2, pengeluaran.getKategori());
            ps.setString(3, pengeluaran.getDeskripsi());
            ps.setDouble(4, pengeluaran.getJumlah());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
