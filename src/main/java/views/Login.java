package views;

import components.RoundedPanel;
import utils.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextAttribute;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

public class Login extends JFrame {

    private boolean isPasswordVisible = false;
    private final Color COLOR_LEFT_BG = Theme.SIDEBAR;
    private final Color COLOR_RIGHT_BG = Theme.BG;
    private final Color COLOR_INPUT_BG = Theme.CARD;
    private final Color COLOR_TEXT_MUTED = Theme.TEXT_SECONDARY;
    private final Color COLOR_PRIMARY = Theme.BLUE_ACCENT;
    private Preferences prefs = Preferences.userNodeForPackage(Login.class);

    public Login() {
        setTitle("TofuBase - Login");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- PANEL KIRI (SIDEBAR STYLE) ---
        add(createLeftPanel(), BorderLayout.WEST);
        // --- PANEL KANAN (DASHBOARD BG STYLE) ---
        add(createRightPanel(), BorderLayout.CENTER);
    }

    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel();
        leftPanel.setPreferredSize(new Dimension(480, 800));
        leftPanel.setBackground(COLOR_LEFT_BG);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(new EmptyBorder(70, 50, 60, 50));

        // -- Brand & Logo --
        JPanel brandRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        brandRow.setOpaque(false);
        brandRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        RoundedPanel logoBox = new RoundedPanel(12, COLOR_PRIMARY);
        logoBox.setPreferredSize(new Dimension(45, 45));
        logoBox.setLayout(new BorderLayout());
        JLabel lblLogo = new JLabel("⊞", SwingConstants.CENTER);
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setFont(new Font("SansSerif", Font.BOLD, 24));
        logoBox.add(lblLogo, BorderLayout.CENTER);

        JPanel textBrandPanel = new JPanel(new GridLayout(2, 1));
        textBrandPanel.setOpaque(false);
        JLabel lblBrandName = new JLabel("TofuBase");
        lblBrandName.setForeground(Theme.TEXT_PRIMARY);
        lblBrandName.setFont(new Font("SansSerif", Font.BOLD, 22));

        JLabel lblBrandSub = new JLabel("PABRIK TAHU");
        lblBrandSub.setForeground(COLOR_TEXT_MUTED);
        Font subFont = new Font("SansSerif", Font.BOLD, 10);
        Map<TextAttribute, Object> attributes = new HashMap<>();
        attributes.put(TextAttribute.TRACKING, 0.15);
        lblBrandSub.setFont(subFont.deriveFont(attributes));

        textBrandPanel.add(lblBrandName);
        textBrandPanel.add(lblBrandSub);
        brandRow.add(logoBox);
        brandRow.add(textBrandPanel);

        // -- Teks Deskripsi --
        JLabel lblTitle1 = new JLabel("Kelola produksi tahu");
        lblTitle1.setForeground(Theme.TEXT_PRIMARY);
        lblTitle1.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblTitle1.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTitle2 = new JLabel("lebih efisien");
        lblTitle2.setForeground(Theme.TEXT_PRIMARY);
        lblTitle2.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblTitle2.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblDesc1 = new JLabel("Pantau stok bahan, kelola batch produksi, dan lihat");
        lblDesc1.setForeground(COLOR_TEXT_MUTED);
        lblDesc1.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblDesc1.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblDesc2 = new JLabel("laporan keuangan pabrik dalam satu dashboard.");
        lblDesc2.setForeground(COLOR_TEXT_MUTED);
        lblDesc2.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblDesc2.setAlignmentX(Component.LEFT_ALIGNMENT);

        // -- Fitur List --
        JPanel featuresPanel = new JPanel();
        featuresPanel.setLayout(new BoxLayout(featuresPanel, BoxLayout.Y_AXIS));
        featuresPanel.setOpaque(false);
        featuresPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        featuresPanel.add(createFeatureItem("📊", "Laporan produksi real-time", new Color(COLOR_PRIMARY.getRed(), COLOR_PRIMARY.getGreen(), COLOR_PRIMARY.getBlue(), 40), COLOR_PRIMARY));
        featuresPanel.add(Box.createVerticalStrut(20));
        featuresPanel.add(createFeatureItem("📦", "Manajemen stok bahan baku", new Color(Theme.GREEN.getRed(), Theme.GREEN.getGreen(), Theme.GREEN.getBlue(), 40), Theme.GREEN));
        featuresPanel.add(Box.createVerticalStrut(20));
        featuresPanel.add(createFeatureItem("📑", "Laporan keuangan otomatis", new Color(Theme.WARNING.getRed(), Theme.WARNING.getGreen(), Theme.WARNING.getBlue(), 40), Theme.WARNING));

        JPanel divider = new JPanel();
        divider.setMaximumSize(new Dimension(350, 1));
        divider.setBackground(new Color(255, 255, 255, 20));
        divider.setAlignmentX(Component.LEFT_ALIGNMENT);

        // -- Statistik Bawah --
        JPanel statsRow = new JPanel(new GridLayout(1, 3, 10, 0));
        statsRow.setOpaque(false);
        statsRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        statsRow.setMaximumSize(new Dimension(380, 50));

        statsRow.add(createStatItem("240", "Produksi/hari", COLOR_PRIMARY));
        statsRow.add(createStatItem("85 kg", "Stok kedelai", Theme.GREEN));
        statsRow.add(createStatItem("Rp 4.2jt", "Pendapatan", Theme.WARNING));

        leftPanel.add(brandRow);
        leftPanel.add(Box.createVerticalStrut(70));
        leftPanel.add(lblTitle1);
        leftPanel.add(lblTitle2);
        leftPanel.add(Box.createVerticalStrut(15));
        leftPanel.add(lblDesc1);
        leftPanel.add(Box.createVerticalStrut(5));
        leftPanel.add(lblDesc2);
        leftPanel.add(Box.createVerticalStrut(40));
        leftPanel.add(featuresPanel);
        leftPanel.add(Box.createVerticalGlue());
        leftPanel.add(divider);
        leftPanel.add(Box.createVerticalStrut(25));
        leftPanel.add(statsRow);
        return leftPanel;
    }

    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(COLOR_RIGHT_BG);

        JPanel formBox = new JPanel(new GridBagLayout());
        formBox.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        // -- Ambil Data dari Preferences --
        boolean rememberMe = prefs.getBoolean("rememberMe", false);
        String savedEmail = prefs.get("email", "");
        String savedPassword = prefs.get("password", "");

        // -- Header Form --
        JLabel lblLogin = new JLabel("Masuk ke akun");
        lblLogin.setForeground(Theme.TEXT_PRIMARY);
        lblLogin.setFont(new Font("SansSerif", Font.BOLD, 28));
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 5, 0);
        formBox.add(lblLogin, gbc);

        JLabel lblWelcome = new JLabel("Selamat datang kembali di TofuBase.");
        lblWelcome.setForeground(COLOR_TEXT_MUTED);
        lblWelcome.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 45, 0);
        formBox.add(lblWelcome, gbc);

        // -- Input Email --
        JLabel lblEmail = new JLabel("EMAIL");
        lblEmail.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblEmail.setForeground(COLOR_TEXT_MUTED);
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 8, 0);
        formBox.add(lblEmail, gbc);

        RoundedPanel emailWrapper = new RoundedPanel(10, COLOR_INPUT_BG);
        emailWrapper.setLayout(new BorderLayout());
        emailWrapper.setPreferredSize(new Dimension(380, 48));
        emailWrapper.setBorder(new EmptyBorder(0, 15, 0, 15));

        JLabel lblIconMail = new JLabel("✉  ");
        lblIconMail.setFont(new Font("SansSerif", Font.PLAIN, 14));
        emailWrapper.add(lblIconMail, BorderLayout.WEST);

        JTextField txtEmail = new JTextField();
        txtEmail.setOpaque(false);
        txtEmail.setBorder(null);
        txtEmail.setCaretColor(Color.WHITE);
        txtEmail.setFont(new Font("SansSerif", Font.PLAIN, 14));

        // Masukkan data kalau Remember Me aktif
        if (rememberMe && !savedEmail.isEmpty()) {
            txtEmail.setText(savedEmail);
            txtEmail.setForeground(Theme.TEXT_PRIMARY);
            lblIconMail.setForeground(COLOR_PRIMARY);
        } else {
            txtEmail.setText("email@pabrik.com");
            txtEmail.setForeground(COLOR_TEXT_MUTED);
            lblIconMail.setForeground(COLOR_TEXT_MUTED);
        }

        txtEmail.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (txtEmail.getText().equals("email@pabrik.com")) {
                    txtEmail.setText("");
                    txtEmail.setForeground(Theme.TEXT_PRIMARY);
                    lblIconMail.setForeground(COLOR_PRIMARY);
                }
            }

            public void focusLost(FocusEvent e) {
                if (txtEmail.getText().isEmpty()) {
                    txtEmail.setText("email@pabrik.com");
                    txtEmail.setForeground(COLOR_TEXT_MUTED);
                    lblIconMail.setForeground(COLOR_TEXT_MUTED);
                }
            }
        });
        emailWrapper.add(txtEmail, BorderLayout.CENTER);

        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 20, 0);
        formBox.add(emailWrapper, gbc);

        // -- Input Password --
        JLabel lblPassword = new JLabel("KATA SANDI");
        lblPassword.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblPassword.setForeground(COLOR_TEXT_MUTED);
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 8, 0);
        formBox.add(lblPassword, gbc);

        RoundedPanel passWrapper = new RoundedPanel(10, COLOR_INPUT_BG);
        passWrapper.setLayout(new BorderLayout());
        passWrapper.setPreferredSize(new Dimension(380, 48));
        passWrapper.setBorder(new EmptyBorder(0, 15, 0, 10));

        JLabel lblIconLock = new JLabel("🔒  ");
        lblIconLock.setFont(new Font("SansSerif", Font.PLAIN, 14));
        passWrapper.add(lblIconLock, BorderLayout.WEST);

        JPasswordField txtPass = new JPasswordField();
        txtPass.setOpaque(false);
        txtPass.setBorder(null);
        txtPass.setCaretColor(Color.WHITE);
        txtPass.setFont(new Font("SansSerif", Font.PLAIN, 14));

        // Masukkan data kalau Remember Me aktif
        if (rememberMe && !savedPassword.isEmpty()) {
            txtPass.setText(savedPassword);
            txtPass.setForeground(Theme.TEXT_PRIMARY);
            txtPass.setEchoChar('•');
            lblIconLock.setForeground(COLOR_PRIMARY);
        } else {
            txtPass.setText("Masukkan kata sandi");
            txtPass.setForeground(COLOR_TEXT_MUTED);
            txtPass.setEchoChar((char) 0);
            lblIconLock.setForeground(COLOR_TEXT_MUTED);
        }

        JLabel lblEye = new JLabel("👁");
        lblEye.setForeground(COLOR_TEXT_MUTED);
        lblEye.setFont(new Font("SansSerif", Font.PLAIN, 16));
        lblEye.setCursor(new Cursor(Cursor.HAND_CURSOR));

        txtPass.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (String.valueOf(txtPass.getPassword()).equals("Masukkan kata sandi")) {
                    txtPass.setText("");
                    txtPass.setForeground(Theme.TEXT_PRIMARY);
                    lblIconLock.setForeground(COLOR_PRIMARY);
                    if (!isPasswordVisible) {
                        txtPass.setEchoChar('•');
                    }
                }
            }

            public void focusLost(FocusEvent e) {
                if (String.valueOf(txtPass.getPassword()).isEmpty()) {
                    txtPass.setEchoChar((char) 0);
                    txtPass.setText("Masukkan kata sandi");
                    txtPass.setForeground(COLOR_TEXT_MUTED);
                    lblIconLock.setForeground(COLOR_TEXT_MUTED);
                }
            }
        });

        lblEye.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                isPasswordVisible = !isPasswordVisible;
                if (isPasswordVisible) {
                    txtPass.setEchoChar((char) 0);
                    lblEye.setForeground(COLOR_PRIMARY);
                } else {
                    if (!String.valueOf(txtPass.getPassword()).equals("Masukkan kata sandi")) {
                        txtPass.setEchoChar('•');
                    }
                    lblEye.setForeground(COLOR_TEXT_MUTED);
                }
            }
        });

        passWrapper.add(txtPass, BorderLayout.CENTER);
        passWrapper.add(lblEye, BorderLayout.EAST);

        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 15, 0);
        formBox.add(passWrapper, gbc);

        // -- Checkbox --
        JPanel optionsRow = new JPanel(new BorderLayout());
        optionsRow.setOpaque(false);

        JCheckBox chkRemember = new JCheckBox("Ingat saya");
        chkRemember.setOpaque(false);
        chkRemember.setForeground(COLOR_TEXT_MUTED);
        chkRemember.setFont(new Font("SansSerif", Font.PLAIN, 13));
        chkRemember.setFocusPainted(false);
        chkRemember.setCursor(new Cursor(Cursor.HAND_CURSOR));
        chkRemember.setSelected(rememberMe);

        optionsRow.add(chkRemember, BorderLayout.WEST);

        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 35, 0);
        formBox.add(optionsRow, gbc);

        // -- Tombol Login --
        RoundedPanel btnWrapper = new RoundedPanel(10, COLOR_PRIMARY);
        btnWrapper.setLayout(new BorderLayout());
        btnWrapper.setPreferredSize(new Dimension(380, 48));
        btnWrapper.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel lblBtnText = new JLabel("Masuk ke Akun", SwingConstants.CENTER);
        lblBtnText.setForeground(Color.WHITE);
        lblBtnText.setFont(new Font("SansSerif", Font.BOLD, 15));
        btnWrapper.add(lblBtnText, BorderLayout.CENTER);

        // --- AUTENTIKASI DATABASE & PREFERENCES ---
        btnWrapper.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btnWrapper.setBackground(COLOR_PRIMARY.darker());
                btnWrapper.repaint();
            }

            public void mouseExited(MouseEvent e) {
                btnWrapper.setBackground(COLOR_PRIMARY);
                btnWrapper.repaint();
            }

            public void mouseClicked(MouseEvent e) {
                String email = txtEmail.getText();
                String password = String.valueOf(txtPass.getPassword());

                // Validasi Input
                if (email.isEmpty() || email.equals("email@pabrik.com") || password.isEmpty() || password.equals("Masukkan kata sandi")) {
                    JOptionPane.showMessageDialog(Login.this, "Email dan Kata Sandi tidak boleh kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    Connection conn = utils.DatabaseConfig.getKoneksi();
                    String sql = "SELECT u.nama, "
                            + "COALESCE(a.jabatan, s.jabatan, o.jabatan, 'Pengguna') AS peran "
                            + "FROM users u "
                            + "LEFT JOIN admin a ON u.id_user = a.id_user "
                            + "LEFT JOIN staff s ON u.id_user = s.id_user "
                            + "LEFT JOIN owner o ON u.id_user = o.id_user "
                            + "WHERE u.email = ? AND u.password = ?";

                    PreparedStatement pst = conn.prepareStatement(sql);
                    pst.setString(1, email);
                    pst.setString(2, password);
                    ResultSet rs = pst.executeQuery();

                    if (rs.next()) {
                        String namaUser = rs.getString("nama");
                        String roleUser = rs.getString("peran");
                        if (chkRemember.isSelected()) {
                            prefs.putBoolean("rememberMe", true);
                            prefs.put("email", email);
                            prefs.put("password", password);
                        } else {
                            prefs.putBoolean("rememberMe", false);
                            prefs.remove("email");
                            prefs.remove("password");
                        }

                        // Masuk Dashboard
                        dispose();
                        new MainFrame(namaUser, roleUser).setVisible(true);

                    } else {
                        JOptionPane.showMessageDialog(Login.this, "Email atau Kata Sandi salah!", "Login Gagal", JOptionPane.ERROR_MESSAGE);
                    }

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(Login.this, "Koneksi Database Gagal: \n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Gagal Login: " + ex.getMessage());
                }
            }
        });

        gbc.gridy = 7;
        gbc.insets = new Insets(0, 0, 80, 0);
        formBox.add(btnWrapper, gbc);

        // -- Footer --
        JLabel lblFooter = new JLabel("© 2026 TofuBase — Sistem Manajemen Pabrik Tahu", SwingConstants.CENTER);
        lblFooter.setForeground(new Color(255, 255, 255, 60));
        lblFooter.setFont(new Font("SansSerif", Font.PLAIN, 11));

        gbc.gridy = 8;
        gbc.insets = new Insets(0, 0, 0, 0);
        formBox.add(lblFooter, gbc);

        rightPanel.add(formBox);
        return rightPanel;
    }

    private JPanel createFeatureItem(String iconText, String labelText, Color bgIconColor, Color iconColor) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        panel.setOpaque(false);

        RoundedPanel iconBox = new RoundedPanel(10, bgIconColor);
        iconBox.setPreferredSize(new Dimension(35, 35));
        iconBox.setLayout(new BorderLayout());
        JLabel lblIcon = new JLabel(iconText, SwingConstants.CENTER);
        lblIcon.setForeground(iconColor);
        lblIcon.setFont(new Font("SansSerif", Font.PLAIN, 14));
        iconBox.add(lblIcon, BorderLayout.CENTER);

        JLabel lblText = new JLabel(labelText);
        lblText.setForeground(COLOR_TEXT_MUTED);
        lblText.setFont(new Font("SansSerif", Font.PLAIN, 13));

        panel.add(iconBox);
        panel.add(lblText);
        return panel;
    }

    private JPanel createStatItem(String value, String title, Color valColor) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel lblVal = new JLabel(value);
        lblVal.setForeground(valColor);
        lblVal.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblVal.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setForeground(COLOR_TEXT_MUTED);
        lblTitle.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(lblVal);
        panel.add(lblTitle);
        return panel;
    }
}
