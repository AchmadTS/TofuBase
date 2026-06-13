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

    private final BahanBakuDAO bahanBakuDAO = new BahanBakuDAO();

    public BahanBaku(String userName, String userRole) {
        setLayout(new BorderLayout());
        setBackground(Theme.BG);
        add(createMainContent(), BorderLayout.CENTER);
    }

    private JPanel createMainContent() {
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

        JScrollPane mainScroll = new JScrollPane(container);
        mainScroll.setBorder(null);
        mainScroll.getViewport().setBackground(Theme.BG);
        mainScroll.getVerticalScrollBar().setUnitIncrement(16);
        mainScroll.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        mainScroll.getHorizontalScrollBar().setUnitIncrement(16);
        mainScroll.getHorizontalScrollBar().setUI(new ModernScrollBarUI());
        mainContent.add(mainScroll, BorderLayout.CENTER);
        return mainContent;
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.BG);
        header.setBorder(new EmptyBorder(20, 30, 10, 30));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setBackground(Theme.BG);
        JLabel headerTitle = new JLabel("Bahan Baku");
        headerTitle.setFont(new Font("SansSerif", Font.BOLD, 28));
        headerTitle.setForeground(Theme.TEXT_PRIMARY);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", Locale.forLanguageTag("id-ID"));
        JLabel headerDate = new JLabel(LocalDate.now().format(formatter));
        headerDate.setFont(new Font("SansSerif", Font.PLAIN, 14));
        headerDate.setForeground(Theme.TEXT_SECONDARY);
        new Timer(60_000, e -> headerDate.setText(LocalDate.now().format(formatter))).start();

        titlePanel.add(headerTitle);
        titlePanel.add(headerDate);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Theme.BG);
        RoundedPanel btnExport = createHeaderButton("Export PDF", false);
        RoundedPanel btnAdd = createHeaderButton("+ Tambah Data", true);
        btnAdd.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(BahanBaku.this);
                ModalTambahBahan modal = new ModalTambahBahan(parentFrame);
                modal.setVisible(true);
                if (modal.isSaved()) {
                    removeAll();
                    add(createMainContent(), BorderLayout.CENTER);
                    revalidate();
                    repaint();
                }
            }
        });

        buttonPanel.add(btnExport);
        buttonPanel.add(btnAdd);
        header.add(titlePanel, BorderLayout.WEST);
        header.add(buttonPanel, BorderLayout.EAST);
        return header;
    }

    private JPanel buildTopCards() {
        JPanel topCardsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        topCardsPanel.setBackground(Theme.BG);
        topCardsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

        JLabel lblTotalAset = createAnimatedLabel();
        JLabel lblStatusAset = createStatusLabel("Total nilai gudang", Theme.TEXT_SECONDARY);

        JLabel lblStokKedelai = createAnimatedLabel();
        JLabel lblStatusKedelai = createStatusLabel("Menghitung...", Theme.TEXT_SECONDARY);

        JLabel lblPemasok = createAnimatedLabel();
        JLabel lblStatusPemasok = createStatusLabel("Terdaftar unik", Theme.TEXT_SECONDARY);

        topCardsPanel.add(createStatCard("NILAI ASET STOK", lblTotalAset, "Jt", lblStatusAset));
        topCardsPanel.add(createStatCard("STOK KEDELAI", lblStokKedelai, "kg", lblStatusKedelai));
        topCardsPanel.add(createStatCard("PEMASOK AKTIF", lblPemasok, "orang", lblStatusPemasok));

        new Thread(() -> {
            Map<String, String> data = bahanBakuDAO.getTopCardsData();
            String colorStr = data.getOrDefault("status_ked_color", "GRAY");
            Color statusColor = "RED".equals(colorStr) ? Theme.RED : ("WARNING".equals(colorStr) ? Theme.WARNING : Theme.GREEN);

            SwingUtilities.invokeLater(() -> {
                lblTotalAset.setText(data.getOrDefault("aset", "0"));
                lblStokKedelai.setText(data.getOrDefault("kedelai", "0"));
                lblStatusKedelai.setText(data.getOrDefault("status_ked_txt", "Tidak ada data"));
                lblStatusKedelai.setForeground(statusColor);
                lblPemasok.setText(data.getOrDefault("pemasok", "0"));
            });
        }).start();
        return topCardsPanel;
    }

    private ActivityTable buildBahanTable() {
        String[] bahanHeaders = {"ID", "Nama Bahan", "Stok Tersedia", "Satuan", "Rata-rata harga Beli", "Min. Stok", "Status Stok", "Aksi"};
        ActivityTable table = new ActivityTable("Daftar Stok Bahan Baku", bahanHeaders, 6, new ActivityTable.DataProvider() {
            @Override
            public int getTotalRowCount(String keyword) {
                return bahanBakuDAO.getTableTotalRows(keyword);
            }

            @Override
            public List<String[]> getPageData(int limit, int offset, String keyword) {
                return bahanBakuDAO.getTablePageData(limit, offset, keyword);
            }
        });

        table.setTableActionListener((id, name) -> {
            JOptionPane.showMessageDialog(this, "Membuka Riwayat Transaksi untuk:\n\nBahan: " + name + "\nID: " + id, "Informasi Sistem", JOptionPane.INFORMATION_MESSAGE);
        });

        return table;
    }

    private RoundedPanel createHeaderButton(String text, boolean isPrimary) {
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

        valuePanel.add(lblValue);
        valuePanel.add(new JLabel(unit) {
            {
                setForeground(Theme.TEXT_SECONDARY);
            }
        });

        card.add(lblTitle);
        card.add(Box.createVerticalStrut(10));
        card.add(valuePanel);
        card.add(Box.createVerticalGlue());
        card.add(lblStatus);
        return card;
    }
}
