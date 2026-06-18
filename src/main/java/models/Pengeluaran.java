package models;

import java.util.Date;

public class Pengeluaran {
    private int idPengeluaran;
    private Date tanggal;
    private String kategori;
    private String deskripsi;
    private double jumlah;

    public Pengeluaran() {
    }

    public Pengeluaran(int idPengeluaran, Date tanggal, String kategori, String deskripsi, double jumlah) {
        this.idPengeluaran = idPengeluaran;
        this.tanggal = tanggal;
        this.kategori = kategori;
        this.deskripsi = deskripsi;
        this.jumlah = jumlah;
    }

    public int getIdPengeluaran() {
        return idPengeluaran;
    }

    public void setIdPengeluaran(int idPengeluaran) {
        this.idPengeluaran = idPengeluaran;
    }

    public Date getTanggal() {
        return tanggal;
    }

    public void setTanggal(Date tanggal) {
        this.tanggal = tanggal;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public double getJumlah() {
        return jumlah;
    }

    public void setJumlah(double jumlah) {
        this.jumlah = jumlah;
    }
}
