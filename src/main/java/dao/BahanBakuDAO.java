package dao;

import utils.DatabaseConfig;
import utils.FormatUtil;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BahanBakuDAO {

    public Map<String, String> getTopCardsData() {
        Map<String, String> data = new HashMap<>();
        try {
            Connection conn = DatabaseConfig.getKoneksi();
            Statement stmt = conn.createStatement();

            // Total Aset Stok
            ResultSet rsAset = stmt.executeQuery("SELECT SUM(stok * harga_beli) AS total FROM bahan_baku");
            if (rsAset.next() && rsAset.getString("total") != null) {
                data.put("aset", FormatUtil.formatAngka(rsAset.getDouble("total") / 1000000.0));
            } else {
                data.put("aset", "0");
            }

            // Stok Kedelai & Status
            ResultSet rsKed = stmt.executeQuery("SELECT SUM(stok) AS total_stok, MAX(min_stok) AS batas_stok FROM bahan_baku WHERE nama LIKE '%Kedelai%'");
            if (rsKed.next() && rsKed.getString("total_stok") != null) {
                double stok = rsKed.getDouble("total_stok");
                double min = rsKed.getDouble("batas_stok");

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
            } else {
                data.put("kedelai", "0");
                data.put("status_ked_txt", "Tidak ada data");
                data.put("status_ked_color", "GRAY");
            }

            // Jumlah Supplier Terdaftar
            ResultSet rsSup = stmt.executeQuery("SELECT COUNT(DISTINCT id_supplier) AS total FROM bahan_baku");
            if (rsSup.next()) {
                data.put("pemasok", FormatUtil.formatAngka(rsSup.getDouble("total")));
            } else {
                data.put("pemasok", "0");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public int getTableTotalRows(String keyword) {
        String query = keyword.isEmpty()
                ? "SELECT COUNT(*) AS total FROM (SELECT 1 FROM bahan_baku GROUP BY nama, satuan) AS sub"
                : "SELECT COUNT(*) AS total FROM (SELECT 1 FROM bahan_baku WHERE nama LIKE '" + keyword + "%' OR id_bahan LIKE '" + keyword + "%' GROUP BY nama, satuan) AS sub";

        try {
            Connection conn = DatabaseConfig.getKoneksi();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
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
        String whereClause = keyword.isEmpty() ? "" : "WHERE nama LIKE '" + keyword + "%' OR id_bahan LIKE '" + keyword + "%' ";

        String query = "SELECT MIN(b.id_bahan) AS id_bahan, b.nama, b.satuan, "
                + "SUM(b.stok) AS total_stok, "
                + "AVG(b.harga_beli) AS rata_harga, "
                + "MAX(b.min_stok) AS batas_stok "
                + "FROM (SELECT nama, satuan FROM bahan_baku " + whereClause
                + "GROUP BY nama, satuan ORDER BY nama ASC LIMIT " + limit + " OFFSET " + offset + ") AS filter_b "
                + "JOIN bahan_baku b ON b.nama = filter_b.nama AND b.satuan = filter_b.satuan "
                + "GROUP BY b.nama, b.satuan ORDER BY b.nama ASC";

        try {
            Connection conn = DatabaseConfig.getKoneksi();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                String id = "BHN-" + rs.getInt("id_bahan");
                String nama = rs.getString("nama");
                String satuan = rs.getString("satuan");

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

                data.add(new String[]{id, nama, stokStr, satuan, harga, minStokStr, status});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public Map<Integer, String> getSupplierList() {
        Map<Integer, String> suppliers = new HashMap<>();
        String query = "SELECT id_supplier, nama FROM supplier ORDER BY nama ASC";
        try {
            Connection conn = DatabaseConfig.getKoneksi();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                suppliers.put(rs.getInt("id_supplier"), rs.getString("nama"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return suppliers;
    }

    public List<String> getSatuanList() {
        List<String> satuanList = new ArrayList<>(java.util.Arrays.asList("kg", "liter", "pcs", "gram", "ml", "bungkus"));
        String query = "SELECT DISTINCT satuan FROM bahan_baku WHERE satuan IS NOT NULL AND satuan != ''";
        try {
            Connection conn = DatabaseConfig.getKoneksi();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                String s = rs.getString("satuan").toLowerCase();
                if (!satuanList.contains(s)) {
                    satuanList.add(s);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        java.util.Collections.sort(satuanList);
        return satuanList;
    }

    public boolean simpanAtauUpdateBahan(String nama, int idSupplier, String satuan, double stok, double minStok, double hargaBeli) {
        String normalizedInput = nama.replaceAll("\\s+", "").toLowerCase();
        String checkQuery = "SELECT id_bahan, stok FROM bahan_baku WHERE LOWER(REPLACE(nama, ' ', '')) = '" + normalizedInput + "'";
        try {
            Connection conn = DatabaseConfig.getKoneksi();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(checkQuery);

            if (rs.next()) {
                int idBahan = rs.getInt("id_bahan");
                String updateQuery = "UPDATE bahan_baku SET "
                        + "stok = stok + " + stok + ", "
                        + "min_stok = " + minStok + ", "
                        + "harga_beli = " + hargaBeli + ", "
                        + "satuan = '" + satuan + "', "
                        + "id_supplier = " + idSupplier + " "
                        + "WHERE id_bahan = " + idBahan;
                stmt.executeUpdate(updateQuery);
            } else {
                String insertQuery = "INSERT INTO bahan_baku (nama, id_supplier, satuan, stok, min_stok, harga_beli) " + "VALUES ('" + nama + "', " + idSupplier + ", '" + satuan + "', " + stok + ", " + minStok + ", " + hargaBeli + ")";
                stmt.executeUpdate(insertQuery);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Map<String, Object> cekDetailBahan(String namaBahan) {
        Map<String, Object> result = new java.util.HashMap<>();
        String query = "SELECT satuan, min_stok FROM bahan_baku WHERE nama = ? LIMIT 1";
        try (java.sql.Connection conn = utils.DatabaseConfig.getKoneksi(); java.sql.PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, namaBahan);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    result.put("satuan", rs.getString("satuan"));
                    result.put("min_stok", rs.getDouble("min_stok"));
                    return result;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getRiwayatTotalRows(String namaBahan, String keyword) {
        int total = 0;
        String query = "SELECT COUNT(*) FROM bahan_baku WHERE LOWER(nama) = LOWER(?) " + "AND LOWER(id_supplier) LIKE LOWER(?)";
        try (java.sql.Connection conn = utils.DatabaseConfig.getKoneksi(); // Pastikan utils koneksi Anda benar
                 java.sql.PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, namaBahan);
            ps.setString(2, "%" + keyword + "%");
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    total = rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return total;
    }

    public List<String[]> getRiwayatPageData(int limit, int offset, String namaBahan, String keyword) {
        List<String[]> list = new java.util.ArrayList<>();
        String query = "SELECT id_bahan, created_at, nama, id_supplier, stok, satuan, harga_beli "
                + "FROM bahan_baku WHERE LOWER(nama) = LOWER(?) "
                + "AND LOWER(id_supplier) LIKE LOWER(?) "
                + "ORDER BY created_at DESC LIMIT ? OFFSET ?";
        try (java.sql.Connection conn = utils.DatabaseConfig.getKoneksi(); java.sql.PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, namaBahan);
            ps.setString(2, "%" + keyword + "%");
            ps.setInt(3, limit);
            ps.setInt(4, offset);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                java.text.NumberFormat nf = java.text.NumberFormat.getInstance(new java.util.Locale("id", "ID"));
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

    public Map<String, String> getRiwayatTopCardsData(String namaBahan) {
        Map<String, String> result = new java.util.HashMap<>();
        String query = "SELECT COUNT(*) as total_transaksi, SUM(stok * harga_beli) as nilai_pembelian, SUM(stok) as stok_masuk " + "FROM bahan_baku WHERE LOWER(nama) = LOWER(?)";
        try (java.sql.Connection conn = utils.DatabaseConfig.getKoneksi(); java.sql.PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, namaBahan);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    java.text.NumberFormat nf = java.text.NumberFormat.getInstance(new java.util.Locale("id", "ID"));
                    result.put("total_transaksi", nf.format(rs.getInt("total_transaksi")));
                    double nilai = rs.getDouble("nilai_pembelian");
                    if (nilai >= 1_000_000) {
                        result.put("nilai_pembelian", "Rp " + String.format("%.1f jt", nilai / 1_000_000).replace(".", ","));
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
}
