package models;

public class Owner extends User {

    private String jabatan;
    private String levelAkses;

    public Owner(int id, String username, String password, String nama, String email, String noTelp, String status, String jabatan, String levelAkses) {
        super(id, username, password, nama, email, noTelp, status);
        this.jabatan = jabatan;
        this.levelAkses = levelAkses;
    }

    @Override
    public boolean verifikasiAksesMenu(String menuName) {
        return true;
    }

    public boolean kelolaUser() {
        return true;
    }

    public boolean kelolaDataMaster() {
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
