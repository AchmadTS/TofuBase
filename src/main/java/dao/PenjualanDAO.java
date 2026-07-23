package dao;

import models.Penjualan;
import models.RecordPenjualan;
import utils.DatabaseConfig;
import utils.FormatUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PenjualanDAO {

    public Map<String, String> getTopCardsData() {
        Map<String, String> data = new HashMap<>();
        data.put("total_penjualan", "0");
        data.put("produk_terjual", "0");
        data.put("pelanggan", "0");
        data.put("omzet", "Rp 0");

        String querySummary = "SELECT COUNT(*) AS total_penjualan, SUM(total) AS total_omzet, COUNT(DISTINCT id_pelanggan) AS total_pelanggan "
                + "FROM penjualan WHERE tanggal >= date_trunc('month', CURRENT_DATE)::date AND tanggal <= (date_trunc('month', CURRENT_DATE) + interval '1 month - 1 day')::date";
        
        String queryProduk = "SELECT SUM(rp.jumlah) AS total_produk FROM penjualan p "
                + "JOIN record_penjualan rp ON p.id_penjualan = rp.id_penjualan "
                + "WHERE p.tanggal >= date_trunc('month', CURRENT_DATE)::date AND p.tanggal <= (date_trunc('month', CURRENT_DATE) + interval '1 month - 1 day')::date";

        try (Connection conn = DatabaseConfig.getKoneksi(); PreparedStatement ps = conn.prepareStatement(querySummary); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                data.put("total_penjualan", FormatUtil.formatAngka(rs.getDouble("total_penjualan")));
                double omzet = rs.getObject("total_omzet") != null ? rs.getDouble("total_omzet") : 0.0;
                data.put("omzet", "Rp " + FormatUtil.formatAngka(omzet));
                data.put("pelanggan", FormatUtil.formatAngka(rs.getDouble("total_pelanggan")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try (Connection conn = DatabaseConfig.getKoneksi(); PreparedStatement ps = conn.prepareStatement(queryProduk); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                data.put("produk_terjual", FormatUtil.formatAngka(rs.getDouble("total_produk")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    public int getTableTotalRows(String keyword) {
        String query = "SELECT COUNT(*) AS total FROM penjualan p "
                + "JOIN pelanggan t ON p.id_pelanggan = t.id_pelanggan "
                + "WHERE CAST(p.id_penjualan AS TEXT) ILIKE ? OR t.nama ILIKE ? OR p.keterangan ILIKE ?";
        if (keyword == null || keyword.trim().isEmpty()) {
            query = "SELECT COUNT(*) AS total FROM penjualan";
        }
        try (Connection conn = DatabaseConfig.getKoneksi(); PreparedStatement ps = conn.prepareStatement(query)) {
            if (keyword != null && !keyword.trim().isEmpty()) {
                String param = keyword.trim() + "%";
                ps.setString(1, param);
                ps.setString(2, "%" + keyword.trim() + "%");
                ps.setString(3, "%" + keyword.trim() + "%");
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
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
        String baseSql = "SELECT p.id_penjualan, p.tanggal, t.nama AS pelanggan, p.total, "
                + "COALESCE(SUM(rp.jumlah), 0) AS produk_terjual, p.keterangan "
                + "FROM penjualan p "
                + "JOIN pelanggan t ON p.id_pelanggan = t.id_pelanggan "
                + "LEFT JOIN record_penjualan rp ON rp.id_penjualan = p.id_penjualan ";
                
        String where = hasKeyword ? "WHERE CAST(p.id_penjualan AS TEXT) ILIKE ? OR t.nama ILIKE ? OR p.keterangan ILIKE ? " : "";
        String groupOrder = "GROUP BY p.id_penjualan, p.tanggal, t.nama, p.total, p.keterangan ORDER BY p.tanggal DESC, p.id_penjualan DESC LIMIT ? OFFSET ?";

        try (Connection conn = DatabaseConfig.getKoneksi(); PreparedStatement ps = conn.prepareStatement(baseSql + where + groupOrder)) {
            int index = 1;
            if (hasKeyword) {
                ps.setString(index++, keyword.trim() + "%");
                ps.setString(index++, "%" + keyword.trim() + "%");
                ps.setString(index++, "%" + keyword.trim() + "%");
            }
            ps.setInt(index++, limit);
            ps.setInt(index, offset);
            try (ResultSet rs = ps.executeQuery()) {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd MMM yyyy");
                while (rs.next()) {
                    String id = String.valueOf(rs.getInt("id_penjualan"));
                    String tanggal = rs.getDate("tanggal") != null ? sdf.format(rs.getDate("tanggal")) : "-";
                    String pelanggan = rs.getString("pelanggan");
                    String total = "Rp " + FormatUtil.formatAngka(rs.getDouble("total"));
                    String produkTerjual = FormatUtil.formatAngka(rs.getDouble("produk_terjual"));
                    String keterangan = rs.getString("keterangan");
                    data.add(new String[]{id, tanggal, pelanggan, total, produkTerjual, keterangan, id});
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public Penjualan getPenjualanDetail(int idPenjualan) {
        String query = "SELECT p.id_penjualan, p.id_pelanggan, p.tanggal, p.total, p.keterangan, t.nama AS nama_pelanggan "
                + "FROM penjualan p JOIN pelanggan t ON p.id_pelanggan = t.id_pelanggan WHERE p.id_penjualan = ?";
        try (Connection conn = DatabaseConfig.getKoneksi(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, idPenjualan);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Penjualan(rs.getInt("id_penjualan"), rs.getInt("id_pelanggan"), rs.getDate("tanggal"), rs.getDouble("total"), rs.getString("keterangan"), rs.getString("nama_pelanggan"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<RecordPenjualan> getRecordPenjualanByPenjualanId(int idPenjualan) {
        List<RecordPenjualan> list = new ArrayList<>();
        String query = "SELECT rp.id_record_penjualan, rp.id_produk, rp.jumlah, rp.harga, rp.subtotal, pr.nama AS nama_produk, pr.satuan "
                + "FROM record_penjualan rp "
                + "JOIN produk pr ON rp.id_produk = pr.id_produk "
                + "WHERE rp.id_penjualan = ?";
        try (Connection conn = DatabaseConfig.getKoneksi(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, idPenjualan);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RecordPenjualan record = new RecordPenjualan(
                            rs.getInt("id_record_penjualan"),
                            rs.getInt("id_produk"),
                            rs.getDouble("jumlah"),
                            rs.getDouble("harga"),
                            rs.getDouble("subtotal"),
                            rs.getString("nama_produk"),
                            rs.getString("satuan")
                    );
                    record.setIdPenjualan(idPenjualan);
                    list.add(record);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insertPenjualan(Penjualan penjualan, List<RecordPenjualan> detailList) {
        String queryPenjualan = "INSERT INTO penjualan (id_pelanggan, tanggal, total, keterangan) VALUES (?, ?, ?, ?)";
        String queryRecord = "INSERT INTO record_penjualan (id_penjualan, id_produk, jumlah, harga, subtotal) VALUES (?, ?, ?, ?, ?)";
        String queryUpdateStok = "UPDATE produk SET stok = stok - ? WHERE id_produk = ?";
        Connection conn = null;
        try {
            conn = DatabaseConfig.getKoneksi();
            conn.setAutoCommit(false);
            
            int idPenjualan = 0;
            try (PreparedStatement psP = conn.prepareStatement(queryPenjualan, java.sql.Statement.RETURN_GENERATED_KEYS)) {
                psP.setInt(1, penjualan.getIdPelanggan());
                psP.setDate(2, new java.sql.Date(penjualan.getTanggal().getTime()));
                psP.setDouble(3, penjualan.getTotal());
                psP.setString(4, penjualan.getKeterangan());
                psP.executeUpdate();
                try (ResultSet rs = psP.getGeneratedKeys()) {
                    if (rs.next()) {
                        idPenjualan = rs.getInt(1);
                    }
                }
            }
            
            if (idPenjualan == 0) {
                conn.rollback();
                return false;
            }
            
            try (PreparedStatement psR = conn.prepareStatement(queryRecord);
                 PreparedStatement psS = conn.prepareStatement(queryUpdateStok)) {
                for (RecordPenjualan item : detailList) {
                    psR.setInt(1, idPenjualan);
                    psR.setInt(2, item.getIdProduk());
                    psR.setDouble(3, item.getJumlah());
                    psR.setDouble(4, item.getHarga());
                    psR.setDouble(5, item.getSubtotal());
                    psR.addBatch();
                    
                    psS.setDouble(1, item.getJumlah());
                    psS.setInt(2, item.getIdProduk());
                    psS.addBatch();
                }
                psR.executeBatch();
                psS.executeBatch();
            }
            
            conn.commit();
            return true;
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (Exception ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (Exception e) { e.printStackTrace(); }
            }
        }
    }
}
