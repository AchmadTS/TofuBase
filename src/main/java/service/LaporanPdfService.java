package service;

import dao.LaporanKeuanganDAO;
import utils.FormatUtil;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

/**
 * Service untuk menangani pembuatan berkas cetak Laporan Keuangan TofuBase.
 * Menghasilkan struktur dokumen profesional dengan standar cetak A4.
 */
public class LaporanPdfService {

    private final LaporanKeuanganDAO laporanDAO = new LaporanKeuanganDAO();

    /**
     * Membuat dokumen cetak ringkasan laporan keuangan dalam format HTML/Cetak mandiri.
     * @param outputPath Jalur penyimpanan file hasil ekspor (misal: "Laporan_Keuangan_TofuBase.html")
     * @return boolean Status keberhasilan ekspor
     */
    public boolean exportLaporanKeuangan(String outputPath) {
        // 1. Ambil data ringkasan dan data tabel dari database via DAO
        Map<String, String> topCards = laporanDAO.getTopCardsData();
        
        // Mengambil semua data tanpa limitasi halaman untuk kebutuhan cetak dokumen penuh
        int totalRows = laporanDAO.getTableTotalRows("");
        List<String[]> tableData = laporanDAO.getTablePageData(totalRows, 0, "");

        try (PrintWriter writer = new PrintWriter(new FileOutputStream(outputPath))) {
            // 2. Tulis Struktur HTML Khusus untuk Mesin Cetak PDF (mendukung Paged Media A4)
            writer.println("<!DOCTYPE html>");
            writer.println("<html lang='id'>");
            writer.println("<head>");
            writer.println("    <meta charset='UTF-8'>");
            writer.println("    <title>Laporan Keuangan TofuBase</title>");
            writer.println("    <style>");
            
            // Pengaturan Margin & Ukuran Kertas A4 Internasional
            writer.println("        @page {");
            writer.println("            size: A4;");
            writer.println("            margin: 20mm 15mm 20mm 15mm;");
            writer.println("        }");
            
            // Reset dasar dokumen
            writer.println("        * { box-sizing: border-box; font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif; }");
            writer.println("        body { margin: 0; padding: 0; color: #2d3748; background-color: #ffffff; font-size: 10pt; line-height: 1.4; }");
            
            // Desain Header / Kop Surat Perusahaan
            writer.println("        .header-container { border-bottom: 2px solid #2b6cb0; padding-bottom: 12px; margin-bottom: 25px; }");
            writer.println("        .company-name { font-size: 20pt; font-weight: bold; color: #1a365d; text-transform: uppercase; letter-spacing: 1px; }");
            writer.println("        .company-tagline { font-size: 9pt; color: #718096; margin-top: 2px; }");
            writer.println("        .document-title { font-size: 14pt; font-weight: bold; color: #2b6cb0; text-align: right; margin-top: -35px; text-transform: uppercase; }");
            
            // Meta Informasi Dokumen
            writer.println("        .meta-info { margin-bottom: 25px; font-size: 9pt; color: #4a5568; background-color: #f7fafc; padding: 10px; border-radius: 4px; }");
            
            // Layout Ringkasan Data (Top Cards Modifikasi untuk Tabel Cetak)
            writer.println("        .summary-table { width: 100%; border-collapse: collapse; margin-bottom: 30px; }");
            writer.println("        .summary-table td { width: 33.33%; padding: 12px; background-color: #ebf8ff; border: 1px solid #bee3f8; border-radius: 4px; text-align: center; }");
            writer.println("        .summary-label { font-size: 8pt; text-transform: uppercase; color: #2c5282; font-weight: bold; margin-bottom: 4px; }");
            writer.println("        .summary-value { font-size: 13pt; font-weight: bold; color: #2b6cb0; }");
            
            // Desain Tabel Utama Data Keuangan
            writer.println("        .data-table { width: 100%; border-collapse: collapse; margin-top: 10px; page-break-inside: auto; }");
            writer.println("        .data-table tr { page-break-inside: avoid; page-break-after: auto; }");
            writer.println("        .data-table th { background-color: #2b6cb0; color: #ffffff; font-weight: bold; text-align: center; padding: 8px 10px; font-size: 9pt; border: 1px solid #2b6cb0; text-transform: uppercase; }");
            writer.println("        .data-table td { padding: 8px 10px; border-bottom: 1px solid #e2e8f0; border-left: 1px solid #e2e8f0; border-right: 1px solid #e2e8f0; font-size: 9pt; }");
            writer.println("        .text-center { text-align: center; }");
            writer.println("        .text-right { text-align: right; }");
            
            // Baris bergantian (Zebra Striping)
            writer.println("        .data-table tr:nth-child(even) { background-color: #f7fafc; }");
            
            // Keterangan Halaman / Footer Cetak
            writer.println("        .footer { position: fixed; bottom: 0; left: 0; right: 0; text-align: center; font-size: 8pt; color: #a0aec0; border-top: 1px solid #e2e8f0; padding-top: 5px; }");
            
            writer.println("    </style>");
            writer.println("</head>");
            writer.println("<body>");
            
            // Menulis Kop Dokumen
            writer.println("    <div class='header-container'>");
            writer.println("        <div class='company-name'>TofuBase</div>");
            writer.println("        <div class='company-tagline'>Sistem Informasi Manajemen Produksi & Keuangan Pabrik Tahu</div>");
            writer.println("        <div class='document-title'>Laporan Keuangan</div>");
            writer.println("    </div>");
            
            // Menulis Informasi Tanggal Cetak
            writer.println("    <div class='meta-info'>");
            writer.println("        <strong>Tanggal Cetak:</strong> " + java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd MMMM yyyy")));
            writer.println("        <br><strong>Total Rekor Laporan:</strong> " + totalRows + " Periode Bulan");
            writer.println("    </div>");
            
            // Menulis Struktur Ringkasan (Top Cards)
            writer.println("    <table class='summary-table'>");
            writer.println("        <tr>");
            writer.println("            <td>");
            writer.println("                <div class='summary-label'>Total Buku Laporan</div>");
            writer.println("                <div class='summary-value'>" + topCards.get("total_laporan") + "</div>");
            writer.println("            </td>");
            writer.println("            <td>");
            writer.println("                <div class='summary-label'>Periode Terbaru</div>");
            writer.println("                <div class='summary-value' style='font-size: 10pt; padding-top: 4px;'>" + topCards.get("periode_terbaru") + "</div>");
            writer.println("            </td>");
            writer.println("            <td>");
            writer.println("                <div class='summary-label'>Saldo Terakhir Kas</div>");
            writer.println("                <div class='summary-value' style='color: #38a169;'>" + topCards.get("saldo_terakhir") + "</div>");
            writer.println("            </td>");
            writer.println("        </tr>");
            writer.println("    </table>");
            
            // Menulis Judul Tabel Utama
            writer.println("    <h2 style='font-size: 11pt; text-transform: uppercase; color: #1a365d; margin-bottom: 10px; letter-spacing: 0.5px;'>Rincian Buku Kas Per Periode</h2>");
            
            // Menulis Tabel Data Keuangan
            writer.println("    <table class='data-table'>");
            writer.println("        <thead>");
            writer.println("            <tr>");
            writer.println("                <th style='width: 8%;'>ID</th>");
            writer.println("                <th style='width: 22%;'>Awal Periode</th>");
            writer.println("                <th style='width: 22%;'>Akhir Periode</th>");
            writer.println("                <th style='width: 16%;'>Pemasukan</th>");
            writer.println("                <th style='width: 16%;'>Pengeluaran</th>");
            writer.println("                <th style='width: 16%;'>Saldo Akhir</th>");
            writer.println("            </tr>");
            writer.println("        </thead>");
            writer.println("        <tbody>");
            
            if (tableData.isEmpty()) {
                writer.println("            <tr><td colspan='6' class='text-center' style='color: #a0aec0; padding: 20px;'>Belum ada data keuangan yang dibukukan.</td></tr>");
            } else {
                for (String[] row : tableData) {
                    writer.println("            <tr>");
                    writer.println("                <td class='text-center'>" + row[0] + "</td>");
                    writer.println("                <td class='text-center'>" + row[1] + "</td>");
                    writer.println("                <td class='text-center'>" + row[2] + "</td>");
                    writer.println("                <td class='text-right' style='color: #2f855a;'>" + row[3] + "</td>");
                    writer.println("                <td class='text-right' style='color: #c53030;'>" + row[4] + "</td>");
                    writer.println("                <td class='text-right' style='font-weight: bold;'>" + row[5] + "</td>");
                    writer.println("            </tr>");
                }
            }
            
            writer.println("        </tbody>");
            writer.println("    </table>");
            
            // Menulis Footer Dokumen
            writer.println("    <div class='footer'>Dokumen ini dibuat otomatis oleh Sistem ERP TofuBase Berbasis Java &copy; " + java.time.Year.now().getValue() + "</div>");
            
            writer.println("</body>");
            writer.println("</html>");
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}