package components;

import views.LoginView;
import utils.Theme;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Sidebar extends JPanel {

    private String currentUserName;
    private String currentUserRole;
    private Consumer<String> navigationCallback;
    private Map<String, JPanel> menuPanels = new HashMap<>();
    private Map<String, JLabel[]> menuLabels = new HashMap<>();

    public Sidebar(String userName, String userRole, String activeMenuName, Consumer<String> onNavigate) {
        this.currentUserName = userName;
        this.currentUserRole = userRole;
        this.navigationCallback = onNavigate;

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

        // MENU UTAMA
        JLabel menuLabel = new JLabel("MENU UTAMA");
        menuLabel.setFont(new Font("SansSerif", Font.BOLD, 10));
        menuLabel.setForeground(Theme.TEXT_SECONDARY);
        menuLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(menuLabel);
        add(Box.createVerticalStrut(10));

        // List Menu Utama
        add(createNavMenuItem("⊞", "Dashboard", activeMenuName));
        add(Box.createVerticalStrut(10));
        add(createNavMenuItem("○", "Bahan Baku", activeMenuName));
        add(Box.createVerticalStrut(10));
        add(createNavMenuItem("≡", "Produksi", activeMenuName));
        add(Box.createVerticalStrut(10));
        add(createNavMenuItem("◇", "Stok & Distribusi", activeMenuName));

        add(Box.createVerticalStrut(30));

        // KEUANGAN
        JLabel keuanganLabel = new JLabel("KEUANGAN");
        keuanganLabel.setFont(new Font("SansSerif", Font.BOLD, 10));
        keuanganLabel.setForeground(Theme.TEXT_SECONDARY);
        keuanganLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(keuanganLabel);
        add(Box.createVerticalStrut(10));

        add(createNavMenuItem("↗", "Laporan Keuangan", activeMenuName));

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
        userPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane optionPane = new JOptionPane("Apakah Anda yakin ingin keluar?", JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
                JDialog dialog = optionPane.createDialog("Konfirmasi Log Out");
                dialog.setLocationRelativeTo(null);
                dialog.setVisible(true);
                Object value = optionPane.getValue();
                if (value instanceof Integer confirm && confirm == JOptionPane.YES_OPTION) {
                    Window window = SwingUtilities.getWindowAncestor(Sidebar.this);
                    if (window != null) {
                        window.dispose();
                    }
                    new LoginView().setVisible(true);
                }
            }
        });

        add(userPanel);
    }

    private JPanel createNavMenuItem(String icon, String text, String activeMenuName) {
        RoundedPanel panel = new RoundedPanel(15, Theme.SIDEBAR);
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        panel.setBorder(new EmptyBorder(0, 15, 0, 15));
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel lblIcon = new JLabel(icon, SwingConstants.CENTER);
        lblIcon.setFont(new Font("SansSerif", Font.PLAIN, 16));
        lblIcon.setPreferredSize(new Dimension(24, 24));
        lblIcon.setMaximumSize(new Dimension(24, 24));

        JLabel lblText = new JLabel(text);
        lblText.setFont(new Font("SansSerif", Font.PLAIN, 14));

        panel.add(lblIcon);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(lblText);

        menuPanels.put(text, panel);
        menuLabels.put(text, new JLabel[]{lblIcon, lblText});

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (navigationCallback != null) {
                    navigationCallback.accept(text);
                }
            }
        });

        return panel;
    }

    public void setActiveMenu(String activeMenuName) {
        for (String menuName : menuPanels.keySet()) {
            RoundedPanel panel = (RoundedPanel) menuPanels.get(menuName);
            JLabel[] labels = menuLabels.get(menuName);
            boolean isActive = menuName.equals(activeMenuName);

            if (isActive) {
                panel.setBackground(new Color(220, 235, 255));
                labels[0].setForeground(Theme.BLUE_ACCENT);
                labels[0].setFont(new Font("SansSerif", Font.BOLD, 18));
                labels[1].setForeground(Theme.BLUE_ACCENT);
                labels[1].setFont(new Font("SansSerif", Font.BOLD, 14));
            } else {
                panel.setBackground(Theme.SIDEBAR);
                labels[0].setForeground(Theme.TEXT_SECONDARY);
                labels[0].setFont(new Font("SansSerif", Font.PLAIN, 16));
                labels[1].setForeground(Theme.TEXT_SECONDARY);
                labels[1].setFont(new Font("SansSerif", Font.PLAIN, 14));
            }
            panel.repaint();
        }
    }
}
