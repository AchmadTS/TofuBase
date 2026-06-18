package models;

public class Produk {

    private int idProduk;
    private String nama;
    private String satuan;
    private double hargaJual;
    private String jenis;
    private double stok;

    public Produk() {
    }

    public Produk(int idProduk, String nama, String satuan, double hargaJual, String jenis, double stok) {
        this.idProduk = idProduk;
        this.nama = nama;
        this.satuan = satuan;
        this.hargaJual = hargaJual;
        this.jenis = jenis;
        this.stok = stok;
    }

    public int getIdProduk() {
        return idProduk;
    }

    public void setIdProduk(int idProduk) {
        this.idProduk = idProduk;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getSatuan() {
        return satuan;
    }

    public void setSatuan(String satuan) {
        this.satuan = satuan;
    }

    public double getHargaJual() {
        return hargaJual;
    }

    public void setHargaJual(double hargaJual) {
        this.hargaJual = hargaJual;
    }

    public String getJenis() {
        return jenis;
    }

    public void setJenis(String jenis) {
        this.jenis = jenis;
    }

    public double getStok() {
        return stok;
    }

    public void setStok(double stok) {
        this.stok = stok;
    }
}
