package components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import utils.Theme;

public class Sidebar extends JPanel {

    public Sidebar(String userName, String userRole) {
        setPreferredSize(new Dimension(250, 0));
        setBackground(Theme.SIDEBAR);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("TofuBase");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(Theme.TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Pabrik Tahu");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitle.setForeground(Theme.TEXT_SECONDARY);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        add(title);
        add(subtitle);
        add(Box.createVerticalStrut(40));

        JLabel menuLabel = new JLabel("MENU UTAMA");
        menuLabel.setFont(new Font("SansSerif", Font.BOLD, 10));
        menuLabel.setForeground(Theme.TEXT_SECONDARY);
        menuLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(menuLabel);
        add(Box.createVerticalStrut(10));

        RoundedPanel activeMenu = new RoundedPanel(15, new Color(220, 235, 255));
        activeMenu.setLayout(new BoxLayout(activeMenu, BoxLayout.X_AXIS));
        activeMenu.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        activeMenu.setBorder(new EmptyBorder(0, 15, 0, 15));
        activeMenu.setAlignmentX(Component.LEFT_ALIGNMENT);
        activeMenu.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel dashboardIcon = new JLabel("⊞", SwingConstants.CENTER);
        dashboardIcon.setFont(new Font("SansSerif", Font.BOLD, 18));
        dashboardIcon.setForeground(Theme.BLUE_ACCENT);
        dashboardIcon.setPreferredSize(new Dimension(24, 24));
        dashboardIcon.setMaximumSize(new Dimension(24, 24));

        JLabel dashboardLabel = new JLabel("Dashboard");
        dashboardLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        dashboardLabel.setForeground(Theme.BLUE_ACCENT);

        activeMenu.add(dashboardIcon);
        activeMenu.add(Box.createHorizontalStrut(10));
        activeMenu.add(dashboardLabel);
        add(activeMenu);

        add(Box.createVerticalStrut(10));
        add(createMenuItem("○", "Bahan Baku"));
        add(Box.createVerticalStrut(10));
        add(createMenuItem("≡", "Produksi"));
        add(Box.createVerticalStrut(10));
        add(createMenuItem("◇", "Stok & Distribusi"));

        add(Box.createVerticalStrut(30));

        JLabel keuanganLabel = new JLabel("KEUANGAN");
        keuanganLabel.setFont(new Font("SansSerif", Font.BOLD, 10));
        keuanganLabel.setForeground(Theme.TEXT_SECONDARY);
        keuanganLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(keuanganLabel);
        add(Box.createVerticalStrut(10));

        add(createMenuItem("↗", "Laporan Keuangan"));

        add(Box.createVerticalGlue());

        // --- PROFIL USER ---
        JPanel userPanel = new JPanel(new BorderLayout(10, 0));
        userPanel.setOpaque(false);
        userPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        userPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        userPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        RoundedPanel avatar = new RoundedPanel(36, new Color(150, 200, 255));
        avatar.setPreferredSize(new Dimension(36, 36));
        avatar.setMinimumSize(new Dimension(36, 36));
        avatar.setMaximumSize(new Dimension(36, 36));
        avatar.setLayout(new BorderLayout());

        // Inisial Nama (Misal: Achmad Tirto Sudiro -> AS)
        String initials = "US";
        if (userName != null && !userName.trim().isEmpty()) {
            String[] words = userName.trim().split("\\s+");
            if (words.length == 1) {
                initials = words[0].substring(0, Math.min(2, words[0].length())).toUpperCase();
            } else {
                initials = (words[0].substring(0, 1) + words[words.length - 1].substring(0, 1)).toUpperCase();
            }
        }

        JLabel initLabel = new JLabel(initials, SwingConstants.CENTER);
        initLabel.setForeground(Theme.SIDEBAR);
        initLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        avatar.add(initLabel, BorderLayout.CENTER);

        JPanel userInfo = new JPanel(new GridLayout(2, 1));
        userInfo.setOpaque(false);

        JLabel lblUserName = new JLabel(userName != null ? userName : "User Pabrik");
        lblUserName.setForeground(Theme.TEXT_PRIMARY);
        lblUserName.setFont(new Font("SansSerif", Font.BOLD, 14));

        JLabel lblUserRole = new JLabel(userRole != null ? userRole : "Staff");
        lblUserRole.setForeground(Theme.TEXT_SECONDARY);
        lblUserRole.setFont(new Font("SansSerif", Font.PLAIN, 12));

        userInfo.add(lblUserName);
        userInfo.add(lblUserRole);

        userPanel.add(avatar, BorderLayout.WEST);
        userPanel.add(userInfo, BorderLayout.CENTER);

        add(userPanel);
    }

    private JPanel createMenuItem(String icon, String text) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        panel.setBorder(new EmptyBorder(0, 15, 0, 15));
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel lblIcon = new JLabel(icon, SwingConstants.CENTER);
        lblIcon.setForeground(Theme.TEXT_SECONDARY);
        lblIcon.setFont(new Font("SansSerif", Font.PLAIN, 16));
        lblIcon.setPreferredSize(new Dimension(24, 24));
        lblIcon.setMaximumSize(new Dimension(24, 24));

        JLabel lblText = new JLabel(text);
        lblText.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblText.setForeground(Theme.TEXT_SECONDARY);

        panel.add(lblIcon);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(lblText);
        return panel;
    }
}
