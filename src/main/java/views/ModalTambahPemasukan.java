package views;

import models.Pemasukan;
import dao.PemasukanDAO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ModalTambahPemasukan extends JDialog {
    private JTextField txtSumber, txtJumlah, txtKeterangan;
    private JButton btnSimpan, btnBatal;
    private PemasukanDAO pemasukanDAO;
    private PemasukanPanel parentPanel;

    private final Color BG_DARK = new Color(30, 30, 30);
    private final Color CARD_DARK = new Color(43, 43, 43);
    private final Color TEXT_LIGHT = new Color(240, 240, 240);
    private final Color ACCENT_BLUE = new Color(13, 110, 253);

    public ModalTambahPemasukan(Frame parent, PemasukanPanel parentPanel) {
        super(parent, "Tambah Pemasukan Lainnya", true);
        this.parentPanel = parentPanel;
        this.pemasukanDAO = new PemasukanDAO();
        
        setUndecorated(true);
        initComponents();
    }

    private void initComponents() {
        setSize(480, 460);
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
        JLabel lblTitle = new JLabel("Tambah Pemasukan");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(TEXT_LIGHT);
        JLabel lblSub = new JLabel("Catat pemasukan kas luar transaksi penjualan");
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

        contentPanel.add(createLabelField("SUMBER PEMASUKAN *"));
        txtSumber = createCustomTextField("cth. Suntikan Modal, Penjualan Ampas Tahu...");
        contentPanel.add(txtSumber);
        contentPanel.add(Box.createVerticalStrut(15));

        contentPanel.add(createLabelField("NOMINAL JUMLAH (Rp) *"));
        txtJumlah = createCustomTextField("0");
        contentPanel.add(txtJumlah);
        contentPanel.add(Box.createVerticalStrut(15));

        contentPanel.add(createLabelField("KETERANGAN"));
        txtKeterangan = createCustomTextField("cth. Transfer Bank, Dana tunai...");
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

        btnSimpan = new JButton("💾 Simpan Pemasukan");
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
            String sumber = txtSumber.getText().trim();
            String jumlahStr = txtJumlah.getText().trim();
            String keterangan = txtKeterangan.getText().trim();

            if (sumber.isEmpty() || jumlahStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Kolom bertanda * wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                double jumlah = Double.parseDouble(jumlahStr);
                Pemasukan p = new Pemasukan(0, 0, new java.util.Date(), sumber, jumlah, keterangan);
                
                if (pemasukanDAO.insertPemasukan(p)) {
                    JOptionPane.showMessageDialog(this, "Pemasukan berhasil dicatat!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal menyimpan data pemasukan.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Nominal jumlah harus berupa angka!", "Input Salah", JOptionPane.ERROR_MESSAGE);
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