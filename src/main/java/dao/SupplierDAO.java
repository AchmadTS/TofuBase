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
        try (Connection conn = DatabaseConfig.getKoneksi();
                PreparedStatement ps = conn.prepareStatement(query);
                ResultSet rs = ps.executeQuery()) {
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
        boolean isNumeric = hasKeyword && keyword.trim().matches("\\d+");

        StringBuilder query = new StringBuilder("SELECT COUNT(*) FROM supplier");
        if (hasKeyword) {
            if (isNumeric) {
                query.append(" WHERE nama ILIKE ? OR id_supplier = ?");
            } else {
                query.append(" WHERE nama ILIKE ?");
            }
        }

        try (Connection conn = DatabaseConfig.getKoneksi();
                PreparedStatement ps = conn.prepareStatement(query.toString())) {
            if (hasKeyword) {
                String search = "%" + keyword.trim() + "%";
                ps.setString(1, search);
                if (isNumeric) {
                    ps.setInt(2, Integer.parseInt(keyword.trim()));
                }
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
        boolean isNumeric = hasKeyword && keyword.trim().matches("\\d+");

        StringBuilder query = new StringBuilder();
        query.append("SELECT s.id_supplier, s.nama, s.no_telp FROM supplier s ");
        query.append("JOIN (SELECT id_supplier FROM supplier ");
        if (hasKeyword) {
            if (isNumeric) {
                query.append("WHERE nama ILIKE ? OR id_supplier = ? ");
            } else {
                query.append("WHERE nama ILIKE ? ");
            }
        }
        query.append("ORDER BY nama ASC, id_supplier ASC LIMIT ? OFFSET ?) sub ");
        query.append("ON s.id_supplier = sub.id_supplier ");
        query.append("ORDER BY s.nama ASC, s.id_supplier ASC");

        try (Connection conn = DatabaseConfig.getKoneksi();
                PreparedStatement ps = conn.prepareStatement(query.toString())) {
            int idx = 1;
            if (hasKeyword) {
                String search = "%" + keyword.trim() + "%";
                ps.setString(idx++, search);
                if (isNumeric) {
                    ps.setInt(idx++, Integer.parseInt(keyword.trim()));
                }
            }
            ps.setInt(idx++, limit);
            ps.setInt(idx, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Supplier s = new Supplier(rs.getInt("id_supplier"), rs.getString("nama"), "",
                            rs.getString("no_telp"), "");
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
                    return new Supplier(rs.getInt("id_supplier"), rs.getString("nama"), rs.getString("alamat"),
                            rs.getString("no_telp"), rs.getString("email"));
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

    public Map<String, String> getRiwayatTopCardsData(String idSupplier) {
        Map<String, String> result = new HashMap<>();
        String query = "SELECT COUNT(*) as total_transaksi, COUNT(DISTINCT nama) as bahan_disuplai, SUM(stok * harga_beli) as total_nilai "
                + "FROM bahan_baku WHERE id_supplier = ?";
        try (Connection conn = DatabaseConfig.getKoneksi(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, Integer.parseInt(idSupplier));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    java.text.NumberFormat nf = java.text.NumberFormat.getInstance(java.util.Locale.of("id", "ID"));
                    result.put("total_transaksi", nf.format(rs.getInt("total_transaksi")));
                    result.put("bahan_disuplai", nf.format(rs.getInt("bahan_disuplai")));

                    double nilai = rs.getDouble("total_nilai");
                    result.put("total_nilai", "Rp " + nf.format(nilai));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public int getRiwayatTotalRows(String idSupplier, String keyword) {
        int total = 0;
        String query = "SELECT COUNT(*) FROM bahan_baku WHERE id_supplier = ? AND nama ILIKE ?";
        try (Connection conn = DatabaseConfig.getKoneksi(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, Integer.parseInt(idSupplier));
            ps.setString(2, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    total = rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return total;
    }

    public List<String[]> getRiwayatPageData(int limit, int offset, String idSupplier, String keyword) {
        List<String[]> list = new ArrayList<>();
        String query = "SELECT created_at, nama, stok, satuan, (stok * harga_beli) as total_nilai "
                + "FROM bahan_baku WHERE id_supplier = ? AND nama ILIKE ? "
                + "ORDER BY created_at DESC LIMIT ? OFFSET ?";
        try (Connection conn = DatabaseConfig.getKoneksi(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, Integer.parseInt(idSupplier));
            ps.setString(2, "%" + keyword + "%");
            ps.setInt(3, limit);
            ps.setInt(4, offset);
            try (ResultSet rs = ps.executeQuery()) {
                java.text.NumberFormat nf = java.text.NumberFormat.getInstance(java.util.Locale.of("id", "ID"));
                while (rs.next()) {
                    String[] row = new String[5];
                    row[0] = rs.getDate("created_at") != null ? rs.getDate("created_at").toString() : "-";
                    row[1] = rs.getString("nama");
                    row[2] = nf.format(rs.getDouble("stok"));
                    row[3] = rs.getString("satuan");
                    row[4] = "Rp " + nf.format(rs.getDouble("total_nilai"));
                    list.add(row);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public Supplier getSupplierById(int id) {
        String query = "SELECT * FROM supplier WHERE id_supplier = ?";
        try (Connection conn = DatabaseConfig.getKoneksi(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Supplier(rs.getInt("id_supplier"), rs.getString("nama"), rs.getString("alamat"),
                            rs.getString("no_telp"), rs.getString("email"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<Integer, List<String>> getBahanMapBySupplierIds(List<Integer> supplierIds) {
        Map<Integer, List<String>> map = new HashMap<>();
        if (supplierIds == null || supplierIds.isEmpty()) {
            return map;
        }

        StringBuilder sb = new StringBuilder("SELECT id_supplier, nama FROM bahan_baku WHERE id_supplier IN (");
        for (int i = 0; i < supplierIds.size(); i++) {
            sb.append(i == 0 ? "?" : ", ?");
        }
        sb.append(")");
        try (Connection conn = DatabaseConfig.getKoneksi();
                PreparedStatement ps = conn.prepareStatement(sb.toString())) {
            for (int i = 0; i < supplierIds.size(); i++) {
                ps.setInt(i + 1, supplierIds.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int idSup = rs.getInt("id_supplier");
                    String namaBahan = rs.getString("nama");
                    map.computeIfAbsent(idSup, k -> new ArrayList<>()).add(namaBahan);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }
}
