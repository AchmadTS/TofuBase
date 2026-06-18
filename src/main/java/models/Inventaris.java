package models;

import java.util.Date;

public class Inventaris {
    private int idInventaris;
    private Date tanggalCek;
    private String keterangan;

    public Inventaris() {
    }

    public Inventaris(int idInventaris, Date tanggalCek, String keterangan) {
        this.idInventaris = idInventaris;
        this.tanggalCek = tanggalCek;
        this.keterangan = keterangan;
    }

    public int getIdInventaris() {
        return idInventaris;
    }

    public void setIdInventaris(int idInventaris) {
        this.idInventaris = idInventaris;
    }

    public Date getTanggalCek() {
        return tanggalCek;
    }

    public void setTanggalCek(Date tanggalCek) {
        this.tanggalCek = tanggalCek;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }
}
