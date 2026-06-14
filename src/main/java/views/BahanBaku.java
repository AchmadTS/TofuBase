package views;

import components.ActivityTable;
import components.ModernScrollBarUI;
import components.RoundedPanel;
import dao.BahanBakuDAO;
import utils.Theme;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BahanBaku extends JPanel {

    private static final String TITLE = "Bahan Baku";
    private static final String BTN_EXPORT = "Export PDF";
    private static final String BTN_ADD = "+ Tambah Data";
    private static final String CARD_ASET_TITLE = "NILAI ASET STOK";
    private static final String CARD_ASET_UNIT = "Jt";
    private static final String CARD_KEDELAI_TITLE = "STOK KEDELAI";
    private static final String CARD_KEDELAI_UNIT = "kg";
    private static final String CARD_PEMASOK_TITLE = "PEMASOK AKTIF";
    private static final String CARD_PEMASOK_UNIT = "orang";
    private static final int SCROLL_SPEED = 16;
    private static final int TIMER_DELAY_MS = 60_000;
    private final BahanBakuDAO bahanBakuDAO = new BahanBakuDAO();
    private JLabel lblTotalAset, lblStatusAset;
    private JLabel lblStokKedelai, lblStatusKedelai;
    private JLabel lblPemasok, lblStatusPemasok;
    private ActivityTable tableBahan;

    public BahanBaku(String userName, String userRole) {
        setLayout(new BorderLayout());
        setBackground(Theme.BG);
        initializeDynamicComponents();
        add(buildMainContent(), BorderLayout.CENTER);
        refreshData();
    }

    private void initializeDynamicComponents() {
        lblTotalAset = createAnimatedLabel();
        lblStatusAset = createStatusLabel("Total nilai gudang", Theme.TEXT_SECONDARY);

        lblStokKedelai = createAnimatedLabel();
        lblStatusKedelai = createStatusLabel("Menghitung...", Theme.TEXT_SECONDARY);

        lblPemasok = createAnimatedLabel();
        lblStatusPemasok = createStatusLabel("Terdaftar unik", Theme.TEXT_SECONDARY);
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
        container.add(buildBahanTable());

        mainContent.add(buildScrollPane(container), BorderLayout.CENTER);
        return mainContent;
    }

    private JScrollPane buildScrollPane(JPanel content) {
        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Theme.BG);
        scroll.getVerticalScrollBar().setUnitIncrement(SCROLL_SPEED);
        scroll.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        scroll.getHorizontalScrollBar().setUnitIncrement(SCROLL_SPEED);
        scroll.getHorizontalScrollBar().setUI(new ModernScrollBarUI());
        return scroll;
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
        buttonPanel.add(createHeaderButton(BTN_ADD, true, this::handleAddData));

        return buttonPanel;
    }

    private JPanel buildTopCards() {
        JPanel topCardsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        topCardsPanel.setBackground(Theme.BG);
        topCardsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

        topCardsPanel.add(createStatCard(CARD_ASET_TITLE, lblTotalAset, CARD_ASET_UNIT, lblStatusAset));
        topCardsPanel.add(createStatCard(CARD_KEDELAI_TITLE, lblStokKedelai, CARD_KEDELAI_UNIT, lblStatusKedelai));
        topCardsPanel.add(createStatCard(CARD_PEMASOK_TITLE, lblPemasok, CARD_PEMASOK_UNIT, lblStatusPemasok));

        return topCardsPanel;
    }

    private void fetchTopCardsData() {
        lblStokKedelai.setText("...");

        new SwingWorker<Map<String, String>, Void>() {
            @Override
            protected Map<String, String> doInBackground() {
                return bahanBakuDAO.getTopCardsData();
            }

            @Override
            protected void done() {
                try {
                    Map<String, String> data = get();
                    String colorStr = data.getOrDefault("status_ked_color", "GRAY");
                    Color statusColor = resolveStatusColor(colorStr);

                    lblTotalAset.setText(data.getOrDefault("aset", "0"));
                    lblStokKedelai.setText(data.getOrDefault("kedelai", "0"));
                    lblStatusKedelai.setText(data.getOrDefault("status_ked_txt", "Tidak ada data"));
                    lblStatusKedelai.setForeground(statusColor);
                    lblPemasok.setText(data.getOrDefault("pemasok", "0"));
                } catch (Exception e) {
                    e.printStackTrace();
                    lblStatusKedelai.setText("Gagal memuat data");
                }
            }
        }.execute();
    }

    private Color resolveStatusColor(String colorStr) {
        switch (colorStr) {
            case "RED":
                return Theme.RED;
            case "WARNING":
                return Theme.WARNING;
            case "GREEN":
                return Theme.GREEN;
            default:
                return Theme.TEXT_SECONDARY;
        }
    }

    private ActivityTable buildBahanTable() {
        String[] bahanHeaders = {"ID", "Nama Bahan", "Stok Tersedia", "Satuan", "Harga Rata-rata", "Min. Stok", "Status Stok", "Aksi"};
        tableBahan = new ActivityTable("Daftar Stok Bahan Baku", bahanHeaders, 6, new ActivityTable.DataProvider() {
            @Override
            public int getTotalRowCount(String keyword) {
                return bahanBakuDAO.getTableTotalRows(keyword);
            }

            @Override
            public List<String[]> getPageData(int limit, int offset, String keyword) {
                return bahanBakuDAO.getTablePageData(limit, offset, keyword);
            }
        });

        tableBahan.setTableActionListener(this::handleViewHistory);
        return tableBahan;
    }

    private void handleExportPDF() {
        JOptionPane.showMessageDialog(this, "Fitur Export PDF sedang dalam pengembangan.", "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleAddData() {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        ModalTambahBahan modal = new ModalTambahBahan(parentFrame);
        modal.setVisible(true);

        if (modal.isSaved()) {
            refreshData();
        }
    }

    private void handleViewHistory(String id, String name) {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        ModalRiwayat modal = new ModalRiwayat(parentFrame, id, name);
        modal.setVisible(true);
        refreshData();
    }

    private void refreshData() {
        fetchTopCardsData();
        if (tableBahan != null) {
            tableBahan.updateTableModel();
        }
    }

    private RoundedPanel createHeaderButton(String text, boolean isPrimary, Runnable action) {
        Color defaultBg = isPrimary ? Theme.BLUE_ACCENT : Theme.BG;
        Color hoverBg = isPrimary ? Theme.BLUE_ACCENT.darker() : Theme.CARD;
        Color borderColor = isPrimary ? Theme.BLUE_ACCENT : Theme.BORDER;

        RoundedPanel panel = new RoundedPanel(10, defaultBg);
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createLineBorder(borderColor, 1));
        panel.setPreferredSize(new Dimension(130, 35));
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setForeground(isPrimary ? Color.WHITE : Theme.TEXT_PRIMARY);
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        panel.add(label, BorderLayout.CENTER);
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                action.run();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                panel.setBackground(hoverBg);
                if (!isPrimary) {
                    panel.setBorder(BorderFactory.createLineBorder(Theme.TEXT_SECONDARY, 1));
                }
                panel.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                panel.setBackground(defaultBg);
                if (!isPrimary) {
                    panel.setBorder(BorderFactory.createLineBorder(borderColor, 1));
                }
                panel.repaint();
            }
        });
        return panel;
    }

    private JLabel createAnimatedLabel() {
        JLabel l = new JLabel("...", SwingConstants.CENTER);
        l.setForeground(Theme.TEXT_PRIMARY);
        l.setFont(new Font("SansSerif", Font.BOLD, 32));
        return l;
    }

    private JLabel createStatusLabel(String text, Color color) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setForeground(color);
        l.setFont(new Font("SansSerif", Font.PLAIN, 12));
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        return l;
    }

    private JPanel createStatCard(String title, JLabel lblValue, String unit, JLabel lblStatus) {
        RoundedPanel card = new RoundedPanel(20, Theme.CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(15, 10, 15, 10));

        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setForeground(Theme.TEXT_SECONDARY);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 10));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel valuePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        valuePanel.setBackground(Theme.CARD);
        valuePanel.setOpaque(false);
        valuePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel lblUnit = new JLabel(unit);
        lblUnit.setForeground(Theme.TEXT_SECONDARY);
        valuePanel.add(lblValue);
        valuePanel.add(lblUnit);

        card.add(lblTitle);
        card.add(Box.createVerticalStrut(10));
        card.add(valuePanel);
        card.add(Box.createVerticalGlue());
        card.add(lblStatus);
        return card;
    }
}
