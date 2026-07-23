package dao;

import models.Pemasukan;
import utils.DatabaseConfig;
import utils.FormatUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PemasukanDAO {

    public Map<String, String> getTopCardsData() {
        Map<String, String> data = new HashMap<>();
        data.put("total_pemasukan", "Rp 0");
        data.put("jumlah_transaksi", "0");
        data.put("rata_rata", "Rp 0");

        String query = "SELECT COALESCE(SUM(jumlah), 0) AS total, COUNT(*) AS jumlah, COALESCE(AVG(jumlah), 0) AS rata FROM pemasukan";
        try (Connection conn = DatabaseConfig.getKoneksi();
                PreparedStatement ps = conn.prepareStatement(query);
                ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                data.put("total_pemasukan", "Rp " + FormatUtil.formatAngka(rs.getDouble("total")));
                data.put("jumlah_transaksi", FormatUtil.formatAngka(rs.getDouble("jumlah")));
                data.put("rata_rata", "Rp " + FormatUtil.formatAngka(rs.getDouble("rata")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public int getTableTotalRows(String keyword) {
        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        String query = "SELECT COUNT(*) FROM pemasukan"
                + (hasKeyword ? " WHERE sumber ILIKE ? OR keterangan ILIKE ? OR CAST(id_pemasukan AS TEXT) ILIKE ?"
                        : "");
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
        String query = "SELECT id_pemasukan, tanggal, sumber, jumlah, keterangan FROM pemasukan"
                + (hasKeyword ? " WHERE sumber ILIKE ? OR keterangan ILIKE ? OR CAST(id_pemasukan AS TEXT) ILIKE ?"
                        : "")
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
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd MMM yyyy",
                        java.util.Locale.forLanguageTag("id-ID"));
                while (rs.next()) {
                    data.add(new String[] {
                            String.valueOf(rs.getInt("id_pemasukan")),
                            sdf.format(rs.getDate("tanggal")),
                            rs.getString("sumber"),
                            "Rp " + FormatUtil.formatAngka(rs.getDouble("jumlah")),
                            rs.getString("keterangan"),
                            String.valueOf(rs.getInt("id_pemasukan"))
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public Pemasukan getPemasukanById(int id) {
        String query = "SELECT id_pemasukan, id_penjualan, tanggal, sumber, jumlah, keterangan FROM pemasukan WHERE id_pemasukan = ?";
        try (Connection conn = DatabaseConfig.getKoneksi(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Pemasukan(rs.getInt("id_pemasukan"), rs.getInt("id_penjualan"), rs.getDate("tanggal"),
                            rs.getString("sumber"), rs.getDouble("jumlah"), rs.getString("keterangan"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insertPemasukan(Pemasukan pemasukan) {        
        String query = "INSERT INTO pemasukan (id_penjualan, tanggal, sumber, jumlah, keterangan) VALUES ((SELECT id_penjualan FROM penjualan LIMIT 1), ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getKoneksi(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setDate(1, new java.sql.Date(pemasukan.getTanggal().getTime()));
            ps.setString(2, pemasukan.getSumber());
            ps.setDouble(3, pemasukan.getJumlah());
            ps.setString(4, pemasukan.getKeterangan());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
