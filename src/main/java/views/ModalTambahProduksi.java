package views;

import models.Produk;
import dao.ProduksiDAO;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class ModalTambahProduksi extends JDialog {
    private JComboBox<String> cbProduk, cbStatus;
    private JTextField txtBatch, txtHasil, txtKeterangan;
    private JButton btnSimpan, btnBatal;
    private ProduksiDAO produksiDAO;
    private ProduksiPanel parentPanel;
    private Map<String, Integer> mapProduk = new HashMap<>();

    private final Color BG_DARK = new Color(30, 30, 30);
    private final Color CARD_DARK = new Color(43, 43, 43);
    private final Color TEXT_LIGHT = new Color(240, 240, 240);
    private final Color ACCENT_BLUE = new Color(13, 110, 253);

    public ModalTambahProduksi(Frame parent, ProduksiPanel parentPanel) {
        super(parent, "Tambah Prosedur Produksi", true);
        this.parentPanel = parentPanel;
        this.produksiDAO = new ProduksiDAO();
        setUndecorated(true);
        initComponents();
        loadComboProduk();
    }

    private void initComponents() {
        setSize(480, 580);
        setLocationRelativeTo(getOwner());
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_DARK);
        mainPanel.setBorder(BorderFactory.createLineBorder(CARD_DARK, 2));
        setContentPane(mainPanel);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BG_DARK);
        headerPanel.setBorder(new EmptyBorder(20, 24, 15, 24));
        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 2, 2));
        titlePanel.setBackground(BG_DARK);
        JLabel lblTitle = new JLabel("Mulai Batch Produksi");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(TEXT_LIGHT);
        JLabel lblSub = new JLabel("Catat aktivitas pembuatan produk tahu baru");
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

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BG_DARK);
        contentPanel.setBorder(new EmptyBorder(10, 24, 20, 24));

        contentPanel.add(createLabelField("KODE BATCH *"));
        txtBatch = createCustomTextField("");
        contentPanel.add(txtBatch);
        contentPanel.add(Box.createVerticalStrut(12));

        contentPanel.add(createLabelField("PRODUK YANG DIBUAT *"));
        cbProduk = new JComboBox<>();
        styleComboBox(cbProduk);
        contentPanel.add(cbProduk);
        contentPanel.add(Box.createVerticalStrut(12));

        contentPanel.add(createLabelField("HASIL PRODUKSI (QTY) *"));
        txtHasil = createCustomTextField("0");
        contentPanel.add(txtHasil);
        contentPanel.add(Box.createVerticalStrut(12));

        contentPanel.add(createLabelField("STATUS *"));
        cbStatus = new JComboBox<>(new String[]{"Proses", "Selesai"});
        styleComboBox(cbStatus);
        contentPanel.add(cbStatus);
        contentPanel.add(Box.createVerticalStrut(12));

        contentPanel.add(createLabelField("KETERANGAN"));
        txtKeterangan = createCustomTextField("");
        contentPanel.add(txtKeterangan);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

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

        btnSimpan = new JButton("💾 Simpan Produksi");
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
            String batch = txtBatch.getText().trim();
            String prodSelected = (String) cbProduk.getSelectedItem();
            String hasilStr = txtHasil.getText().trim();
            String status = (String) cbStatus.getSelectedItem();
            String ket = txtKeterangan.getText().trim();

            if (batch.isEmpty() || prodSelected == null || hasilStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Kolom bertanda * wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                double hasil = Double.parseDouble(hasilStr);
                int idProduk = mapProduk.get(prodSelected);
                if (produksiDAO.insertProduksi(idProduk, batch, new java.util.Date(), hasil, status, ket, 3)) {
                    JOptionPane.showMessageDialog(this, "Data Produksi berhasil disimpan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal menyimpan data produksi.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Kuantitas hasil produksi harus berupa angka!", "Input Salah", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void loadComboProduk() {
        try {
            cbProduk.removeAllItems();
            java.util.List<Produk> list = produksiDAO.getProdukList();
            
            if (list != null && !list.isEmpty()) {
                for (Produk p : list) {
                    if (p.getNama() != null) {
                        cbProduk.addItem(p.getNama());
                        mapProduk.put(p.getNama(), p.getIdProduk());
                    }
                }
            } else {
                cbProduk.addItem("Tidak ada data produk");
                JOptionPane.showMessageDialog(this, 
                    "Sistem membaca 0 data dari tabel produk. Pastikan database terkoneksi dengan benar.", 
                    "Data Kosong", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            cbProduk.addItem("Gagal memuat data");
        }
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

    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setMaximumSize(new Dimension(Short.MAX_VALUE, 40));
        comboBox.setBackground(CARD_DARK);
        comboBox.setForeground(TEXT_LIGHT);
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
    }
}