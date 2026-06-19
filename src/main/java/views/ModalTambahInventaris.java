package views;

import dao.InventarisDAO;
import utils.Theme;
import components.RoundedPanel;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class ModalTambahInventaris extends JDialog {
    private JTextArea txtKeterangan;
    private JButton btnSimpan;
    private JButton btnBatal;
    private InventarisPanel parentPanel;

    public ModalTambahInventaris(Frame parent, InventarisPanel parentPanel) {
        super(parent, "Tambah Inventaris", true);
        this.parentPanel = parentPanel;
        
        // Setup Window Dialog (Frameless & Transparan agar aesthetic)
        setUndecorated(true);
        setSize(480, 360);
        setLocationRelativeTo(parent);
        setBackground(new Color(0, 0, 0, 0)); // Membuat background window transparan

        // Main Container Utama (Rounded Gelap)
        RoundedPanel mainPanel = new RoundedPanel(20, new Color(30, 30, 30));
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        // ==================== 1. HEADER SECTION ====================
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        // Kiri: Ikon + Judul
        JPanel titleGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        titleGroup.setOpaque(false);

        // Box Ikon Biru Kustom (+)
        RoundedPanel iconBox = new RoundedPanel(10, Theme.BLUE_ACCENT);
        iconBox.setPreferredSize(new Dimension(36, 36));
        iconBox.setLayout(new GridBagLayout());
        JLabel lblPlus = new JLabel("+");
        lblPlus.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblPlus.setForeground(Color.WHITE);
        iconBox.add(lblPlus);

        // Teks Judul & Subtitle
        JPanel textGroup = new JPanel(new GridLayout(2, 1, 0, 2));
        textGroup.setOpaque(false);
        JLabel lblTitle = new JLabel("Tambah Aktivitas Inventaris");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblTitle.setForeground(Color.WHITE);
        JLabel lblSubtitle = new JLabel("Tambahkan catatan atau riwayat pengecekan pabrik baru");
        lblSubtitle.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblSubtitle.setForeground(Theme.TEXT_SECONDARY);
        
        textGroup.add(lblTitle);
        textGroup.add(lblSubtitle);
        titleGroup.add(iconBox);
        titleGroup.add(textGroup);

        // Kanan: Tombol Close (X) Kustom
        RoundedPanel closeBox = new RoundedPanel(8, new Color(45, 45, 45));
        closeBox.setPreferredSize(new Dimension(30, 30));
        closeBox.setLayout(new GridBagLayout());
        closeBox.setCursor(new Cursor(Cursor.HAND_CURSOR));
        JLabel lblClose = new JLabel("x");
        lblClose.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblClose.setForeground(Theme.TEXT_SECONDARY);
        closeBox.add(lblClose);
        closeBox.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) { dispose(); }
        });

        headerPanel.add(titleGroup, BorderLayout.WEST);
        headerPanel.add(closeBox, BorderLayout.EAST);

        // ==================== 2. CONTENT SECTION (INPUT FORM) ====================
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(25, 0, 20, 0));

        JLabel lblKeterangan = new JLabel("KETERANGAN AKTIVITAS *");
        lblKeterangan.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblKeterangan.setForeground(Theme.TEXT_SECONDARY);
        lblKeterangan.setAlignmentX(Component.LEFT_ALIGNMENT);

        // JTextArea Input Kustom dengan Background Gelap
        txtKeterangan = new JTextArea("cth. Pengecekan mesin cetak tahu berkala, restock kedelai...");
        txtKeterangan.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txtKeterangan.setBackground(new Color(40, 40, 40));
        txtKeterangan.setForeground(Theme.TEXT_SECONDARY);
        txtKeterangan.setCaretColor(Color.WHITE);
        txtKeterangan.setLineWrap(true);
        txtKeterangan.setWrapStyleWord(true);
        txtKeterangan.setBorder(new EmptyBorder(10, 12, 10, 12));

        // Efek Placeholder saat Focus
        txtKeterangan.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txtKeterangan.getText().startsWith("cth.")) {
                    txtKeterangan.setText("");
                    txtKeterangan.setForeground(Color.WHITE);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (txtKeterangan.getText().trim().isEmpty()) {
                    txtKeterangan.setText("cth. Pengecekan mesin cetak tahu berkala, restock kedelai...");
                    txtKeterangan.setForeground(Theme.TEXT_SECONDARY);
                }
            }
        });

        // Bungkus JTextArea ke dalam JScrollPane agar rapi
        JScrollPane scrollPane = new JScrollPane(txtKeterangan);
        scrollPane.setBorder(null);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPane.setPreferredSize(new Dimension(0, 120));
        scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        contentPanel.add(lblKeterangan);
        contentPanel.add(Box.createVerticalStrut(8));
        contentPanel.add(scrollPane);

        // Divider Line (Garis Pembatas Horizontal Mewah)
        JPanel divider = new JPanel();
        divider.setBackground(new Color(45, 45, 45));
        divider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        divider.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(divider);

        // ==================== 3. FOOTER SECTION (BUTTONS) ====================
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        footerPanel.setOpaque(false);

        // Tombol Batal Kustom
        btnBatal = new JButton("Batal");
        btnBatal.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnBatal.setForeground(Color.WHITE);
        btnBatal.setBackground(new Color(45, 45, 45));
        btnBatal.setBorder(new EmptyBorder(10, 24, 10, 24));
        btnBatal.setFocusPainted(false);
        btnBatal.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Tombol Simpan Kustom (Biru Modern)
        btnSimpan = new JButton("💾 Simpan Catatan");
        btnSimpan.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnSimpan.setForeground(Color.WHITE);
        btnSimpan.setBackground(Theme.BLUE_ACCENT);
        btnSimpan.setBorder(new EmptyBorder(10, 20, 10, 20));
        btnSimpan.setFocusPainted(false);
        btnSimpan.setCursor(new Cursor(Cursor.HAND_CURSOR));

        footerPanel.add(btnBatal);
        footerPanel.add(btnSimpan);

        // Merakit Semuanya
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        add(mainPanel);

        // ==================== ACTION LISTENERS ====================
        btnBatal.addActionListener(e -> dispose());
        
        btnSimpan.addActionListener(e -> {
            String keterangan = txtKeterangan.getText().trim();
            if (keterangan.isEmpty() || keterangan.startsWith("cth.")) {
                JOptionPane.showMessageDialog(this, "Keterangan tidak boleh kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            boolean sukses = new InventarisDAO().insertInventaris(keterangan);
            if (sukses) {
                JOptionPane.showMessageDialog(this, "Data inventaris berhasil disimpan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                if (parentPanel != null) {
                    parentPanel.refreshData();
                }
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan data ke database.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}