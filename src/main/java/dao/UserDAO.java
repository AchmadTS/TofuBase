package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import utils.DatabaseConfig;

public class UserDAO {
    public String[] authenticateUser(String email, String password) throws SQLException {
        String sql = "SELECT u.nama, "
                + "COALESCE(a.jabatan, s.jabatan, o.jabatan, 'Pengguna') AS peran "
                + "FROM users u "
                + "LEFT JOIN admin a ON u.id_user = a.id_user "
                + "LEFT JOIN staff s ON u.id_user = s.id_user "
                + "LEFT JOIN owner o ON u.id_user = o.id_user "
                + "WHERE u.email = ? AND u.password = ?";

        try (Connection conn = DatabaseConfig.getKoneksi(); PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, email);
            pst.setString(2, password);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new String[]{rs.getString("nama"), rs.getString("peran")};
                }
            }
        }
        return null;
    }
}
