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
    public boolean login(String username, String password) {
        return true;
    }

    @Override
    public void logout() {
    }

    public void kelolaUser() {
    }

    public void kelolaDataMaster() {
    }

    public void kelolaProduksi() {
    }

    public void kelolaPenjualan() {
    }

    public void kelolaKeuangan() {
    }

    public void lihatLaporan() {
    }

    public String getJabatan() {
        return jabatan;
    }

    public String getLevelAkses() {
        return levelAkses;
    }
}
