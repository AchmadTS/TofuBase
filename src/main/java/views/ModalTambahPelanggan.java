package views;

import models.Pelanggan;
import dao.PelangganDAO;
import utils.Theme; // Mengambil warna tema default aplikasi Anda

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ModalTambahPelanggan extends JDialog {
    private JTextField txtNama, txtAlamat, txtNoTelp, txtEmail;
    private JButton btnSimpan, btnBatal;
    private PelangganDAO pelangganDAO;
    private PelangganPanel parentPanel;

    // Definisikan warna dark mode agar mirip dengan gambar referensi
    private final Color BG_DARK = new Color(30, 30, 30);
    private final Color CARD_DARK = new Color(43, 43, 43);
    private final Color TEXT_LIGHT = new Color(240, 240, 240);
    private final Color ACCENT_BLUE = new Color(13, 110, 253);

    public ModalTambahPelanggan(Frame parent, PelangganPanel parentPanel) {
        super(parent, "Tambah Pelanggan", true);
        this.parentPanel = parentPanel;
        this.pelangganDAO = new PelangganDAO();
        
        setUndecorated(true); // Menghilangkan border putih bawaan Windows agar estetik
        initComponents();
    }

    private void initComponents() {
        setSize(480, 500);
        setLocationRelativeTo(getOwner());
        
        // Main Panel dengan background gelap
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(BG_DARK);
        mainPanel.setBorder(BorderFactory.createLineBorder(CARD_DARK, 2));
        setContentPane(mainPanel);

        // ================= HEADER PANEL =================
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BG_DARK);
        headerPanel.setBorder(new EmptyBorder(20, 24, 15, 24));

        // Judul & Subjudul kiri
        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 2, 2));
        titlePanel.setBackground(BG_DARK);
        
        JLabel lblTitle = new JLabel("Tambah Pelanggan");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(TEXT_LIGHT);
        
        JLabel lblSub = new JLabel("Tambahkan data pelanggan baru ke sistem");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(Color.GRAY);
        
        titlePanel.add(lblTitle);
        titlePanel.add(lblSub);
        headerPanel.add(titlePanel, BorderLayout.WEST);

        // Tombol Silang (X) Kanan Atas
        JButton btnClose = new JButton("X");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnClose.setForeground(Color.GRAY);
        btnClose.setContentAreaFilled(false);
        btnClose.setBorderPainted(false);
        btnClose.setFocusPainted(false);
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> dispose());
        headerPanel.add(btnClose, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // ================= CONTENT PANEL (FORM INPUT) =================
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BG_DARK);
        contentPanel.setBorder(new EmptyBorder(10, 24, 20, 24));

        // Input 1: Nama Pelanggan
        contentPanel.add(createLabelField("NAMA PELANGGAN *"));
        txtNama = createCustomTextField("cth. PT. Tahu Sejahtera, Budi...");
        contentPanel.add(txtNama);
        contentPanel.add(Box.createVerticalStrut(15));

        // Input 2: Alamat
        contentPanel.add(createLabelField("ALAMAT *"));
        txtAlamat = createCustomTextField("cth. Jl. Raya No. 123, Bandung");
        contentPanel.add(txtAlamat);
        contentPanel.add(Box.createVerticalStrut(15));

        // Input 3: No Telepon
        contentPanel.add(createLabelField("NO. TELEPON *"));
        txtNoTelp = createCustomTextField("cth. 081234567890");
        contentPanel.add(txtNoTelp);
        contentPanel.add(Box.createVerticalStrut(15));

        // Input 4: Email
        contentPanel.add(createLabelField("EMAIL"));
        txtEmail = createCustomTextField("cth. pelanggan@gmail.com");
        contentPanel.add(txtEmail);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // ================= BUTTON PANEL FOOTER =================
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        footerPanel.setBackground(BG_DARK);
        footerPanel.setBorder(new EmptyBorder(15, 24, 24, 24));

        // Tombol Batal
        btnBatal = new JButton("Batal");
        btnBatal.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnBatal.setForeground(TEXT_LIGHT);
        btnBatal.setBackground(CARD_DARK);
        btnBatal.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            new EmptyBorder(8, 20, 8, 20)
        ));
        btnBatal.setContentAreaFilled(false);
        btnBatal.setOpaque(true);
        btnBatal.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBatal.addActionListener(e -> dispose());

        // Tombol Simpan
        btnSimpan = new JButton("💾 Simpan Pelanggan");
        btnSimpan.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSimpan.setForeground(TEXT_LIGHT);
        btnSimpan.setBackground(ACCENT_BLUE);
        btnSimpan.setBorder(new EmptyBorder(10, 20, 10, 20));
        btnSimpan.setContentAreaFilled(false);
        btnSimpan.setOpaque(true);
        btnSimpan.setCursor(new Cursor(Cursor.HAND_CURSOR));

        footerPanel.add(btnBatal);
        footerPanel.add(btnSimpan);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        // Logic Aksi Simpan
        btnSimpan.addActionListener(e -> {
            String nama = txtNama.getText().trim();
            String alamat = txtAlamat.getText().trim();
            String noTelp = txtNoTelp.getText().trim();
            String email = txtEmail.getText().trim();

            if (nama.isEmpty() || alamat.isEmpty() || noTelp.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Kolom bertanda * wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Pelanggan pelangganBaru = new Pelanggan(0, nama, alamat, noTelp, email);
            
            if (pelangganDAO.insertPelanggan(pelangganBaru)) {
                JOptionPane.showMessageDialog(this, "Data Pelanggan berhasil disimpan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan data.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    // Helper untuk membuat Label kecil berwarna abu di atas TextField
    private JLabel createLabelField(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 11));
        label.setForeground(Color.LIGHT_GRAY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    // Helper untuk membuat TextField kustom berlatar gelap
    private JTextField createCustomTextField(String placeholder) {
        JTextField textField = new JTextField();
        textField.setMaximumSize(new Dimension(Short.MAX_VALUE, 40));
        textField.setBackground(CARD_DARK);
        textField.setForeground(TEXT_LIGHT);
        textField.setCaretColor(TEXT_LIGHT);
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        textField.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Memberikan padding dalam teks agar tidak nempel garis pinggir
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 70, 70), 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        
        return textField;
    }
}