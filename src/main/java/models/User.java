package models;

public abstract class User {

    private int id;
    private String username;
    private String password;
    private String nama;
    private String email;
    private String noTelp;
    private String status;

    public User(int id, String username, String password, String nama, String email, String noTelp, String status) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.nama = nama;
        this.email = email;
        this.noTelp = noTelp;
        this.status = status;
    }

    public boolean login(String inputUsername, String inputPassword) {
        if (this.email.equals(inputUsername) && this.password.equals(inputPassword)) {
            this.status = "Online";
            return true;
        }
        return false;
    }

    public void logout() {
        this.status = "Offline";
    }

    public abstract boolean verifikasiAksesMenu(String menuName);

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getNama() {
        return nama;
    }

    public String getEmail() {
        return email;
    }

    public String getNoTelp() {
        return noTelp;
    }

    public String getStatus() {
        return status;
    }
}
