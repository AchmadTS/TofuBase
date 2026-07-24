package dao;

import utils.DatabaseConfig;
import utils.FormatUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import models.BahanBakuModel;

public class BahanBakuDAO {

    public Map<String, String> getTopCardsData() {
        Map<String, String> data = new HashMap<>();
        data.put("aset", "0");
        data.put("kedelai", "0");
        data.put("status_ked_txt", "Tidak ada data");
        data.put("status_ked_color", "GRAY");
        data.put("pemasok", "0");

        try (Connection conn = DatabaseConfig.getKoneksi()) {

            // Total Aset Stok
            String queryAset = "SELECT SUM(stok * harga_beli) AS total FROM bahan_baku";
            try (PreparedStatement ps = conn.prepareStatement(queryAset); ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getString("total") != null) {
                    data.put("aset", FormatUtil.formatAngka(rs.getDouble("total") / 1_000_000.0));
                }
            }

            // Stok Kedelai & Status
            String queryKed = "SELECT SUM(stok) AS total_stok, MAX(min_stok) AS batas_stok FROM bahan_baku WHERE nama ILIKE ?";
            try (PreparedStatement ps = conn.prepareStatement(queryKed)) {
                ps.setString(1, "%Kedelai%");
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getString("total_stok") != null) {
                        double stok = rs.getDouble("total_stok");
                        double min = rs.getDouble("batas_stok");

                        data.put("kedelai", FormatUtil.formatAngka(stok));
                        if (stok <= min / 2) {
                            data.put("status_ked_txt", "▼ Kritis");
                            data.put("status_ked_color", "RED");
                        } else if (stok <= min) {
                            data.put("status_ked_txt", "▼ Rendah");
                            data.put("status_ked_color", "WARNING");
                        } else {
                            data.put("status_ked_txt", "▲ Aman");
                            data.put("status_ked_color", "GREEN");
                        }
                    }
                }
            }

            // Jumlah Supplier Terdaftar
            String querySup = "SELECT COUNT(DISTINCT id_supplier) AS total FROM bahan_baku";
            try (PreparedStatement ps = conn.prepareStatement(querySup); ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    data.put("pemasok", FormatUtil.formatAngka(rs.getDouble("total")));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public int getTableTotalRows(String keyword) {
        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        String query = "SELECT COUNT(*) AS total FROM (SELECT 1 FROM bahan_baku "
                + (hasKeyword ? "WHERE nama ILIKE ? OR CAST(id_bahan AS TEXT) ILIKE ? " : "")
                + "GROUP BY LOWER(TRIM(nama)), LOWER(TRIM(COALESCE(satuan, '')))) AS sub";
        try (Connection conn = DatabaseConfig.getKoneksi(); PreparedStatement ps = conn.prepareStatement(query)) {
            if (hasKeyword) {
                String searchParam = "%" + keyword.trim() + "%";
                ps.setString(1, searchParam);
                ps.setString(2, searchParam);
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

        String whereClause = hasKeyword ? "WHERE nama ILIKE ? OR CAST(id_bahan AS TEXT) ILIKE ? " : "";
        String query = "SELECT MIN(b.id_bahan) AS id_bahan, "
                + "MAX(b.nama) AS nama, MAX(b.satuan) AS satuan, "
                + "SUM(b.stok) AS total_stok, "
                + "SUM(b.stok * b.harga_beli) / NULLIF(SUM(b.stok), 0) AS rata_harga, "
                + "MAX(b.min_stok) AS batas_stok "
                + "FROM (SELECT LOWER(TRIM(nama)) AS lname, LOWER(TRIM(COALESCE(satuan, ''))) AS lsatuan FROM bahan_baku "
                + whereClause
                + "GROUP BY LOWER(TRIM(nama)), LOWER(TRIM(COALESCE(satuan, ''))) ORDER BY LOWER(TRIM(nama)) ASC LIMIT ? OFFSET ?) AS filter_b "
                + "JOIN bahan_baku b ON LOWER(TRIM(b.nama)) = filter_b.lname AND LOWER(TRIM(COALESCE(b.satuan, ''))) = filter_b.lsatuan "
                + "GROUP BY filter_b.lname, filter_b.lsatuan ORDER BY filter_b.lname ASC";

        try (Connection conn = DatabaseConfig.getKoneksi(); PreparedStatement ps = conn.prepareStatement(query)) {
            int paramIndex = 1;
            if (hasKeyword) {
                String searchParam = "%" + keyword.trim() + "%";
                ps.setString(paramIndex++, searchParam);
                ps.setString(paramIndex++, searchParam);
            }
            ps.setInt(paramIndex++, limit);
            ps.setInt(paramIndex, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String id = "BHN-" + rs.getInt("id_bahan");
                    String nama = rs.getString("nama");
                    String satuan = rs.getString("satuan") != null ? rs.getString("satuan") : "-";

                    double stok = rs.getDouble("total_stok");
                    double hargaBeliAvg = rs.getDouble("rata_harga");
                    double minStok = rs.getDouble("batas_stok");

                    String harga = "Rp " + FormatUtil.formatAngka(hargaBeliAvg);
                    String stokStr = FormatUtil.formatAngka(stok);
                    String minStokStr = FormatUtil.formatAngka(minStok);
                    String status = "Aman";
                    if (stok <= minStok / 2) {
                        status = "Kritis";
                    } else if (stok <= minStok) {
                        status = "Rendah";
                    }

                    data.add(new String[] { id, nama, stokStr, satuan, harga, minStokStr, status });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public Map<Integer, String> getSupplierList() {
        Map<Integer, String> suppliers = new HashMap<>();
        String query = "SELECT id_supplier, nama FROM supplier ORDER BY nama ASC";
        try (Connection conn = DatabaseConfig.getKoneksi();
                PreparedStatement ps = conn.prepareStatement(query);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                suppliers.put(rs.getInt("id_supplier"), rs.getString("nama"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return suppliers;
    }

    public List<String> getSatuanList() {
        List<String> satuanList = new ArrayList<>(Arrays.asList("kg", "liter", "pcs", "gram", "ml", "bungkus"));
        String query = "SELECT DISTINCT satuan FROM bahan_baku WHERE satuan IS NOT NULL AND satuan != ''";
        try (Connection conn = DatabaseConfig.getKoneksi();
                PreparedStatement ps = conn.prepareStatement(query);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String s = rs.getString("satuan").toLowerCase();
                if (!satuanList.contains(s)) {
                    satuanList.add(s);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Collections.sort(satuanList);
        return satuanList;
    }

    public boolean insertBahanBaru(BahanBakuModel bahan) {
        String insertQuery = "INSERT INTO bahan_baku (nama, id_supplier, satuan, stok, min_stok, harga_beli) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getKoneksi(); PreparedStatement ps = conn.prepareStatement(insertQuery)) {
            ps.setString(1, bahan.getNama());
            ps.setInt(2, bahan.getIdSupplier());
            ps.setString(3, bahan.getSatuan());
            ps.setDouble(4, bahan.getStok());
            ps.setDouble(5, bahan.getMinStok());
            ps.setDouble(6, bahan.getHargaBeli());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public BahanBakuModel cekDetailBahan(String namaBahan) {
        String query = "SELECT satuan, min_stok FROM bahan_baku WHERE LOWER(nama) = LOWER(?) LIMIT 1";
        try (Connection conn = DatabaseConfig.getKoneksi(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, namaBahan);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BahanBakuModel model = new BahanBakuModel();
                    model.setSatuan(rs.getString("satuan"));
                    model.setMinStok(rs.getDouble("min_stok"));
                    return model;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getRiwayatTotalRows(String namaBahan, String satuan, String keyword) {
        int total = 0;
        String query = "SELECT COUNT(*) FROM bahan_baku WHERE LOWER(TRIM(nama)) = LOWER(TRIM(?)) "
                + "AND LOWER(TRIM(COALESCE(satuan, ''))) = LOWER(TRIM(?)) "
                + "AND CAST(id_supplier AS TEXT) ILIKE ?";
        try (Connection conn = DatabaseConfig.getKoneksi(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, namaBahan);
            ps.setString(2, satuan);
            ps.setString(3, "%" + keyword + "%");
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

    public List<String[]> getRiwayatPageData(int limit, int offset, String namaBahan, String satuan, String keyword) {
        List<String[]> list = new ArrayList<>();
        String query = "SELECT id_bahan, created_at, nama, id_supplier, stok, satuan, harga_beli "
                + "FROM bahan_baku WHERE LOWER(TRIM(nama)) = LOWER(TRIM(?)) "
                + "AND LOWER(TRIM(COALESCE(satuan, ''))) = LOWER(TRIM(?)) "
                + "AND CAST(id_supplier AS TEXT) ILIKE ? "
                + "ORDER BY created_at DESC LIMIT ? OFFSET ?";
        try (Connection conn = DatabaseConfig.getKoneksi(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, namaBahan);
            ps.setString(2, satuan);
            ps.setString(3, "%" + keyword + "%");
            ps.setInt(4, limit);
            ps.setInt(5, offset);
            try (ResultSet rs = ps.executeQuery()) {
                java.text.NumberFormat nf = java.text.NumberFormat.getInstance(java.util.Locale.of("id", "ID"));
                while (rs.next()) {
                    String[] row = new String[8];
                    row[0] = rs.getDate("created_at") != null ? rs.getDate("created_at").toString() : "-";
                    row[1] = rs.getString("nama");
                    row[2] = rs.getString("id_supplier");
                    row[3] = nf.format(rs.getDouble("stok"));
                    row[4] = rs.getString("satuan");

                    double totalNilai = rs.getDouble("stok") * rs.getDouble("harga_beli");
                    row[5] = "Rp " + nf.format(totalNilai);
                    row[6] = "";
                    row[7] = String.valueOf(rs.getInt("id_bahan"));
                    list.add(row);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public Map<String, String> getRiwayatTopCardsData(String namaBahan, String satuan) {
        Map<String, String> result = new HashMap<>();
        String query = "SELECT COUNT(*) as total_transaksi, SUM(stok * harga_beli) as nilai_pembelian, SUM(stok) as stok_masuk "
                + "FROM bahan_baku WHERE LOWER(TRIM(nama)) = LOWER(TRIM(?)) "
                + "AND LOWER(TRIM(COALESCE(satuan, ''))) = LOWER(TRIM(?))";
        try (Connection conn = DatabaseConfig.getKoneksi(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, namaBahan);
            ps.setString(2, satuan);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    java.text.NumberFormat nf = java.text.NumberFormat.getInstance(java.util.Locale.of("id", "ID"));
                    result.put("total_transaksi", nf.format(rs.getInt("total_transaksi")));
                    double nilai = rs.getDouble("nilai_pembelian");
                    if (nilai >= 1_000_000) {
                        result.put("nilai_pembelian",
                                "Rp " + String.format("%.1f jt", nilai / 1_000_000.0).replace(".", ","));
                    } else {
                        result.put("nilai_pembelian", "Rp " + nf.format(nilai));
                    }
                    result.put("stok_terpakai", nf.format(rs.getDouble("stok_masuk")) + " satuan");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public BahanBakuModel getTransaksiById(String idBahan) {
        String query = "SELECT id_bahan, nama, id_supplier, satuan, stok, min_stok, harga_beli FROM bahan_baku WHERE id_bahan = ?";
        try (Connection conn = DatabaseConfig.getKoneksi(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, Integer.parseInt(idBahan));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BahanBakuModel model = new BahanBakuModel();
                    model.setIdBahan(rs.getInt("id_bahan"));
                    model.setNama(rs.getString("nama"));
                    model.setIdSupplier(rs.getInt("id_supplier"));
                    model.setSatuan(rs.getString("satuan"));
                    model.setStok(rs.getDouble("stok"));
                    model.setMinStok(rs.getDouble("min_stok"));
                    model.setHargaBeli(rs.getDouble("harga_beli"));
                    return model;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateTransaksiBahan(BahanBakuModel bahan) {
        String query = "UPDATE bahan_baku SET nama = ?, id_supplier = ?, satuan = ?, stok = ?, min_stok = ?, harga_beli = ? WHERE id_bahan = ?";
        try (Connection conn = DatabaseConfig.getKoneksi(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, bahan.getNama());
            ps.setInt(2, bahan.getIdSupplier());
            ps.setString(3, bahan.getSatuan());
            ps.setDouble(4, bahan.getStok());
            ps.setDouble(5, bahan.getMinStok());
            ps.setDouble(6, bahan.getHargaBeli());
            ps.setInt(7, bahan.getIdBahan());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteRiwayatById(String idBahan) {
        String query = "DELETE FROM bahan_baku WHERE id_bahan = ?";
        try (Connection conn = DatabaseConfig.getKoneksi(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, Integer.parseInt(idBahan));
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
