package models;

import java.util.Date;

public class Pemasukan {
    private int idPemasukan;
    private int idPenjualan;
    private Date tanggal;
    private String sumber;
    private double jumlah;
    private String keterangan;

    public Pemasukan() {
    }

    public Pemasukan(int idPemasukan, int idPenjualan, Date tanggal, String sumber, double jumlah, String keterangan) {
        this.idPemasukan = idPemasukan;
        this.idPenjualan = idPenjualan;
        this.tanggal = tanggal;
        this.sumber = sumber;
        this.jumlah = jumlah;
        this.keterangan = keterangan;
    }

    public int getIdPemasukan() {
        return idPemasukan;
    }

    public void setIdPemasukan(int idPemasukan) {
        this.idPemasukan = idPemasukan;
    }

    public int getIdPenjualan() {
        return idPenjualan;
    }

    public void setIdPenjualan(int idPenjualan) {
        this.idPenjualan = idPenjualan;
    }

    public Date getTanggal() {
        return tanggal;
    }

    public void setTanggal(Date tanggal) {
        this.tanggal = tanggal;
    }

    public String getSumber() {
        return sumber;
    }

    public void setSumber(String sumber) {
        this.sumber = sumber;
    }

    public double getJumlah() {
        return jumlah;
    }

    public void setJumlah(double jumlah) {
        this.jumlah = jumlah;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }
}
