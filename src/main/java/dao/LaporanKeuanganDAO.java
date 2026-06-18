package dao;

import utils.DatabaseConfig;
import utils.FormatUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LaporanKeuanganDAO {

    public Map<String, String> getTopCardsData() {
        Map<String, String> data = new HashMap<>();
        data.put("total_laporan", "0");
        data.put("saldo_terakhir", "Rp 0");
        data.put("periode_terbaru", "-");

        String query = "SELECT COUNT(*) total_laporan, IFNULL(MAX(saldo), 0) saldo_terakhir, "
                + "IFNULL(MAX(CONCAT(periode_awal, ' - ', periode_akhir)), '-') periode_terbaru FROM laporan_keuangan";
        try (Connection conn = DatabaseConfig.getKoneksi(); PreparedStatement ps = conn.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                data.put("total_laporan", FormatUtil.formatAngka(rs.getDouble("total_laporan")));
                data.put("saldo_terakhir", "Rp " + FormatUtil.formatAngka(rs.getDouble("saldo_terakhir")));
                data.put("periode_terbaru", rs.getString("periode_terbaru"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public int getTableTotalRows(String keyword) {
        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        String query = "SELECT COUNT(*) FROM laporan_keuangan"
                + (hasKeyword ? " WHERE periode_awal LIKE ? OR periode_akhir LIKE ? OR CAST(id_laporan AS CHAR) LIKE ?" : "");
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
        String query = "SELECT id_laporan, periode_awal, periode_akhir, total_pemasukan, total_pengeluaran, saldo FROM laporan_keuangan"
                + (hasKeyword ? " WHERE periode_awal LIKE ? OR periode_akhir LIKE ? OR CAST(id_laporan AS CHAR) LIKE ?" : "")
                + " ORDER BY id_laporan DESC LIMIT ? OFFSET ?";
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
                    data.add(new String[]{
                            String.valueOf(rs.getInt("id_laporan")),
                            rs.getString("periode_awal"),
                            rs.getString("periode_akhir"),
                            "Rp " + FormatUtil.formatAngka(rs.getDouble("total_pemasukan")),
                            "Rp " + FormatUtil.formatAngka(rs.getDouble("total_pengeluaran")),
                            "Rp " + FormatUtil.formatAngka(rs.getDouble("saldo")),
                            String.valueOf(rs.getInt("id_laporan"))
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }
}
