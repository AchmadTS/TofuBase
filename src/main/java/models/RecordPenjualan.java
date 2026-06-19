package models;

public class RecordPenjualan {

    private int idRecordPenjualan;
    private int idPenjualan;
    private int idProduk;
    private double jumlah;
    private double harga;
    private double subtotal;
    private String namaProduk;
    private String satuan;

    public RecordPenjualan() {
    }

    public RecordPenjualan(int idRecordPenjualan, int idProduk, double jumlah, double harga, double subtotal, String namaProduk, String satuan) {
        this.idRecordPenjualan = idRecordPenjualan;
        this.idProduk = idProduk;
        this.jumlah = jumlah;
        this.harga = harga;
        this.subtotal = subtotal;
        this.namaProduk = namaProduk;
        this.satuan = satuan;
    }

    public int getIdRecordPenjualan() {
        return idRecordPenjualan;
    }

    public void setIdRecordPenjualan(int idRecordPenjualan) {
        this.idRecordPenjualan = idRecordPenjualan;
    }

    public int getIdPenjualan() {
        return idPenjualan;
    }

    public void setIdPenjualan(int idPenjualan) {
        this.idPenjualan = idPenjualan;
    }

    public int getIdProduk() {
        return idProduk;
    }

    public void setIdProduk(int idProduk) {
        this.idProduk = idProduk;
    }

    public double getJumlah() {
        return jumlah;
    }

    public void setJumlah(double jumlah) {
        this.jumlah = jumlah;
    }

    public double getHarga() {
        return harga;
    }

    public void setHarga(double harga) {
        this.harga = harga;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public String getNamaProduk() {
        return namaProduk;
    }

    public void setNamaProduk(String namaProduk) {
        this.namaProduk = namaProduk;
    }

    public String getSatuan() {
        return satuan;
    }

    public void setSatuan(String satuan) {
        this.satuan = satuan;
    }
}
