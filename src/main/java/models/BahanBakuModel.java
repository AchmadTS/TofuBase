package models;

import java.util.Date;

public class BahanBakuModel {

    private int idBahan;
    private String nama;
    private int idSupplier;
    private String satuan;
    private double stok;
    private double minStok;
    private double hargaBeli;
    private Date createdAt;

    public BahanBakuModel() {
    }

    public BahanBakuModel(int idBahan, String nama, int idSupplier, String satuan,
            double stok, double minStok, double hargaBeli, Date createdAt) {
        this.idBahan = idBahan;
        this.nama = nama;
        this.idSupplier = idSupplier;
        this.satuan = satuan;
        this.stok = stok;
        this.minStok = minStok;
        this.hargaBeli = hargaBeli;
        this.createdAt = createdAt;
    }

    // =========================================================
    // 2. GETTERS & SETTERS
    // =========================================================
    public int getIdBahan() {
        return idBahan;
    }

    public void setIdBahan(int idBahan) {
        this.idBahan = idBahan;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public int getIdSupplier() {
        return idSupplier;
    }

    public void setIdSupplier(int idSupplier) {
        this.idSupplier = idSupplier;
    }

    public String getSatuan() {
        return satuan;
    }

    public void setSatuan(String satuan) {
        this.satuan = satuan;
    }

    public double getStok() {
        return stok;
    }

    public void setStok(double stok) {
        this.stok = stok;
    }

    public double getMinStok() {
        return minStok;
    }

    public void setMinStok(double minStok) {
        this.minStok = minStok;
    }

    public double getHargaBeli() {
        return hargaBeli;
    }

    public void setHargaBeli(double hargaBeli) {
        this.hargaBeli = hargaBeli;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
