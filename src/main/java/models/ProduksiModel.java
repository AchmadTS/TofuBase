package models;

import java.util.Date;

public class ProduksiModel {
    private int idProduksi;
    private int idProduk;
    private String batch;
    private Date tanggal;
    private int hasilTahu;
    private int idUser;
    private String status;
    private String keterangan;

    public ProduksiModel() {
    }

    public ProduksiModel(int idProduksi, int idProduk, String batch, Date tanggal, int hasilTahu, int idUser, String status, String keterangan) {
        this.idProduksi = idProduksi;
        this.idProduk = idProduk;
        this.batch = batch;
        this.tanggal = tanggal;
        this.hasilTahu = hasilTahu;
        this.idUser = idUser;
        this.status = status;
        this.keterangan = keterangan;
    }

    public int getIdProduksi() {
        return idProduksi;
    }

    public void setIdProduksi(int idProduksi) {
        this.idProduksi = idProduksi;
    }

    public int getIdProduk() {
        return idProduk;
    }

    public void setIdProduk(int idProduk) {
        this.idProduk = idProduk;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public Date getTanggal() {
        return tanggal;
    }

    public void setTanggal(Date tanggal) {
        this.tanggal = tanggal;
    }

    public int getHasilTahu() {
        return hasilTahu;
    }

    public void setHasilTahu(int hasilTahu) {
        this.hasilTahu = hasilTahu;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }
}
