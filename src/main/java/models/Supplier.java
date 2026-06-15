package models;

import dao.SupplierDAO;
import java.util.List;

public class Supplier {

    private int idSupplier;
    private String nama;
    private String alamat;
    private String noTelp;
    private String email;
    private final SupplierDAO dao = new SupplierDAO();

    public Supplier(int idSupplier, String nama, String alamat, String noTelp, String email) {
        this.idSupplier = idSupplier;
        this.nama = nama;
        this.alamat = alamat;
        this.noTelp = noTelp;
        this.email = email;
    }

    public List<String> getDaftarBahan() {
        return dao.getBahanBySupplierId(this.idSupplier);
    }

    public boolean tambahSupplier() {
        return dao.insertSupplier(this);
    }

    public boolean updateSupplier() {
        return dao.updateSupplier(this);
    }

    public boolean hapusSupplier() {
        return dao.deleteSupplier(this.idSupplier);
    }

    // Getter & Setter...
    public int getIdSupplier() {
        return idSupplier;
    }

    public String getNama() {
        return nama;
    }

    public String getAlamat() {
        return alamat;
    }

    public String getNoTelp() {
        return noTelp;
    }

    public String getEmail() {
        return email;
    }
}
