package views;

import models.Produk;
import dao.ProdukDAO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ModalTambahProduk extends JDialog {
    private JTextField txtNama, txtJenis, txtSatuan, txtHargaJual, txtStok;
    private JButton btnSimpan, btnBatal;
    private ProdukDAO produkDAO;
    private ProdukPanel parentPanel;

    private final Color BG_DARK = new Color(30, 30, 30);
    private final Color CARD_DARK = new Color(43, 43, 43);
    private final Color TEXT_LIGHT = new Color(240, 240, 240);
    private final Color ACCENT_BLUE = new Color(13, 110, 253);

    public ModalTambahProduk(Frame parent, ProdukPanel parentPanel) {
        super(parent, "Tambah Produk", true);
        this.parentPanel = parentPanel;
        this.produkDAO = new ProdukDAO();
        
        setUndecorated(true);
        initComponents();
    }

    private void initComponents() {
        setSize(480, 560);
        setLocationRelativeTo(getOwner());
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_DARK);
        mainPanel.setBorder(BorderFactory.createLineBorder(CARD_DARK, 2));
        setContentPane(mainPanel);

        // HEADER
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BG_DARK);
        headerPanel.setBorder(new EmptyBorder(20, 24, 15, 24));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 2, 2));
        titlePanel.setBackground(BG_DARK);
        JLabel lblTitle = new JLabel("Tambah Produk");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(TEXT_LIGHT);
        JLabel lblSub = new JLabel("Tambahkan varian produk tahu baru ke inventaris");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(Color.GRAY);
        titlePanel.add(lblTitle);
        titlePanel.add(lblSub);
        headerPanel.add(titlePanel, BorderLayout.WEST);

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

        // CONTENT
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BG_DARK);
        contentPanel.setBorder(new EmptyBorder(10, 24, 20, 24));

        contentPanel.add(createLabelField("NAMA PRODUK *"));
        txtNama = createCustomTextField("cth. Tahu Sutera Premium, Tofu Goreng...");
        contentPanel.add(txtNama);
        contentPanel.add(Box.createVerticalStrut(12));

        contentPanel.add(createLabelField("JENIS / KATEGORI"));
        txtJenis = createCustomTextField("cth. Tahu Basah, Tahu Kering");
        contentPanel.add(txtJenis);
        contentPanel.add(Box.createVerticalStrut(12));

        contentPanel.add(createLabelField("SATUAN *"));
        txtSatuan = createCustomTextField("cth. pcs, bungkus, box");
        contentPanel.add(txtSatuan);
        contentPanel.add(Box.createVerticalStrut(12));

        contentPanel.add(createLabelField("HARGA JUAL (Rp) *"));
        txtHargaJual = createCustomTextField("cth. 15000");
        contentPanel.add(txtHargaJual);
        contentPanel.add(Box.createVerticalStrut(12));

        contentPanel.add(createLabelField("STOK AWAL *"));
        txtStok = createCustomTextField("0");
        contentPanel.add(txtStok);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // FOOTER
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        footerPanel.setBackground(BG_DARK);
        footerPanel.setBorder(new EmptyBorder(15, 24, 24, 24));

        btnBatal = new JButton("Batal");
        btnBatal.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnBatal.setForeground(TEXT_LIGHT);
        btnBatal.setBackground(CARD_DARK);
        btnBatal.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY, 1), new EmptyBorder(8, 20, 8, 20)));
        btnBatal.setContentAreaFilled(false);
        btnBatal.setOpaque(true);
        btnBatal.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBatal.addActionListener(e -> dispose());

        btnSimpan = new JButton("💾 Simpan Produk");
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

        btnSimpan.addActionListener(e -> {
            String nama = txtNama.getText().trim();
            String jenis = txtJenis.getText().trim();
            String satuan = txtSatuan.getText().trim();
            String hargaStr = txtHargaJual.getText().trim();
            String stokStr = txtStok.getText().trim();

            if (nama.isEmpty() || satuan.isEmpty() || hargaStr.isEmpty() || stokStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Kolom bertanda * wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                double hargaJual = Double.parseDouble(hargaStr);
                double stok = Double.parseDouble(stokStr);

                Produk produkBaru = new Produk(0, nama, satuan, hargaJual, jenis, stok);
                
                if (produkDAO.insertProduk(produkBaru)) {
                    JOptionPane.showMessageDialog(this, "Produk baru berhasil disimpan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal menyimpan data produk.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Harga dan Stok harus berupa angka!", "Input Salah", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private JLabel createLabelField(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 11));
        label.setForeground(Color.LIGHT_GRAY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JTextField createCustomTextField(String placeholder) {
        JTextField textField = new JTextField();
        textField.setMaximumSize(new Dimension(Short.MAX_VALUE, 40));
        textField.setBackground(CARD_DARK);
        textField.setForeground(TEXT_LIGHT);
        textField.setCaretColor(TEXT_LIGHT);
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        textField.setAlignmentX(Component.LEFT_ALIGNMENT);
        textField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(70, 70, 70), 1), new EmptyBorder(8, 12, 8, 12)));
        return textField;
    }
}