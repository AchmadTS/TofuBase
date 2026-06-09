package components;
import views.BahanBaku;
import views.Dashboard;
import utils.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Sidebar extends JPanel {

    private String currentUserName;
    private String currentUserRole;
    private JFrame parentFrame; 

    public Sidebar(String userName, String userRole, String activeMenuName) {
        this.currentUserName = userName;
        this.currentUserRole = userRole;

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
        userPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int confirm = JOptionPane.showConfirmDialog(Sidebar.this, "Apakah Anda yakin ingin keluar?", "Konfirmasi Log Out", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    JFrame current = getParentFrame();
                    if (current != null) current.dispose();
                    new views.Login().setVisible(true);
                }
            }
        });

        add(userPanel);
    }

    private JPanel createNavMenuItem(String icon, String text, String activeMenuName) {
        boolean isActive = text.equals(activeMenuName);

        JPanel panel;
        if (isActive) {
            panel = new RoundedPanel(15, new Color(220, 235, 255));
        } else {
            panel = new JPanel();
            panel.setOpaque(false);
        }

        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40)); 
        panel.setBorder(new EmptyBorder(0, 15, 0, 15));
        
        if (!isActive) {
            panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        Color fgColor = isActive ? Theme.BLUE_ACCENT : Theme.TEXT_SECONDARY;
        int fontWeight = isActive ? Font.BOLD : Font.PLAIN;

        JLabel lblIcon = new JLabel(icon, SwingConstants.CENTER);
        lblIcon.setForeground(fgColor);
        lblIcon.setFont(new Font("SansSerif", fontWeight, isActive ? 18 : 16));
        lblIcon.setPreferredSize(new Dimension(24, 24));
        lblIcon.setMaximumSize(new Dimension(24, 24));

        JLabel lblText = new JLabel(text);
        lblText.setFont(new Font("SansSerif", fontWeight, 14));
        lblText.setForeground(fgColor);

        panel.add(lblIcon);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(lblText);
        if (!isActive) {
            panel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    lblText.setForeground(Theme.TEXT_PRIMARY);
                    lblIcon.setForeground(Theme.TEXT_PRIMARY);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    lblText.setForeground(Theme.TEXT_SECONDARY);
                    lblIcon.setForeground(Theme.TEXT_SECONDARY);
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    handleNavigation(text);
                }
            });
        }

        return panel;
    }

    private JFrame getParentFrame() {
        if (parentFrame == null) {
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window instanceof JFrame) {
                parentFrame = (JFrame) window;
            }
        }
        return parentFrame;
    }

    private void handleNavigation(String destination) {
        JFrame currentFrame = getParentFrame();        
        int windowState = JFrame.NORMAL;
        Rectangle windowBounds = null;
        
        if (currentFrame != null) {
            windowState = currentFrame.getExtendedState();
            windowBounds = currentFrame.getBounds();
        }

        JFrame nextFrame = null;
        switch (destination) {
            case "Dashboard":
                nextFrame = new Dashboard(currentUserName, currentUserRole);
                break;
            case "Bahan Baku":
                nextFrame = new BahanBaku(currentUserName, currentUserRole);
                break;
            default:
                JOptionPane.showMessageDialog(this, "Halaman " + destination + " sedang dalam pengembangan.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
        }

        if (nextFrame != null) {
            if (windowBounds != null) {
                nextFrame.setBounds(windowBounds);
            }
            nextFrame.setExtendedState(windowState);
            
            if (currentFrame != null) {
                currentFrame.dispose();
            }
            
            nextFrame.setVisible(true);
        }
    }
}