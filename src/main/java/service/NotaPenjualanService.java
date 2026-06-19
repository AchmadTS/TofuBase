package service;

import utils.DatabaseConfig;
import utils.FormatUtil;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class NotaPenjualanService {

    public boolean exportNota(int idPenjualan, String outputPath) {
        String queryPenjualan = "SELECT p.id_penjualan, p.tanggal, pl.nama as nama_pelanggan, p.total " +
                                "FROM penjualan p " +
                                "LEFT JOIN pelanggan pl ON p.id_pelanggan = pl.id_pelanggan " +
                                "WHERE p.id_penjualan = ?";
                                
        String queryDetail = "SELECT pr.nama as nama_produk, dp.jumlah, dp.harga_satuan, dp.subtotal " +
                                "FROM record_penjualan dp " +
                                "JOIN produk pr ON dp.id_produk = pr.id_produk " +
                                "WHERE dp.id_penjualan = ?";

        try (Connection conn = DatabaseConfig.getKoneksi();
             PrintWriter writer = new PrintWriter(new FileOutputStream(outputPath))) {
            
            String tanggal = "";
            String pelanggan = "Umum / Tunai";
            double totalHarga = 0;
            
            try (PreparedStatement ps = conn.prepareStatement(queryPenjualan)) {
                ps.setInt(1, idPenjualan);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        tanggal = rs.getString("tanggal");
                        if (rs.getString("nama_pelanggan") != null) {
                            pelanggan = rs.getString("nama_pelanggan");
                        }
                        totalHarga = rs.getDouble("total");
                    } else {
                        return false;
                    }
                }
            }

            writer.println("<!DOCTYPE html>");
            writer.println("<html lang='id'>");
            writer.println("<head>");
            writer.println("    <meta charset='UTF-8'>");
            writer.println("    <title>Nota Penjualan #" + idPenjualan + "</title>");
            writer.println("    <style>");
            writer.println("        @page { size: 105mm 148mm; margin: 10mm; }");
            writer.println("        * { box-sizing: border-box; font-family: 'Courier New', Courier, monospace; }");
            writer.println("        body { margin: 0; padding: 0; color: #000000; font-size: 9pt; }");
            writer.println("        .text-center { text-align: center; }");
            writer.println("        .text-right { text-align: right; }");
            writer.println("        .line-dashed { border-top: 1px dashed #000; margin: 10px 0; }");
            writer.println("        .header { margin-bottom: 15px; }");
            writer.println("        .shop-name { font-size: 14pt; font-weight: bold; text-transform: uppercase; }");
            writer.println("        .meta-table, .item-table { width: 100%; border-collapse: collapse; }");
            writer.println("        .meta-table td { padding: 2px 0; font-size: 8pt; }");
            writer.println("        .item-table th { text-align: left; padding: 5px 0; border-bottom: 1px dashed #000; font-size: 8pt; }");
            writer.println("        .item-table td { padding: 5px 0; font-size: 8pt; vertical-align: top; }");
            writer.println("        .total-section { font-size: 11pt; font-weight: bold; margin-top: 15px; text-align: right; }");
            writer.println("    </style>");
            writer.println("</head>");
            writer.println("<body>");

            writer.println("    <div class='header text-center'>");
            writer.println("        <span class='shop-name'>PABRIK TAHU TOFUBASE</span><br>");
            writer.println("        <span>Kualitas Segar & Higienis Setiap Hari</span>");
            writer.println("    </div>");
            writer.println("    <div class='line-dashed'></div>");

            writer.println("    <table class='meta-table'>");
            writer.println("        <tr><td><strong>No Nota :</strong> #" + idPenjualan + "</td><td class='text-right'><strong>Kasir:</strong> Admin</td></tr>");
            writer.println("        <tr><td><strong>Tanggal :</strong> " + tanggal + "</td><td class='text-right'><strong>Pelanggan:</strong> " + pelanggan + "</td></tr>");
            writer.println("    </table>");
            writer.println("    <div class='line-dashed'></div>");

            writer.println("    <table class='item-table'>");
            writer.println("        <thead>");
            writer.println("            <tr>");
            writer.println("                <th style='width: 45%;'>Produk</th>");
            writer.println("                <th style='width: 15%;' class='text-center'>Qty</th>");
            writer.println("                <th style='width: 20%;' class='text-right'>Harga</th>");
            writer.println("                <th style='width: 20%;' class='text-right'>Total</th>");
            writer.println("            </tr>");
            writer.println("        </thead>");
            writer.println("        <tbody>");

            try (PreparedStatement ps = conn.prepareStatement(queryDetail)) {
                ps.setInt(1, idPenjualan);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        writer.println("            <tr>");
                        writer.println("                <td>" + rs.getString("nama_produk") + "</td>");
                        writer.println("                <td class='text-center'>" + rs.getInt("jumlah") + "</td>");
                        writer.println("                <td class='text-right'>" + FormatUtil.formatAngka(rs.getDouble("harga_satuan")) + "</td>");
                        writer.println("                <td class='text-right'>" + FormatUtil.formatAngka(rs.getDouble("subtotal")) + "</td>");
                        writer.println("            </tr>");
                    }
                }
            }

            writer.println("        </tbody>");
            writer.println("    </table>");
            writer.println("    <div class='line-dashed'></div>");

            writer.println("    <div class='total-section'>");
            writer.println("        GRAND TOTAL: Rp " + FormatUtil.formatAngka(totalHarga));
            writer.println("    </div>");

            writer.println("    <div class='text-center' style='margin-top: 25px; font-size: 8pt;'>");
            writer.println("        -- Terima Kasih Atas Kunjungan Anda --<br>");
            writer.println("        Barang yang sudah dibeli tidak dapat ditukar/dikembalikan");
            writer.println("    </div>");

            writer.println("</body>");
            writer.println("</html>");

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}