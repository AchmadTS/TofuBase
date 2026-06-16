package dao;

import utils.DatabaseConfig;
import utils.FormatUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import models.Produksi;
import models.RecordProduksi;

public class DashboardDAO {

    public Map<String, String> getTopCardsData() {
        Map<String, String> data = new HashMap<>();
        try (Connection conn = DatabaseConfig.getKoneksi(); Statement stmt = conn.createStatement()) {
            try (ResultSet rsProd = stmt.executeQuery("SELECT SUM(hasil_tahu) AS total FROM produksi WHERE tanggal = CURDATE()")) {
                data.put("produksi", (rsProd.next() && rsProd.getObject("total") != null) ? FormatUtil.formatAngka(rsProd.getDouble("total")) : "0");
            }

            try (ResultSet rsKed = stmt.executeQuery("SELECT SUM(stok) AS total_stok FROM bahan_baku WHERE nama LIKE '%Kedelai%'")) {
                data.put("stok", (rsKed.next() && rsKed.getObject("total_stok") != null) ? FormatUtil.formatAngka(rsKed.getDouble("total_stok")) : "0");
            }

            try (ResultSet rsPend = stmt.executeQuery("SELECT SUM(total) AS total FROM penjualan WHERE tanggal >= DATE_FORMAT(CURDATE(), '%Y-%m-01') AND tanggal <= LAST_DAY(CURDATE())")) {
                double totalPendapatan = (rsPend.next() && rsPend.getObject("total") != null) ? rsPend.getDouble("total") / 1000000.0 : 0.0;
                data.put("pendapatan", "Rp " + FormatUtil.formatAngka(totalPendapatan));
            }

            try (ResultSet rsTahu = stmt.executeQuery("SELECT SUM(stok) AS total FROM produk")) {
                data.put("tahu", (rsTahu.next() && rsTahu.getObject("total") != null) ? FormatUtil.formatAngka(rsTahu.getDouble("total")) : "0");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public List<Object[]> getChartData(String timeframe) {
        List<Object[]> data = new ArrayList<>();
        String query;
        if ("1W".equals(timeframe)) {
            query = "SELECT DATE_FORMAT(tanggal, '%d %b') as label, SUM(hasil_tahu) as total FROM produksi WHERE tanggal >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) GROUP BY tanggal ORDER BY tanggal ASC";
        } else if ("1M".equals(timeframe)) {
            query = "SELECT DATE_FORMAT(tanggal, '%d %b') as label, SUM(hasil_tahu) as total FROM produksi WHERE tanggal >= DATE_SUB(CURDATE(), INTERVAL 1 MONTH) GROUP BY tanggal ORDER BY tanggal ASC";
        } else if ("3M".equals(timeframe)) {
            query = "SELECT DATE_FORMAT(tanggal, '%b %Y') as label, SUM(hasil_tahu) as total FROM produksi WHERE tanggal >= DATE_SUB(CURDATE(), INTERVAL 3 MONTH) GROUP BY YEAR(tanggal), MONTH(tanggal) ORDER BY YEAR(tanggal), MONTH(tanggal) ASC";
        } else {
            query = "SELECT DATE_FORMAT(tanggal, '%b %Y') as label, SUM(hasil_tahu) as total FROM produksi GROUP BY YEAR(tanggal), MONTH(tanggal) ORDER BY YEAR(tanggal), MONTH(tanggal) ASC";
        }

        try (Connection conn = DatabaseConfig.getKoneksi(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                data.add(new Object[]{rs.getString("label"), rs.getInt("total")});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public List<String[]> getStatusStokData() {
        List<String[]> list = new ArrayList<>();
        String query = "SELECT nama, satuan, MAX(min_stok) as batas_stok, SUM(stok) as total_stok FROM bahan_baku GROUP BY nama, satuan ORDER BY nama ASC";
        try (Connection conn = DatabaseConfig.getKoneksi(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                list.add(new String[]{
                    rs.getString("nama"),
                    rs.getString("satuan"),
                    String.valueOf(rs.getDouble("batas_stok")),
                    String.valueOf(rs.getDouble("total_stok"))
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public int getTableTotalRows(String keyword) {
        String query = keyword.isEmpty() ? "SELECT COUNT(id_produksi) AS total FROM produksi" : "SELECT COUNT(p.id_produksi) AS total FROM produksi p JOIN users u ON p.id_user = u.id_user WHERE p.batch LIKE '" + keyword + "%' OR u.nama LIKE '%" + keyword + "%' OR p.status LIKE '" + keyword + "%'";
        try (Connection conn = DatabaseConfig.getKoneksi(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<String[]> getTablePageData(int limit, int offset, String keyword) {
        List<String[]> data = new ArrayList<>();
        String query;
        if (keyword.isEmpty()) {
            query = "SELECT p.tanggal, p.batch, p.hasil_tahu, p.status, u.nama AS nama_operator, "
                    + "(SELECT SUM(rp.jumlah) FROM record_produksi rp WHERE rp.id_produksi = p.id_produksi AND rp.id_bahan IN (SELECT id_bahan FROM bahan_baku WHERE nama LIKE '%Kedelai%')) AS jumlah_kedelai, "
                    + "(SELECT MAX(bb.satuan) FROM record_produksi rp JOIN bahan_baku bb ON rp.id_bahan = bb.id_bahan WHERE rp.id_produksi = p.id_produksi AND bb.nama LIKE '%Kedelai%') AS satuan_kedelai "
                    + "FROM (SELECT id_produksi FROM produksi ORDER BY tanggal DESC, id_produksi DESC LIMIT " + limit + " OFFSET " + offset + ") AS filter_p "
                    + "JOIN produksi p ON p.id_produksi = filter_p.id_produksi "
                    + "JOIN users u ON p.id_user = u.id_user ORDER BY p.tanggal DESC, p.id_produksi DESC";
        } else {
            query = "SELECT p.tanggal, p.batch, p.hasil_tahu, p.status, u.nama AS nama_operator, "
                    + "(SELECT SUM(rp.jumlah) FROM record_produksi rp WHERE rp.id_produksi = p.id_produksi AND rp.id_bahan IN (SELECT id_bahan FROM bahan_baku WHERE nama LIKE '%Kedelai%')) AS jumlah_kedelai, "
                    + "(SELECT MAX(bb.satuan) FROM record_produksi rp JOIN bahan_baku bb ON rp.id_bahan = bb.id_bahan WHERE rp.id_produksi = p.id_produksi AND bb.nama LIKE '%Kedelai%') AS satuan_kedelai "
                    + "FROM produksi p JOIN users u ON p.id_user = u.id_user "
                    + "WHERE p.batch LIKE '" + keyword + "%' OR u.nama LIKE '%" + keyword + "%' OR p.status LIKE '" + keyword + "%' "
                    + "ORDER BY p.tanggal DESC, p.id_produksi DESC LIMIT " + limit + " OFFSET " + offset;
        }

        try (Connection conn = DatabaseConfig.getKoneksi(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd MMM yyyy");
            while (rs.next()) {
                java.sql.Date dbDate = rs.getDate("tanggal");
                String date = (dbDate != null) ? sdf.format(dbDate) : "-";
                String batch = rs.getString("batch");
                String hasil = FormatUtil.formatAngka(rs.getDouble("hasil_tahu")) + " potong";
                String kedelai = "-";

                if (rs.getObject("jumlah_kedelai") != null) {
                    double jumlah = rs.getDouble("jumlah_kedelai");
                    String satuan = rs.getString("satuan_kedelai");
                    kedelai = FormatUtil.formatAngka(jumlah) + " " + (satuan != null ? satuan.trim() : "");
                }

                String operator = rs.getString("nama_operator");
                if (operator != null && operator.contains(" ")) {
                    operator = operator.split(" ")[0];
                }
                data.add(new String[]{date, batch, kedelai, hasil, operator, rs.getString("status")});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public List<Produksi> getListProduksiLengkap(int limit, int offset, String keyword) {
        List<Produksi> listProduksi = new ArrayList<>();
        String search = "%" + keyword + "%";
        String query = "SELECT p.id_produksi, p.tanggal, p.keterangan, p.hasil_tahu, p.status, u.nama as nama_operator " + "FROM produksi p JOIN users u ON p.id_user = u.id_user " + "WHERE p.batch LIKE ? OR p.status LIKE ? OR u.nama LIKE ? " + "ORDER BY p.tanggal DESC LIMIT ? OFFSET ?";
        try (Connection conn = DatabaseConfig.getKoneksi(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, search);
            ps.setString(2, search);
            ps.setString(3, search);
            ps.setInt(4, limit);
            ps.setInt(5, offset);
            List<Integer> ids = new ArrayList<>();

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Produksi p = new Produksi(rs.getInt("id_produksi"), rs.getDate("tanggal"), rs.getString("keterangan"), rs.getDouble("hasil_tahu"), rs.getString("nama_operator"), rs.getString("status"));
                    listProduksi.add(p);
                    ids.add(p.getIdProduksi());
                }
            }
            if (!ids.isEmpty()) {
                String idList = ids.toString().replace("[", "").replace("]", "");
                String queryRecord = "SELECT rp.id_produksi, rp.id_record_produksi, rp.jumlah, bb.satuan FROM record_produksi rp " + "JOIN bahan_baku bb ON rp.id_bahan = bb.id_bahan WHERE rp.id_produksi IN (" + idList + ")";
                try (Statement st = conn.createStatement(); ResultSet rsRec = st.executeQuery(queryRecord)) {
                    while (rsRec.next()) {
                        int idProd = rsRec.getInt("id_produksi");
                        for (Produksi p : listProduksi) {
                            if (p.getIdProduksi() == idProd) {
                                p.addRecord(new RecordProduksi(rsRec.getInt("id_record_produksi"), rsRec.getDouble("jumlah"), rsRec.getString("satuan")));
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listProduksi;
    }
}
