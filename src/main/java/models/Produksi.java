package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Produksi {

    private int idProduksi;
    private Date tanggal;
    private String keterangan;
    private double hasilTahu;
    private String namaOperator;
    private String status;
    private List<RecordProduksi> records;

    public Produksi(int idProduksi, Date tanggal, String keterangan, double hasilTahu, String namaOperator, String status) {
        this.idProduksi = idProduksi;
        this.tanggal = tanggal;
        this.keterangan = keterangan;
        this.hasilTahu = hasilTahu;
        this.namaOperator = namaOperator;
        this.status = status;
        this.records = new ArrayList<>();
    }

    public double getHasilTahu() {
        return hasilTahu;
    }

    public String getNamaOperator() {
        return namaOperator;
    }

    public String getStatus() {
        return status;
    }

    public void prosesProduksi() {
        
    }

    public void batalProduksi() {
        
    }

    public void cetakProduksi() {
        
    }

    public void addRecord(RecordProduksi record) {
        this.records.add(record);
    }

    public int getIdProduksi() {
        return idProduksi;
    }

    public void setIdProduksi(int idProduksi) {
        this.idProduksi = idProduksi;
    }

    public Date getTanggal() {
        return tanggal;
    }

    public void setTanggal(Date tanggal) {
        this.tanggal = tanggal;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public List<RecordProduksi> getRecords() {
        return records;
    }

    public void setRecords(List<RecordProduksi> records) {
        this.records = records;
    }
}
