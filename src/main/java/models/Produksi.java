package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Produksi {

    private int idProduksi;
    private int idProduk;
    private String batch;
    private Date tanggal;
    private String keterangan;
    private double hasilTahu;
    private int idUser;
    private String namaOperator;
    private String status;
    private List<RecordProduksi> records;

    public Produksi() {
        this.records = new ArrayList<>();
    }

    public Produksi(int idProduksi, Date tanggal, String keterangan, double hasilTahu, String namaOperator, String status) {
        this.idProduksi = idProduksi;
        this.tanggal = tanggal;
        this.keterangan = keterangan;
        this.hasilTahu = hasilTahu;
        this.namaOperator = namaOperator;
        this.status = status;
        this.records = new ArrayList<>();
    }

    public Produksi(int idProduksi, int idProduk, String batch, Date tanggal, double hasilTahu, int idUser, String status, String keterangan) {
        this.idProduksi = idProduksi;
        this.idProduk = idProduk;
        this.batch = batch;
        this.tanggal = tanggal;
        this.hasilTahu = hasilTahu;
        this.idUser = idUser;
        this.status = status;
        this.keterangan = keterangan;
        this.records = new ArrayList<>();
    }

    public int getIdProduksi() { return idProduksi; }
    public void setIdProduksi(int idProduksi) { this.idProduksi = idProduksi; }

    public int getIdProduk() { return idProduk; }
    public void setIdProduk(int idProduk) { this.idProduk = idProduk; }

    public String getBatch() { return batch; }
    public void setBatch(String batch) { this.batch = batch; }

    public Date getTanggal() { return tanggal; }
    public void setTanggal(Date tanggal) { this.tanggal = tanggal; }

    public String getKeterangan() { return keterangan; }
    public void setKeterangan(String keterangan) { this.keterangan = keterangan; }

    public double getHasilTahu() { return hasilTahu; }
    public void setHasilTahu(double hasilTahu) { this.hasilTahu = hasilTahu; }

    public int getIdUser() { return idUser; }
    public void setIdUser(int idUser) { this.idUser = idUser; }

    public String getNamaOperator() { return namaOperator; }
    public void setNamaOperator(String namaOperator) { this.namaOperator = namaOperator; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<RecordProduksi> getRecords() { return records; }
    public void setRecords(List<RecordProduksi> records) { this.records = records; }

    public void addRecord(RecordProduksi record) {
        if (this.records == null) {
            this.records = new ArrayList<>();
        }
        this.records.add(record);
    }
}