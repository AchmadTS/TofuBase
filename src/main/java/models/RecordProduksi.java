package models;

public class RecordProduksi {

    private int idRecordProduksi;
    private double jumlah;
    private String satuan;

    public RecordProduksi(int idRecordProduksi, double jumlah, String satuan) {
        this.idRecordProduksi = idRecordProduksi;
        this.jumlah = jumlah;
        this.satuan = satuan;
    }

    public void hitungPemakaianBahan() {
        
    }

    public int getIdRecordProduksi() {
        return idRecordProduksi;
    }

    public void setIdRecordProduksi(int idRecordProduksi) {
        this.idRecordProduksi = idRecordProduksi;
    }

    public double getJumlah() {
        return jumlah;
    }

    public void setJumlah(double jumlah) {
        this.jumlah = jumlah;
    }

    public String getSatuan() {
        return satuan;
    }

    public void setSatuan(String satuan) {
        this.satuan = satuan;
    }
}
