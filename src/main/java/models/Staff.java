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
    public boolean verifikasiAksesMenu(String menuName) {
        return switch (menuName) {
            case "Bahan Baku" ->
                inputBahanBaku();
            case "Produksi" ->
                inputHasilProduksi();
            case "Stok & Distribusi", "Dashboard" ->
                lihatData(menuName);
            default -> {
                System.err.println("[SECURITY BLOCK] Staff " + getNama() + " mencoba mengakses area terlarang: " + menuName);
                yield false;
            }
        };
    }

    public boolean inputBahanBaku() {
        return true;
    }

    public boolean inputHasilProduksi() {
        return true;
    }

    public boolean lihatData(String modul) {
        return true;
    }

    public String getJabatan() {
        return jabatan;
    }

    public Date getTanggalMasuk() {
        return tanggalMasuk;
    }
}
