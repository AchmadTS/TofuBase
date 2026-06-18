package models;

public class UserAccount {
    private int idUser;
    private String username;
    private String nama;
    private String email;
    private String noTelp;
    private String status;
    private String role;

    public UserAccount() {
    }

    public UserAccount(int idUser, String username, String nama, String email, String noTelp, String status, String role) {
        this.idUser = idUser;
        this.username = username;
        this.nama = nama;
        this.email = email;
        this.noTelp = noTelp;
        this.status = status;
        this.role = role;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNoTelp() {
        return noTelp;
    }

    public void setNoTelp(String noTelp) {
        this.noTelp = noTelp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
