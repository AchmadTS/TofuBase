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

        String query = "SELECT COUNT(*) AS total_produksi, COALESCE(SUM(hasil_tahu), 0) AS hasil_total, COUNT(DISTINCT id_user) AS operator_aktif FROM produksi";
        try (Connection conn = DatabaseConfig.getKoneksi();
                PreparedStatement ps = conn.prepareStatement(query);
                ResultSet rs = ps.executeQuery()) {
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
                + (hasKeyword ? " WHERE pr.nama ILIKE ? OR p.batch ILIKE ? OR p.status ILIKE ?" : "");
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
        String query = "SELECT p.id_produksi, p.batch, p.tanggal, pr.nama AS produk, p.hasil_tahu, p.status FROM produksi p "
                + "JOIN produk pr ON p.id_produk = pr.id_produk"
                + (hasKeyword ? " WHERE pr.nama ILIKE ? OR p.batch ILIKE ? OR p.status ILIKE ?" : "")
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
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd MMM yyyy",
                        java.util.Locale.forLanguageTag("id-ID"));
                while (rs.next()) {
                    data.add(new String[] {
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
        String query = "SELECT p.id_produksi, p.tanggal, p.keterangan, p.hasil_tahu, p.status, pr.nama AS produk, p.batch, p.id_user "
                + "FROM produksi p JOIN produk pr ON p.id_produk = pr.id_produk WHERE p.id_produksi = ?";
        try (Connection conn = DatabaseConfig.getKoneksi(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Produksi(rs.getInt("id_produksi"), rs.getDate("tanggal"), rs.getString("keterangan"),
                            rs.getDouble("hasil_tahu"), rs.getString("produk"), rs.getString("status"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<models.Produk> getProdukList() {
        List<models.Produk> list = new ArrayList<>();
        String query = "SELECT id_produk, nama, satuan, harga_jual, stok FROM produk ORDER BY nama ASC";
        try (Connection conn = utils.DatabaseConfig.getKoneksi();
                PreparedStatement ps = conn.prepareStatement(query);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                models.Produk p = new models.Produk();
                p.setIdProduk(rs.getInt("id_produk"));
                p.setNama(rs.getString("nama"));
                p.setSatuan(rs.getString("satuan"));
                p.setHargaJual(rs.getDouble("harga_jual"));
                p.setStok(rs.getDouble("stok"));
                list.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insertProduksi(int idProduk, String batch, java.util.Date tanggal, double hasilTahu, String status,
            String keterangan, int idUser) {
        String queryProduksi = "INSERT INTO produksi (id_produk, batch, tanggal, hasil_tahu, status, keterangan, id_user) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String queryUpdateStok = "UPDATE produk SET stok = stok + ? WHERE id_produk = ?";
        Connection conn = null;
        try {
            conn = DatabaseConfig.getKoneksi();
            conn.setAutoCommit(false);
            try (PreparedStatement psP = conn.prepareStatement(queryProduksi)) {
                psP.setInt(1, idProduk);
                psP.setString(2, batch);
                psP.setDate(3, new java.sql.Date(tanggal.getTime()));
                psP.setDouble(4, hasilTahu);
                psP.setString(5, status);
                psP.setString(6, keterangan);
                psP.setInt(7, idUser);
                psP.executeUpdate();
            }
            if (status.equalsIgnoreCase("Selesai")) {
                try (PreparedStatement psS = conn.prepareStatement(queryUpdateStok)) {
                    psS.setDouble(1, hasilTahu);
                    psS.setInt(2, idProduk);
                    psS.executeUpdate();
                }
            }
            conn.commit();
            return true;
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
