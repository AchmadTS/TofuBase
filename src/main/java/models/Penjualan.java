package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Penjualan {

    private int idPenjualan;
    private int idPelanggan;
    private Date tanggal;
    private double total;
    private String keterangan;
    private String namaPelanggan;
    private List<RecordPenjualan> records;

    public Penjualan() {
        this.records = new ArrayList<>();
    }

    public Penjualan(int idPenjualan, int idPelanggan, Date tanggal, double total, String keterangan, String namaPelanggan) {
        this.idPenjualan = idPenjualan;
        this.idPelanggan = idPelanggan;
        this.tanggal = tanggal;
        this.total = total;
        this.keterangan = keterangan;
        this.namaPelanggan = namaPelanggan;
        this.records = new ArrayList<>();
    }

    public int getIdPenjualan() {
        return idPenjualan;
    }

    public void setIdPenjualan(int idPenjualan) {
        this.idPenjualan = idPenjualan;
    }

    public int getIdPelanggan() {
        return idPelanggan;
    }

    public void setIdPelanggan(int idPelanggan) {
        this.idPelanggan = idPelanggan;
    }

    public Date getTanggal() {
        return tanggal;
    }

    public void setTanggal(Date tanggal) {
        this.tanggal = tanggal;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getNamaPelanggan() {
        return namaPelanggan;
    }

    public void setNamaPelanggan(String namaPelanggan) {
        this.namaPelanggan = namaPelanggan;
    }

    public List<RecordPenjualan> getRecords() {
        return records;
    }

    public void setRecords(List<RecordPenjualan> records) {
        this.records = records;
    }

    public void addRecord(RecordPenjualan record) {
        this.records.add(record);
    }
}
