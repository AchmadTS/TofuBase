package models;

import java.util.Date;

public class Admin extends User {

    private String jabatan;
    private String levelAkses;
    private Date tanggalDibuat;

    public Admin(int id, String username, String password, String nama, String email, String noTelp, String status, String jabatan, String levelAkses, Date tanggalDibuat) {
        super(id, username, password, nama, email, noTelp, status);
        this.jabatan = jabatan;
        this.levelAkses = levelAkses;
        this.tanggalDibuat = tanggalDibuat;
    }

    @Override
    public boolean verifikasiAksesMenu(String menuName) {
        return switch (menuName) {
            case "Kelola User" ->
                kelolaUser();
            case "Bahan Baku", "Stok & Distribusi" ->
                kelolaDataMaster(menuName);
            case "Produksi" ->
                kelolaProduksi();
            case "Laporan Keuangan" ->
                kelolaKeuangan();
            case "Dashboard" ->
                lihatLaporan();
            default ->
                false;
        };
    }

    public boolean kelolaUser() {
        return true;
    }

    public boolean kelolaDataMaster(String modul) {
        return true;
    }

    public boolean kelolaProduksi() {
        return true;
    }

    public boolean kelolaPenjualan() {
        return true;
    }

    public boolean kelolaKeuangan() {
        return true;
    }

    public boolean lihatLaporan() {
        return true;
    }

    public String getJabatan() {
        return jabatan;
    }

    public String getLevelAkses() {
        return levelAkses;
    }
}
