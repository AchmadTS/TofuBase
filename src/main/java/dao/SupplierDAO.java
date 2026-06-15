package dao;

import utils.DatabaseConfig;
import utils.FormatUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import models.Supplier;

public class SupplierDAO {

    public Map<String, String> getTopCardsData() {
        Map<String, String> data = new HashMap<>();
        data.put("total_supplier", "0");
        data.put("bahan_disuplai", "0");
        data.put("total_nilai", "Rp 0");

        String query = "SELECT "
                + "(SELECT COUNT(*) FROM supplier) as total_sup, "
                + "(SELECT COUNT(DISTINCT nama) FROM bahan_baku) as total_bahan, "
                + "(SELECT SUM(stok * harga_beli) FROM bahan_baku) as total_val";

        try (Connection conn = DatabaseConfig.getKoneksi(); PreparedStatement ps = conn.prepareStatement(query); ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                data.put("total_supplier", FormatUtil.formatAngka(rs.getDouble("total_sup")));
                data.put("bahan_disuplai", FormatUtil.formatAngka(rs.getDouble("total_bahan")));
                double nilai = rs.getDouble("total_val");
                data.put("total_nilai", "Rp " + FormatUtil.formatAngka(nilai / 1_000_000.0) + " Jt");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public int getTableTotalRows(String keyword) {
        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        String query = "SELECT COUNT(*) FROM supplier" + (hasKeyword ? " WHERE nama LIKE ? OR CAST(id_supplier AS CHAR) LIKE ?" : "");

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

    public List<Supplier> getTablePageData(int limit, int offset, String keyword) {
        List<Supplier> list = new ArrayList<>();
        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        String query = "SELECT s.id_supplier, s.nama, s.no_telp, "
                + "GROUP_CONCAT(DISTINCT b.nama SEPARATOR ', ') AS bahan_list "
                + "FROM supplier s "
                + "LEFT JOIN bahan_baku b ON s.id_supplier = b.id_supplier "
                + (hasKeyword ? "WHERE s.nama LIKE ? OR CAST(s.id_supplier AS CHAR) LIKE ? " : "")
                + "GROUP BY s.id_supplier "
                + "ORDER BY s.nama ASC LIMIT ? OFFSET ?";

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
                while (rs.next()) {
                    Supplier s = new Supplier(rs.getInt("id_supplier"), rs.getString("nama"), "", rs.getString("no_telp"), "");
                    list.add(s);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public Supplier checkSupplierByName(String nama) {
        String query = "SELECT * FROM supplier WHERE nama = ? LIMIT 1";
        try (Connection conn = DatabaseConfig.getKoneksi(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, nama);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Supplier(
                            rs.getInt("id_supplier"),
                            rs.getString("nama"),
                            rs.getString("alamat"),
                            rs.getString("no_telp"),
                            rs.getString("email")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insertSupplier(Supplier s) {
        String query = "INSERT INTO supplier (nama, alamat, no_telp, email) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getKoneksi(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, s.getNama());
            ps.setString(2, s.getAlamat());
            ps.setString(3, s.getNoTelp());
            ps.setString(4, s.getEmail());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateSupplier(Supplier s) {
        String query = "UPDATE supplier SET nama = ?, alamat = ?, no_telp = ?, email = ? WHERE id_supplier = ?";
        try (Connection conn = DatabaseConfig.getKoneksi(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, s.getNama());
            ps.setString(2, s.getAlamat());
            ps.setString(3, s.getNoTelp());
            ps.setString(4, s.getEmail());
            ps.setInt(5, s.getIdSupplier());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteSupplier(int id) {
        String query = "DELETE FROM supplier WHERE id_supplier = ?";
        try (Connection conn = DatabaseConfig.getKoneksi(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<String> getBahanBySupplierId(int supplierId) {
        List<String> list = new ArrayList<>();
        String query = "SELECT nama FROM bahan_baku WHERE id_supplier = ?";
        try (Connection conn = DatabaseConfig.getKoneksi(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, supplierId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(rs.getString("nama"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean isSupplierInUse(int idSupplier) {
        String query = "SELECT COUNT(*) FROM bahan_baku WHERE id_supplier = ?";
        try (Connection conn = DatabaseConfig.getKoneksi(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, idSupplier);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
