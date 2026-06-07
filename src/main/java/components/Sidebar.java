package components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import utils.Theme;

public class Sidebar extends JPanel {

    public Sidebar() {
        setPreferredSize(new Dimension(250, 0));
        setBackground(Theme.SIDEBAR);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("TofuBase");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(Theme.TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Pabrik Tahu Sejahtera");
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

        JPanel userPanel = new JPanel(new BorderLayout(10, 0));
        userPanel.setOpaque(false);
        userPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        userPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        RoundedPanel avatar = new RoundedPanel(36, new Color(150, 200, 255));
        avatar.setPreferredSize(new Dimension(36, 36));
        avatar.setMinimumSize(new Dimension(36, 36));
        avatar.setMaximumSize(new Dimension(36, 36));
        avatar.setLayout(new BorderLayout());

        JLabel initLabel = new JLabel("OW", SwingConstants.CENTER);
        initLabel.setForeground(Theme.SIDEBAR);
        initLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        avatar.add(initLabel, BorderLayout.CENTER);

        JPanel userInfo = new JPanel(new GridLayout(2, 1));
        userInfo.setOpaque(false);
        JLabel userName = new JLabel("Pak Budi");
        userName.setForeground(Theme.TEXT_PRIMARY);
        userName.setFont(new Font("SansSerif", Font.BOLD, 14));
        JLabel userRole = new JLabel("Owner");
        userRole.setForeground(Theme.TEXT_SECONDARY);
        userRole.setFont(new Font("SansSerif", Font.PLAIN, 12));
        userInfo.add(userName);
        userInfo.add(userRole);

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
