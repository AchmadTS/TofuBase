package views;

import components.ActivityTable;
import components.ModernScrollBarUI;
import components.RoundedPanel;
import dao.SupplierDAO;
import utils.Theme;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import models.Supplier;
import java.util.Set;
import java.util.TreeSet;

public class SupplierPanel extends JPanel {

    private static final String TITLE = "Supplier";
    private static final String BTN_EXPORT = "Export PDF";
    private static final String BTN_ADD = "+ Tambah Supplier";
    private static final int SCROLL_SPEED = 16;
    private static final int TIMER_DELAY_MS = 60_000;
    private final SupplierDAO supplierDAO = new SupplierDAO();
    private JLabel lblTotalSupplier, lblBahanDisuplai, lblTotalNilai;
    private ActivityTable tableSupplier;

    public SupplierPanel(String userName, String userRole) {
        setLayout(new BorderLayout());
        setBackground(Theme.BG);
        initializeDynamicComponents();
        add(buildMainContent(), BorderLayout.CENTER);
        startAutoRefresh();
        refreshData();
    }

    private void initializeDynamicComponents() {
        lblTotalSupplier = createAnimatedLabel();
        lblBahanDisuplai = createAnimatedLabel();
        lblTotalNilai = createAnimatedLabel();
    }

    private void startAutoRefresh() {
        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent evt) {
                refreshData();
            }
        });

        new Timer(15_000, e -> {
            if (isShowing()) {
                refreshData();
            }
        }).start();
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
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
        container.add(buildSupplierTable());

        mainContent.add(buildScrollPane(container), BorderLayout.CENTER);
        return mainContent;
    }

    // --- HEADER ---
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
    // --- HEADER END ---

    private JPanel buildTopCards() {
        JPanel topCardsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        topCardsPanel.setBackground(Theme.BG);
        topCardsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        topCardsPanel.add(createStatCard("TOTAL SUPPLIER", lblTotalSupplier, "Pemasok terdaftar", Theme.TEXT_SECONDARY));
        topCardsPanel.add(createStatCard("BAHAN DISUPLAI", lblBahanDisuplai, "Jenis bahan aktif", Theme.GREEN));
        topCardsPanel.add(createStatCard("TOTAL NILAI PASOKAN", lblTotalNilai, "Total aset nilai", Theme.TEXT_SECONDARY));
        return topCardsPanel;
    }

    private ActivityTable buildSupplierTable() {
        String[] headers = {"ID", "Nama Supplier", "Kontak", "Bahan Disuplai", "Aksi"};
        tableSupplier = new ActivityTable("Daftar Supplier", headers, 5, new ActivityTable.DataProvider() {
            @Override
            public int getTotalRowCount(String keyword) {
                return supplierDAO.getTableTotalRows(keyword);
            }

            @Override
            public List<String[]> getPageData(int limit, int offset, String keyword) {
                List<Supplier> suppliers = supplierDAO.getTablePageData(limit, offset, keyword);
                List<String[]> dataTabel = new ArrayList<>();
                for (Supplier s : suppliers) {
                    List<String> bahanList = s.getDaftarBahan();
                    Set<String> uniqueBahan = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
                    uniqueBahan.addAll(bahanList);

                    String displayBahan;
                    if (uniqueBahan.isEmpty()) {
                        displayBahan = "-";
                    } else if (uniqueBahan.size() == 1) {
                        displayBahan = uniqueBahan.iterator().next();
                    } else {
                        displayBahan = uniqueBahan.size() + " Bahan";
                    }
                    dataTabel.add(new String[]{
                        String.valueOf(s.getIdSupplier()),
                        s.getNama(),
                        s.getNoTelp(),
                        displayBahan,
                        ""
                    });
                }
                return dataTabel;
            }
        });

        tableSupplier.setTableActionListener(this::handleAction);
        return tableSupplier;
    }

    // --- ACTIONS ---
    private void handleExportPDF() {
        JOptionPane.showMessageDialog(this, "Fitur Export PDF Supplier sedang dalam pengembangan.", "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleAddData() {
        ModalTambahSupplier modal = new ModalTambahSupplier((Frame) SwingUtilities.getWindowAncestor(this));
        modal.setVisible(true);
        if (modal.isSaved()) {
            refreshData();
        }
    }

    private void handleAction(String id, String name) {
        System.out.println("Action untuk supplier ID: " + id);
    }

    private void refreshData() {
        fetchTopCardsData();
        if (tableSupplier != null) {
            tableSupplier.updateTableModel();
        }
    }

    private void fetchTopCardsData() {
        new SwingWorker<Map<String, String>, Void>() {
            @Override
            protected Map<String, String> doInBackground() {
                return supplierDAO.getTopCardsData();
            }

            @Override
            protected void done() {
                try {
                    Map<String, String> data = get();
                    lblTotalSupplier.setText(data.getOrDefault("total_supplier", "0"));
                    lblBahanDisuplai.setText(data.getOrDefault("bahan_disuplai", "0"));
                    lblTotalNilai.setText(data.getOrDefault("total_nilai", "Rp 0"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    // --- UTILS UI ---
    private JScrollPane buildScrollPane(JPanel content) {
        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Theme.BG);
        scroll.getVerticalScrollBar().setUnitIncrement(SCROLL_SPEED);
        scroll.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        return scroll;
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
                panel.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                panel.setBackground(defaultBg);
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

    private JPanel createStatCard(String title, JLabel lblValue, String subtitle, Color subtitleColor) {
        RoundedPanel card = new RoundedPanel(20, Theme.CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(15, 10, 15, 10));

        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setForeground(Theme.TEXT_SECONDARY);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 10));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblValue.setForeground(Theme.TEXT_PRIMARY);
        lblValue.setFont(new Font("SansSerif", Font.BOLD, 32));
        lblValue.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel(subtitle, SwingConstants.CENTER);
        lblSub.setForeground(subtitleColor);
        lblSub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(lblTitle);
        card.add(Box.createVerticalStrut(10));
        card.add(lblValue);
        card.add(Box.createVerticalGlue());
        card.add(lblSub);
        return card;
    }
}
