package dao;

import utils.DatabaseConfig;
import utils.FormatUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserManagementDAO {

    public Map<String, String> getTopCardsData() {
        Map<String, String> data = new HashMap<>();
        data.put("total_users", "0");
        data.put("admin_users", "0");
        data.put("staff_users", "0");

        String query = "SELECT COUNT(*) AS total_users, "
                + "SUM(CASE WHEN a.id_admin IS NOT NULL THEN 1 ELSE 0 END) AS admin_users, "
                + "SUM(CASE WHEN s.id_staff IS NOT NULL THEN 1 ELSE 0 END) AS staff_users "
                + "FROM users u "
                + "LEFT JOIN admin a ON u.id_user = a.id_user "
                + "LEFT JOIN staff s ON u.id_user = s.id_user";
        try (Connection conn = DatabaseConfig.getKoneksi();
                PreparedStatement ps = conn.prepareStatement(query);
                ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                data.put("total_users", FormatUtil.formatAngka(rs.getDouble("total_users")));
                data.put("admin_users", FormatUtil.formatAngka(rs.getDouble("admin_users")));
                data.put("staff_users", FormatUtil.formatAngka(rs.getDouble("staff_users")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public int getTableTotalRows(String keyword) {
        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        String query = "SELECT COUNT(*) FROM users u"
                + " LEFT JOIN admin a ON u.id_user = a.id_user"
                + " LEFT JOIN owner o ON u.id_user = o.id_user"
                + " LEFT JOIN staff s ON u.id_user = s.id_user"
                + (hasKeyword ? " WHERE u.nama ILIKE ? OR u.username ILIKE ? OR u.email ILIKE ?" : "");
        try (Connection conn = DatabaseConfig.getKoneksi(); PreparedStatement ps = conn.prepareStatement(query)) {
            if (hasKeyword) {
                String search = "%" + keyword.trim() + "%";
                ps.setString(1, search);
                ps.setString(2, search);
                ps.setString(3, search);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<String[]> getTablePageData(int limit, int offset, String keyword) {
        List<String[]> data = new ArrayList<>();
        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        String query = "SELECT u.id_user, u.username, u.nama, u.email, u.no_telp, "
                + "CASE WHEN a.id_admin IS NOT NULL THEN 'Admin' "
                + "WHEN o.id_owner IS NOT NULL THEN 'Owner' "
                + "WHEN s.id_staff IS NOT NULL THEN 'Staff' ELSE 'User' END AS peran "
                + "FROM users u "
                + "LEFT JOIN admin a ON u.id_user = a.id_user "
                + "LEFT JOIN owner o ON u.id_user = o.id_user "
                + "LEFT JOIN staff s ON u.id_user = s.id_user"
                + (hasKeyword ? " WHERE u.nama ILIKE ? OR u.username ILIKE ? OR u.email ILIKE ?" : "")
                + " ORDER BY u.id_user DESC LIMIT ? OFFSET ?";
        try (Connection conn = DatabaseConfig.getKoneksi(); PreparedStatement ps = conn.prepareStatement(query)) {
            int idx = 1;
            if (hasKeyword) {
                String search = "%" + keyword.trim() + "%";
                ps.setString(idx++, search);
                ps.setString(idx++, search);
                ps.setString(idx++, search);
            }
            ps.setInt(idx++, limit);
            ps.setInt(idx, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    data.add(new String[] {
                            String.valueOf(rs.getInt("id_user")),
                            rs.getString("username"),
                            rs.getString("nama"),
                            rs.getString("email"),
                            rs.getString("no_telp"),
                            rs.getString("peran"),
                            String.valueOf(rs.getInt("id_user"))
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }
}
