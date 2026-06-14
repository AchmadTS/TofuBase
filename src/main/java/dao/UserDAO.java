package dao;

import models.User;
import models.Staff;
import models.Admin;
import models.Owner;
import utils.DatabaseConfig;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    public User authenticateUser(String email, String password) throws SQLException {
        String query = "SELECT u.*, "
                + "a.id_admin, a.jabatan AS admin_jabatan, a.level_akses AS admin_level, a.tanggal_dibuat, "
                + "o.id_owner, o.jabatan AS owner_jabatan, o.level_akses AS owner_level, "
                + "s.id_staff, s.jabatan AS staff_jabatan, s.tanggal_masuk "
                + "FROM users u "
                + "LEFT JOIN admin a ON u.id_user = a.id_user "
                + "LEFT JOIN owner o ON u.id_user = o.id_user "
                + "LEFT JOIN staff s ON u.id_user = s.id_user "
                + "WHERE u.email = ? AND u.password = ? AND u.status = 'Aktif'";
        try (Connection conn = DatabaseConfig.getKoneksi(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, email);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id_user");
                    String user = rs.getString("username");
                    String pass = rs.getString("password");
                    String nama = rs.getString("nama");
                    String eml = rs.getString("email");
                    String telp = rs.getString("no_telp");
                    String status = rs.getString("status");

                    if (rs.getInt("id_admin") != 0) {
                        return new Admin(id, user, pass, nama, eml, telp, status,
                                rs.getString("admin_jabatan"),
                                rs.getString("admin_level"),
                                rs.getDate("tanggal_dibuat"));
                    } else if (rs.getInt("id_owner") != 0) {
                        return new Owner(id, user, pass, nama, eml, telp, status,
                                rs.getString("owner_jabatan"),
                                rs.getString("owner_level"));
                    } else if (rs.getInt("id_staff") != 0) {
                        return new Staff(id, user, pass, nama, eml, telp, status,
                                rs.getString("staff_jabatan"),
                                rs.getDate("tanggal_masuk"));
                    }
                }
            }
        }
        return null;
    }
}
