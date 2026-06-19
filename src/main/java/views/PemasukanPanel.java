package views;

import components.ActivityTable;
import components.ModernScrollBarUI;
import components.RoundedPanel;
import dao.PemasukanDAO;
import utils.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PemasukanPanel extends JPanel {
    private static final String TITLE = "Pemasukan";
    private static final String BTN_EXPORT = "Export PDF";
    private static final String BTN_ADD = "+ Tambah Pemasukan";
    private static final int SCROLL_SPEED = 16;
    private static final int TIMER_DELAY_MS = 60_000;

    private final PemasukanDAO pemasukanDAO = new PemasukanDAO();
    private JLabel lblTotalPemasukan;
    private JLabel lblJumlahTransaksi;
    private JLabel lblRataRata;
    private ActivityTable tablePemasukan;

    public PemasukanPanel(String userName, String userRole) {
        setLayout(new BorderLayout());
        setBackground(Theme.BG);
        initializeDynamicComponents();
        add(buildMainContent(), BorderLayout.CENTER);
        setupListeners();
        refreshData();
    }

    private void initializeDynamicComponents() {
        lblTotalPemasukan = createAnimatedLabel();
        lblJumlahTransaksi = createAnimatedLabel();
        lblRataRata = createAnimatedLabel();
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
        container.add(buildPemasukanTable());

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
        buttonPanel.add(createHeaderButton(BTN_ADD, true, this::handleAddPemasukan));
        return buttonPanel;
    }

    private JPanel buildTopCards() {
        JPanel topCards = new JPanel(new GridLayout(1, 3, 20, 0));
        topCards.setBackground(Theme.BG);
        topCards.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        topCards.add(createStatCard("TOTAL PEMASUKAN", lblTotalPemasukan, "Total penerimaan", Theme.BLUE_ACCENT));
        topCards.add(createStatCard("TRANSAKSI", lblJumlahTransaksi, "Jumlah pencatatan", Theme.GREEN));
        topCards.add(createStatCard("RATA-RATA", lblRataRata, "Rata-rata pemasukan", Theme.WARNING));
        return topCards;
    }

    private ActivityTable buildPemasukanTable() {
        String[] headers = {"ID", "Tanggal", "Sumber", "Jumlah", "Keterangan", "Aksi"};
        tablePemasukan = new ActivityTable("Daftar Pemasukan", headers, -1, new ActivityTable.DataProvider() {
            @Override
            public int getTotalRowCount(String keyword) {
                return pemasukanDAO.getTableTotalRows(keyword);
            }

            @Override
            public java.util.List<String[]> getPageData(int limit, int offset, String keyword) {
                return pemasukanDAO.getTablePageData(limit, offset, keyword);
            }
        });
        tablePemasukan.setTableActionListener((id, name) -> handleViewDetail(id, name));
        return tablePemasukan;
    }

    private void handleViewDetail(String id, String name) {
        JOptionPane.showMessageDialog(this, "Detail pemasukan id " + id + " tidak tersedia saat ini.", "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleExportPDF() {
        JOptionPane.showMessageDialog(this, "Fitur export PDF pemasukan sedang dalam pengembangan.", "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleAddPemasukan() {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        ModalTambahPemasukan modal = new ModalTambahPemasukan(topFrame, this);
        modal.setVisible(true);
        refreshData(); 
    }

    private void refreshData() {
        fetchTopCardsData();
        if (tablePemasukan != null) {
            tablePemasukan.updateTableModel();
        }
    }

    private void fetchTopCardsData() {
        new SwingWorker<Map<String, String>, Void>() {
            @Override
            protected Map<String, String> doInBackground() {
                return pemasukanDAO.getTopCardsData();
            }

            @Override
            protected void done() {
                try {
                    Map<String, String> data = get();
                    lblTotalPemasukan.setText(data.getOrDefault("total_pemasukan", "Rp 0"));
                    lblJumlahTransaksi.setText(data.getOrDefault("jumlah_transaksi", "0"));
                    lblRataRata.setText(data.getOrDefault("rata_rata", "Rp 0"));
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
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblValue.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblValue.setFont(new Font("SansSerif", Font.BOLD, 32));

        JLabel lblStatus = new JLabel(subtitle, SwingConstants.CENTER);
        lblStatus.setForeground(subtitleColor);
        lblStatus.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblStatus.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(lblTitle);
        card.add(Box.createVerticalStrut(10));
        card.add(lblValue);
        card.add(Box.createVerticalStrut(10));
        card.add(lblStatus);
        return card;
    }

    private JLabel createAnimatedLabel() {
        JLabel label = new JLabel("0", SwingConstants.CENTER);
        label.setForeground(Theme.TEXT_PRIMARY);
        return label;
    }
}
