package views;

import components.RoundedPanel;
import controllers.LoginController;
import utils.Theme;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;

public class LoginView extends JFrame {

    private boolean isPasswordVisible = false;
    private JTextField txtEmail;
    private JPasswordField txtPass;
    private JCheckBox chkRemember;
    private RoundedPanel btnLogin;
    private JLabel lblIconMail;
    private JLabel lblIconLock;
    private final String EMAIL_PLACEHOLDER = "email@pabrik.com";
    private final String PASS_PLACEHOLDER = "Masukkan kata sandi";

    public LoginView() {
        setTitle("TofuBase - Login");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        add(createLeftPanel(), BorderLayout.WEST);
        add(createRightPanel(), BorderLayout.CENTER);
        new LoginController(this);
    }

    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel();
        leftPanel.setPreferredSize(new Dimension(480, 800));
        leftPanel.setBackground(Theme.SIDEBAR);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(new EmptyBorder(70, 50, 60, 50));

        JPanel brandRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        brandRow.setOpaque(false);
        brandRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        RoundedPanel logoBox = new RoundedPanel(12, Theme.BLUE_ACCENT);
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
        lblBrandSub.setForeground(Theme.TEXT_SECONDARY);
        Font subFont = new Font("SansSerif", Font.BOLD, 10);
        Map<TextAttribute, Object> attributes = new HashMap<>();
        attributes.put(TextAttribute.TRACKING, 0.15);
        lblBrandSub.setFont(subFont.deriveFont(attributes));

        textBrandPanel.add(lblBrandName);
        textBrandPanel.add(lblBrandSub);
        brandRow.add(logoBox);
        brandRow.add(textBrandPanel);

        JLabel lblTitle1 = new JLabel("Kelola produksi tahu");
        lblTitle1.setForeground(Theme.TEXT_PRIMARY);
        lblTitle1.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblTitle1.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTitle2 = new JLabel("lebih efisien");
        lblTitle2.setForeground(Theme.TEXT_PRIMARY);
        lblTitle2.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblTitle2.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblDesc1 = new JLabel("Pantau stok bahan, kelola batch produksi, dan lihat");
        lblDesc1.setForeground(Theme.TEXT_SECONDARY);
        lblDesc1.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblDesc1.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblDesc2 = new JLabel("laporan keuangan pabrik dalam satu dashboard.");
        lblDesc2.setForeground(Theme.TEXT_SECONDARY);
        lblDesc2.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblDesc2.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel featuresPanel = new JPanel();
        featuresPanel.setLayout(new BoxLayout(featuresPanel, BoxLayout.Y_AXIS));
        featuresPanel.setOpaque(false);
        featuresPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        featuresPanel.add(createFeatureItem("📊", "Laporan produksi real-time", new Color(Theme.BLUE_ACCENT.getRed(), Theme.BLUE_ACCENT.getGreen(), Theme.BLUE_ACCENT.getBlue(), 40), Theme.BLUE_ACCENT));
        featuresPanel.add(Box.createVerticalStrut(20));
        featuresPanel.add(createFeatureItem("📦", "Manajemen stok bahan baku", new Color(Theme.GREEN.getRed(), Theme.GREEN.getGreen(), Theme.GREEN.getBlue(), 40), Theme.GREEN));
        featuresPanel.add(Box.createVerticalStrut(20));
        featuresPanel.add(createFeatureItem("📑", "Laporan keuangan otomatis", new Color(Theme.WARNING.getRed(), Theme.WARNING.getGreen(), Theme.WARNING.getBlue(), 40), Theme.WARNING));

        JPanel divider = new JPanel();
        divider.setMaximumSize(new Dimension(350, 1));
        divider.setBackground(new Color(255, 255, 255, 20));
        divider.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel statsRow = new JPanel(new GridLayout(1, 3, 10, 0));
        statsRow.setOpaque(false);
        statsRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        statsRow.setMaximumSize(new Dimension(380, 50));

        statsRow.add(createStatItem("240", "Produksi/hari", Theme.BLUE_ACCENT));
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
        rightPanel.setBackground(Theme.BG);

        JPanel formBox = new JPanel(new GridBagLayout());
        formBox.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        JLabel lblLogin = new JLabel("Masuk ke akun");
        lblLogin.setForeground(Theme.TEXT_PRIMARY);
        lblLogin.setFont(new Font("SansSerif", Font.BOLD, 28));
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 5, 0);
        formBox.add(lblLogin, gbc);

        JLabel lblWelcome = new JLabel("Selamat datang kembali di TofuBase.");
        lblWelcome.setForeground(Theme.TEXT_SECONDARY);
        lblWelcome.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 45, 0);
        formBox.add(lblWelcome, gbc);

        // Input Email
        JLabel lblEmail = new JLabel("EMAIL");
        lblEmail.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblEmail.setForeground(Theme.TEXT_SECONDARY);
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 8, 0);
        formBox.add(lblEmail, gbc);

        RoundedPanel emailWrapper = new RoundedPanel(10, Theme.CARD);
        emailWrapper.setLayout(new BorderLayout());
        emailWrapper.setPreferredSize(new Dimension(380, 48));
        emailWrapper.setBorder(new EmptyBorder(0, 15, 0, 15));

        lblIconMail = new JLabel("✉  ");
        lblIconMail.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblIconMail.setForeground(Theme.TEXT_SECONDARY);
        emailWrapper.add(lblIconMail, BorderLayout.WEST);

        txtEmail = new JTextField(EMAIL_PLACEHOLDER);
        txtEmail.setOpaque(false);
        txtEmail.setBorder(null);
        txtEmail.setCaretColor(Color.WHITE);
        txtEmail.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtEmail.setForeground(Theme.TEXT_SECONDARY);

        setupTextFieldFocus(txtEmail, EMAIL_PLACEHOLDER, lblIconMail);
        emailWrapper.add(txtEmail, BorderLayout.CENTER);

        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 20, 0);
        formBox.add(emailWrapper, gbc);

        // Input Password
        JLabel lblPassword = new JLabel("KATA SANDI");
        lblPassword.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblPassword.setForeground(Theme.TEXT_SECONDARY);
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 8, 0);
        formBox.add(lblPassword, gbc);

        RoundedPanel passWrapper = new RoundedPanel(10, Theme.CARD);
        passWrapper.setLayout(new BorderLayout());
        passWrapper.setPreferredSize(new Dimension(380, 48));
        passWrapper.setBorder(new EmptyBorder(0, 15, 0, 10));

        lblIconLock = new JLabel("🔒  ");
        lblIconLock.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblIconLock.setForeground(Theme.TEXT_SECONDARY);
        passWrapper.add(lblIconLock, BorderLayout.WEST);

        txtPass = new JPasswordField(PASS_PLACEHOLDER);
        txtPass.setOpaque(false);
        txtPass.setBorder(null);
        txtPass.setCaretColor(Color.WHITE);
        txtPass.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtPass.setForeground(Theme.TEXT_SECONDARY);
        txtPass.setEchoChar((char) 0);

        JLabel lblEye = new JLabel("👁");
        lblEye.setForeground(Theme.TEXT_SECONDARY);
        lblEye.setFont(new Font("SansSerif", Font.PLAIN, 16));
        lblEye.setCursor(new Cursor(Cursor.HAND_CURSOR));

        setupPasswordFieldFocusAndEye(lblEye);

        passWrapper.add(txtPass, BorderLayout.CENTER);
        passWrapper.add(lblEye, BorderLayout.EAST);

        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 15, 0);
        formBox.add(passWrapper, gbc);

        // Checkbox Remember Me
        JPanel optionsRow = new JPanel(new BorderLayout());
        optionsRow.setOpaque(false);

        chkRemember = new JCheckBox("Ingat saya");
        chkRemember.setOpaque(false);
        chkRemember.setForeground(Theme.TEXT_SECONDARY);
        chkRemember.setFont(new Font("SansSerif", Font.PLAIN, 13));
        chkRemember.setFocusPainted(false);
        chkRemember.setCursor(new Cursor(Cursor.HAND_CURSOR));
        optionsRow.add(chkRemember, BorderLayout.WEST);

        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 35, 0);
        formBox.add(optionsRow, gbc);

        // Tombol Login
        btnLogin = new RoundedPanel(10, Theme.BLUE_ACCENT);
        btnLogin.setLayout(new BorderLayout());
        btnLogin.setPreferredSize(new Dimension(380, 48));
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel lblBtnText = new JLabel("Masuk ke Akun", SwingConstants.CENTER);
        lblBtnText.setForeground(Color.WHITE);
        lblBtnText.setFont(new Font("SansSerif", Font.BOLD, 15));
        btnLogin.add(lblBtnText, BorderLayout.CENTER);

        gbc.gridy = 7;
        gbc.insets = new Insets(0, 0, 80, 0);
        formBox.add(btnLogin, gbc);

        // Footer
        JLabel lblFooter = new JLabel("© 2026 TofuBase — Sistem Manajemen Pabrik Tahu", SwingConstants.CENTER);
        lblFooter.setForeground(new Color(255, 255, 255, 60));
        lblFooter.setFont(new Font("SansSerif", Font.PLAIN, 11));

        gbc.gridy = 8;
        gbc.insets = new Insets(0, 0, 0, 0);
        formBox.add(lblFooter, gbc);

        rightPanel.add(formBox);
        return rightPanel;
    }

    private void setupTextFieldFocus(JTextField txtField, String placeholder, JLabel icon) {
        txtField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txtField.getText().equals(placeholder)) {
                    txtField.setText("");
                    txtField.setForeground(Theme.TEXT_PRIMARY);
                    icon.setForeground(Theme.BLUE_ACCENT);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (txtField.getText().isEmpty()) {
                    txtField.setText(placeholder);
                    txtField.setForeground(Theme.TEXT_SECONDARY);
                    icon.setForeground(Theme.TEXT_SECONDARY);
                }
            }
        });
    }

    private void setupPasswordFieldFocusAndEye(JLabel lblEye) {
        txtPass.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (String.valueOf(txtPass.getPassword()).equals(PASS_PLACEHOLDER)) {
                    txtPass.setText("");
                    txtPass.setForeground(Theme.TEXT_PRIMARY);
                    lblIconLock.setForeground(Theme.BLUE_ACCENT);
                    if (!isPasswordVisible) {
                        txtPass.setEchoChar('•');
                    }
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (String.valueOf(txtPass.getPassword()).isEmpty()) {
                    txtPass.setEchoChar((char) 0);
                    txtPass.setText(PASS_PLACEHOLDER);
                    txtPass.setForeground(Theme.TEXT_SECONDARY);
                    lblIconLock.setForeground(Theme.TEXT_SECONDARY);
                }
            }
        });

        lblEye.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                isPasswordVisible = !isPasswordVisible;
                if (isPasswordVisible) {
                    txtPass.setEchoChar((char) 0);
                    lblEye.setForeground(Theme.BLUE_ACCENT);
                } else {
                    if (!String.valueOf(txtPass.getPassword()).equals(PASS_PLACEHOLDER)) {
                        txtPass.setEchoChar('•');
                    }
                    lblEye.setForeground(Theme.TEXT_SECONDARY);
                }
            }
        });
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
        lblText.setForeground(Theme.TEXT_SECONDARY);
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
        lblTitle.setForeground(Theme.TEXT_SECONDARY);
        lblTitle.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(lblVal);
        panel.add(lblTitle);
        return panel;
    }

    public JTextField getTxtEmail() {
        return txtEmail;
    }

    public JPasswordField getTxtPass() {
        return txtPass;
    }

    public JCheckBox getChkRemember() {
        return chkRemember;
    }

    public RoundedPanel getBtnLogin() {
        return btnLogin;
    }

    public JLabel getLblIconMail() {
        return lblIconMail;
    }

    public JLabel getLblIconLock() {
        return lblIconLock;
    }
}
