package models;

import java.util.Date;

public class Staff extends User {

    private String jabatan;
    private Date tanggalMasuk;

    public Staff(int id, String username, String password, String nama, String email, String noTelp, String status, String jabatan, Date tanggalMasuk) {
        super(id, username, password, nama, email, noTelp, status);
        this.jabatan = jabatan;
        this.tanggalMasuk = tanggalMasuk;
    }

    @Override
    public boolean login(String username, String password) {
        return true;
    }

    @Override
    public void logout() {
    }

    public void inputBahanBaku() {
        System.out.println("Akses buka form bahan baku...");
    }

    public void inputHasilProduksi() {
        System.out.println("Akses form produksi...");
    }

    public void lihatData() {
        System.out.println("Melihat data...");
    }

    public String getJabatan() {
        return jabatan;
    }

    public Date getTanggalMasuk() {
        return tanggalMasuk;
    }
}
