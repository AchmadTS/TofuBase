package views;

import models.Penjualan;
import models.RecordPenjualan;
import dao.PenjualanDAO;
import utils.DatabaseConfig;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModalTambahPenjualan extends JDialog {
    private JComboBox<String> cbPelanggan, cbProduk;
    private JTextField txtJumlah, txtHarga, txtKeterangan;
    private JLabel lblTotalBelanja;
    private JButton btnTambahItem, btnSimpan, btnBatal;
    private JTable tableItem;
    private DefaultTableModel tableModel;
    
    private PenjualanDAO penjualanDAO;
    private PenjualanPanel parentPanel;
    private List<RecordPenjualan> keranjang;
    private double totalBelanja = 0;

    private Map<String, Integer> mapPelanggan = new HashMap<>();
    private Map<String, Integer> mapProduk = new HashMap<>();
    private Map<Integer, Double> hargaProduk = new HashMap<>();

    private final Color BG_DARK = new Color(30, 30, 30);
    private final Color CARD_DARK = new Color(43, 43, 43);
    private final Color TEXT_LIGHT = new Color(240, 240, 240);
    private final Color ACCENT_BLUE = new Color(13, 110, 253);

    public ModalTambahPenjualan(Frame parent, PenjualanPanel parentPanel) {
        super(parent, "Tambah Penjualan", true);
        this.parentPanel = parentPanel;
        this.penjualanDAO = new PenjualanDAO();
        this.keranjang = new ArrayList<>();
        
        setUndecorated(true);
        initComponents();
        loadComboPelanggan();
        loadComboProduk();
    }

    private void initComponents() {
        setSize(600, 650);
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
        JLabel lblTitle = new JLabel("Tambah Transaksi Penjualan");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(TEXT_LIGHT);
        JLabel lblSub = new JLabel("Catat penjualan produk tahu ke pelanggan");
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
        contentPanel.setBorder(new EmptyBorder(10, 24, 10, 24));

        contentPanel.add(createLabelField("PILIH PELANGGAN *"));
        cbPelanggan = new JComboBox<>();
        styleComboBox(cbPelanggan);
        contentPanel.add(cbPelanggan);
        contentPanel.add(Box.createVerticalStrut(10));

        JPanel itemInputPanel = new JPanel(new GridLayout(2, 3, 10, 5));
        itemInputPanel.setBackground(BG_DARK);
        
        itemInputPanel.add(createLabelField("PRODUK *"));
        itemInputPanel.add(createLabelField("HARGA SATUAN"));
        itemInputPanel.add(createLabelField("QTY *"));

        cbProduk = new JComboBox<>();
        styleComboBox(cbProduk);
        cbProduk.addActionListener(e -> {
            String selected = (String) cbProduk.getSelectedItem();
            if (selected != null && mapProduk.containsKey(selected)) {
                int idPr = mapProduk.get(selected);
                txtHarga.setText(String.valueOf(hargaProduk.getOrDefault(idPr, 0.0).intValue()));
            }
        });
        
        txtHarga = createCustomTextField("");
        txtHarga.setEditable(false);
        txtJumlah = createCustomTextField("0");
        
        itemInputPanel.add(cbProduk);
        itemInputPanel.add(txtHarga);
        itemInputPanel.add(txtJumlah);
        contentPanel.add(itemInputPanel);
        contentPanel.add(Box.createVerticalStrut(10));

        btnTambahItem = new JButton("+ Tambah ke Keranjang");
        btnTambahItem.setBackground(CARD_DARK);
        btnTambahItem.setForeground(TEXT_LIGHT);
        btnTambahItem.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnTambahItem.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTambahItem.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(btnTambahItem);
        contentPanel.add(Box.createVerticalStrut(15));

        tableModel = new DefaultTableModel(new String[]{"Produk", "Harga", "Jumlah", "Subtotal"}, 0);
        tableItem = new JTable(tableModel);
        tableItem.setBackground(CARD_DARK);
        tableItem.setForeground(TEXT_LIGHT);
        tableItem.setGridColor(BG_DARK);
        JScrollPane scrollPane = new JScrollPane(tableItem);
        scrollPane.setPreferredSize(new Dimension(550, 150));
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(scrollPane);
        contentPanel.add(Box.createVerticalStrut(10));

        contentPanel.add(createLabelField("KETERANGAN"));
        txtKeterangan = createCustomTextField("cth. Lunas, Kirim besok pagi");
        contentPanel.add(txtKeterangan);
        contentPanel.add(Box.createVerticalStrut(10));

        lblTotalBelanja = new JLabel("TOTAL BAYAR: Rp 0");
        lblTotalBelanja.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTotalBelanja.setForeground(Color.ORANGE);
        lblTotalBelanja.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(lblTotalBelanja);

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

        btnSimpan = new JButton("💾 Simpan Transaksi");
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

        btnTambahItem.addActionListener(e -> {
            String prodSelected = (String) cbProduk.getSelectedItem();
            String qtyStr = txtJumlah.getText().trim();
            if (prodSelected == null || qtyStr.isEmpty() || qtyStr.equals("0")) {
                return;
            }
            try {
                int idPr = mapProduk.get(prodSelected);
                double harga = hargaProduk.get(idPr);
                double qty = Double.parseDouble(qtyStr);
                double sub = harga * qty;
                
                RecordPenjualan item = new RecordPenjualan(0, idPr, qty, harga, sub, prodSelected, "");
                keranjang.add(item);
                
                tableModel.addRow(new Object[]{prodSelected, (int)harga, (int)qty, (int)sub});
                totalBelanja += sub;
                lblTotalBelanja.setText("TOTAL BAYAR: Rp " + utils.FormatUtil.formatAngka(totalBelanja));
                txtJumlah.setText("0");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        btnSimpan.addActionListener(e -> {
            String pelSelected = (String) cbPelanggan.getSelectedItem();
            if (pelSelected == null || keranjang.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Pelanggan atau keranjang belanja tidak boleh kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int idPel = mapPelanggan.get(pelSelected);
            String ket = txtKeterangan.getText().trim();
            
            Penjualan p = new Penjualan(0, idPel, new java.util.Date(), totalBelanja, ket, pelSelected);
            if (penjualanDAO.insertPenjualan(p, keranjang)) {
                JOptionPane.showMessageDialog(this, "Transaksi Penjualan berhasil disimpan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan transaksi.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void loadComboPelanggan() {
        String sql = "SELECT id_pelanggan, nama FROM pelanggan ORDER BY nama ASC";
        try (Connection conn = DatabaseConfig.getKoneksi(); PreparedStatement ps = conn.prepareStatement(sql); java.sql.ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String nama = rs.getString("nama");
                int id = rs.getInt("id_pelanggan");
                cbPelanggan.addItem(nama);
                mapPelanggan.put(nama, id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadComboProduk() {
        String sql = "SELECT id_produk, nama, harga_jual FROM produk ORDER BY nama ASC";
        try (Connection conn = DatabaseConfig.getKoneksi(); PreparedStatement ps = conn.prepareStatement(sql); java.sql.ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String nama = rs.getString("nama");
                int id = rs.getInt("id_produk");
                double harga = rs.getDouble("harga_jual");
                cbProduk.addItem(nama);
                mapProduk.put(nama, id);
                hargaProduk.put(id, harga);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        textField.setMaximumSize(new Dimension(Short.MAX_VALUE, 35));
        textField.setBackground(CARD_DARK);
        textField.setForeground(TEXT_LIGHT);
        textField.setCaretColor(TEXT_LIGHT);
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        textField.setAlignmentX(Component.LEFT_ALIGNMENT);
        textField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(70, 70, 70), 1), new EmptyBorder(5, 10, 5, 10)));
        return textField;
    }

    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setMaximumSize(new Dimension(Short.MAX_VALUE, 35));
        comboBox.setBackground(CARD_DARK);
        comboBox.setForeground(TEXT_LIGHT);
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
    }
}