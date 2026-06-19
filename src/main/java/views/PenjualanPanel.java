package views;

import components.ActivityTable;
import components.ModernScrollBarUI;
import components.RoundedPanel;
import dao.PenjualanDAO;
import utils.FormatUtil;
import utils.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.io.File;
import service.NotaPenjualanService;

public class PenjualanPanel extends JPanel {

    private static final String TITLE = "Penjualan";
    private static final String BTN_EXPORT = "Export PDF";
    private static final String BTN_ADD = "+ Tambah Penjualan";
    private static final int SCROLL_SPEED = 16;
    private static final int TIMER_DELAY_MS = 60_000;

    private final PenjualanDAO penjualanDAO = new PenjualanDAO();
    private JLabel lblTotalPenjualan;
    private JLabel lblProdukTerjual;
    private JLabel lblPelanggan;
    private JLabel lblOmzet;
    private ActivityTable tablePenjualan;

    public PenjualanPanel(String userName, String userRole) {
        setLayout(new BorderLayout());
        setBackground(Theme.BG);
        initializeDynamicComponents();
        add(buildMainContent(), BorderLayout.CENTER);
        setupListeners();
        refreshData();
    }

    private void initializeDynamicComponents() {
        lblTotalPenjualan = createAnimatedLabel();
        lblProdukTerjual = createAnimatedLabel();
        lblPelanggan = createAnimatedLabel();
        lblOmzet = createAnimatedLabel();
    }

    private void setupListeners() {
        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent evt) {
                refreshData();
            }
        });
    }

    private JPanel buildMainContent() {
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(Theme.BG);
        mainContent.add(buildHeader(), BorderLayout.NORTH);

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(Theme.BG);
        container.setBorder(new EmptyBorder(10, 30, 30, 30));

        container.add(buildTopCards());
        container.add(Box.createVerticalStrut(20));
        container.add(buildPenjualanTable());

        JScrollPane scrollPane = new JScrollPane(container);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Theme.BG);
        scrollPane.getVerticalScrollBar().setUnitIncrement(SCROLL_SPEED);
        scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        scrollPane.getHorizontalScrollBar().setUI(new ModernScrollBarUI());
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mainContent.add(scrollPane, BorderLayout.CENTER);
        return mainContent;
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.BG);
        header.setBorder(new EmptyBorder(20, 30, 10, 30));

        header.add(buildHeaderTitlePanel(), BorderLayout.WEST);
        header.add(buildHeaderActionPanel(), BorderLayout.EAST);
        return header;
    }

    private JPanel buildHeaderTitlePanel() {
        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setBackground(Theme.BG);
        JLabel headerTitle = new JLabel(TITLE);
        headerTitle.setFont(new Font("SansSerif", Font.BOLD, 28));
        headerTitle.setForeground(Theme.TEXT_PRIMARY);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", Locale.forLanguageTag("id-ID"));
        JLabel headerDate = new JLabel(LocalDate.now().format(formatter));
        headerDate.setFont(new Font("SansSerif", Font.PLAIN, 14));
        headerDate.setForeground(Theme.TEXT_SECONDARY);
        new Timer(TIMER_DELAY_MS, e -> headerDate.setText(LocalDate.now().format(formatter))).start();

        titlePanel.add(headerTitle);
        titlePanel.add(headerDate);
        return titlePanel;
    }

    private JPanel buildHeaderActionPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Theme.BG);
        buttonPanel.add(createHeaderButton(BTN_EXPORT, false, this::handleExportPDF));
        buttonPanel.add(createHeaderButton(BTN_ADD, true, this::handleAddPenjualan));
        return buttonPanel;
    }

    private JPanel buildTopCards() {
        JPanel topCards = new JPanel(new GridLayout(1, 4, 20, 0));
        topCards.setBackground(Theme.BG);
        topCards.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        topCards.add(createStatCard("TOTAL PENJUALAN", lblTotalPenjualan, "Transaksi bulan ini", Theme.BLUE_ACCENT));
        topCards.add(createStatCard("PRODUK TERJUAL", lblProdukTerjual, "Unit terjual", Theme.GREEN));
        topCards.add(createStatCard("PELANGGAN", lblPelanggan, "Pelanggan aktif", Theme.WARNING));
        topCards.add(createStatCard("OMZET", lblOmzet, "Pendapatan bulan ini", Theme.TEXT_SECONDARY));
        return topCards;
    }

    private ActivityTable buildPenjualanTable() {
        String[] headers = {"ID", "Tanggal", "Pelanggan", "Total", "Produk Terjual", "Keterangan", "Aksi"};
        tablePenjualan = new ActivityTable("Daftar Penjualan", headers, -1, new ActivityTable.DataProvider() {
            @Override
            public int getTotalRowCount(String keyword) {
                return penjualanDAO.getTableTotalRows(keyword);
            }

            @Override
            public List<String[]> getPageData(int limit, int offset, String keyword) {
                return penjualanDAO.getTablePageData(limit, offset, keyword);
            }
        });

        tablePenjualan.setTableActionListener((id, name) -> handleViewDetail(id, name));
        return tablePenjualan;
    }

    private void handleViewDetail(String id, String name) {
        try {
            int penjualanId = Integer.parseInt(id);
            Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
            ModalRiwayatPenjualan modal = new ModalRiwayatPenjualan(parent, penjualanId);
            modal.setVisible(true);
        } catch (NumberFormatException ignored) {
        }
    }

    private void handleExportPDF() {
        String selectedIdStr = tablePenjualan.getLastSelectedId();
        
        if (selectedIdStr == null || selectedIdStr.trim().isEmpty() || selectedIdStr.equals("-")) {
            JOptionPane.showMessageDialog(this, "Silakan klik atau pilih salah satu baris transaksi penjualan dari tabel terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idPenjualan = Integer.parseInt(selectedIdStr);

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Simpan Nota Penjualan");
        fileChooser.setSelectedFile(new File("Nota_Penjualan_#" + idPenjualan + ".html"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String path = fileChooser.getSelectedFile().getAbsolutePath();
            if (!path.toLowerCase().endsWith(".html")) {
                path += ".html";
            }

            NotaPenjualanService notaService = new NotaPenjualanService();
            boolean sukses = notaService.exportNota(idPenjualan, path);

            if (sukses) {
                JOptionPane.showMessageDialog(this, "Nota Penjualan #" + idPenjualan + " berhasil diekspor!\nLokasi: " + path, "Sukses", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Gagal mencetak nota penjualan.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleAddPenjualan() {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        ModalTambahPenjualan modal = new ModalTambahPenjualan(topFrame, this);
        modal.setVisible(true);
        refreshData(); 
    }

    private void refreshData() {
        fetchTopCardsData();
        if (tablePenjualan != null) {
            tablePenjualan.updateTableModel();
        }
    }

    private void fetchTopCardsData() {
        new SwingWorker<Map<String, String>, Void>() {
            @Override
            protected Map<String, String> doInBackground() {
                return penjualanDAO.getTopCardsData();
            }

            @Override
            protected void done() {
                try {
                    Map<String, String> data = get();
                    lblTotalPenjualan.setText(data.getOrDefault("total_penjualan", "0"));
                    lblProdukTerjual.setText(data.getOrDefault("produk_terjual", "0"));
                    lblPelanggan.setText(data.getOrDefault("pelanggan", "0"));
                    lblOmzet.setText(data.getOrDefault("omzet", "Rp 0"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    private RoundedPanel createHeaderButton(String text, boolean isPrimary, Runnable action) {
        Color defaultBg = isPrimary ? Theme.BLUE_ACCENT : Theme.BG;
        Color hoverBg = isPrimary ? Theme.BLUE_ACCENT.darker() : Theme.CARD;
        Color borderColor = isPrimary ? Theme.BLUE_ACCENT : Theme.BORDER;

        RoundedPanel panel = new RoundedPanel(10, defaultBg);
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createLineBorder(borderColor, 1));
        panel.setPreferredSize(new Dimension(150, 35));
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setForeground(isPrimary ? Color.WHITE : Theme.TEXT_PRIMARY);
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        panel.add(label, BorderLayout.CENTER);

        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                action.run();
            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                panel.setBackground(hoverBg);
                panel.repaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                panel.setBackground(defaultBg);
                panel.repaint();
            }
        });

        return panel;
    }

    private RoundedPanel createStatCard(String title, JLabel lblValue, String subtitle, Color subtitleColor) {
        RoundedPanel card = new RoundedPanel(20, Theme.CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(15, 10, 15, 10));

        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setForeground(Theme.TEXT_SECONDARY);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 10));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel valuePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        valuePanel.setOpaque(false);
        JLabel lblUnit = new JLabel("");
        lblUnit.setForeground(Theme.TEXT_SECONDARY);
        valuePanel.add(lblValue);
        valuePanel.add(lblUnit);

        lblValue.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblValue.setFont(new Font("SansSerif", Font.BOLD, 32));

        JLabel lblStatus = new JLabel(subtitle, SwingConstants.CENTER);
        lblStatus.setForeground(subtitleColor);
        lblStatus.setFont(new Font("SansSerif", Font.PLAIN, 12));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(lblTitle);
        card.add(Box.createVerticalStrut(10));
        card.add(valuePanel);
        card.add(Box.createVerticalGlue());
        card.add(lblStatus);
        return card;
    }

    private JLabel createAnimatedLabel() {
        JLabel l = new JLabel("...", SwingConstants.CENTER);
        l.setForeground(Theme.TEXT_PRIMARY);
        l.setFont(new Font("SansSerif", Font.BOLD, 32));
        return l;
    }
}