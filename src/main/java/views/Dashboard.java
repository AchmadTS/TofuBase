package views;

import components.ActivityTable;
import components.ModernScrollBarUI;
import components.RoundedPanel;
import dao.DashboardDAO;
import utils.FormatUtil;
import utils.Theme;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import models.Produksi;
import models.RecordProduksi;

public class Dashboard extends JPanel {

    private final DashboardDAO dashboardDAO = new DashboardDAO();
    private JLabel lblProdHariIni;
    private JLabel lblStokKedelai;
    private JLabel lblPendapatan;
    private JLabel lblTahuSiapJual;
    private JPanel statusListPanel;

    public Dashboard(String userName, String userRole) {
        setLayout(new BorderLayout());
        setBackground(Theme.BG);
        add(createMainContent(), BorderLayout.CENTER);
        startAutoRefresh();
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
        container.add(buildMiddlePanel());
        container.add(Box.createVerticalStrut(20));
        container.add(buildActivityTable());

        JScrollPane mainScrollPane = new JScrollPane(container);
        mainScrollPane.setBorder(null);
        mainScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainScrollPane.getViewport().setBackground(Theme.BG);
        mainScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mainScrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());

        mainContent.add(mainScrollPane, BorderLayout.CENTER);
        return mainContent;
    }

    private void startAutoRefresh() {
        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent evt) {
                refreshTopCardsData();
                refreshStatusStokData();
            }
        });

        new Timer(15_000, e -> {
            if (isShowing()) {
                refreshTopCardsData();
                refreshStatusStokData();
            }
        }).start();
        refreshTopCardsData();
        refreshStatusStokData();
    }

    private void refreshTopCardsData() {
        new Thread(() -> {
            Map<String, String> data = dashboardDAO.getTopCardsData();
            SwingUtilities.invokeLater(() -> {
                if (lblProdHariIni != null) {
                    lblProdHariIni.setText(data.getOrDefault("produksi", "0"));
                }
                if (lblStokKedelai != null) {
                    lblStokKedelai.setText(data.getOrDefault("stok", "0"));
                }
                if (lblPendapatan != null) {
                    lblPendapatan.setText(data.getOrDefault("pendapatan", "Rp 0"));
                }
                if (lblTahuSiapJual != null) {
                    lblTahuSiapJual.setText(data.getOrDefault("tahu", "0"));
                }
            });
        }).start();
    }

    private void refreshStatusStokData() {
        new Thread(() -> {
            List<String[]> dbRows = dashboardDAO.getStatusStokData();
            List<JPanel> uiRows = new ArrayList<>();
            for (String[] r : dbRows) {
                String nama = r[0], satuan = r[1];
                double minStok = Double.parseDouble(r[2]), stok = Double.parseDouble(r[3]);
                String sub = "Min. stok: " + FormatUtil.formatAngka(minStok) + " " + satuan;
                String val = FormatUtil.formatAngka(stok) + " " + satuan;
                String bText = stok <= minStok / 2 ? "Kritis" : (stok <= minStok ? "Rendah" : "Aman");
                Color bColor = stok <= minStok / 2 ? Theme.RED : (stok <= minStok ? Theme.WARNING : Theme.GREEN);
                uiRows.add(createStatusRow(nama, sub, val, bText, bColor));
            }
            SwingUtilities.invokeLater(() -> {
                if (statusListPanel != null) {
                    statusListPanel.removeAll();
                    statusListPanel.add(Box.createVerticalStrut(20));
                    for (JPanel row : uiRows) {
                        statusListPanel.add(row);
                        statusListPanel.add(Box.createVerticalStrut(15));
                    }
                    statusListPanel.revalidate();
                    statusListPanel.repaint();
                }
            });
        }).start();
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.BG);
        header.setBorder(new EmptyBorder(20, 30, 10, 30));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setBackground(Theme.BG);
        JLabel headerTitle = new JLabel("Dashboard");
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
//        btnAdd.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseClicked(MouseEvent e) {
//                Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(Dashboard.this);
//                ModalTambahBahan modal = new ModalTambahBahan(parentFrame);
//                modal.setVisible(true);
//                if (modal.isSaved()) {
//                    refreshTopCardsData();
//                    refreshStatusStokData();
//                }
//            }
//        });

        buttonPanel.add(btnExport);
        buttonPanel.add(btnAdd);

        header.add(titlePanel, BorderLayout.WEST);
        header.add(buttonPanel, BorderLayout.EAST);
        return header;
    }

    private JPanel buildTopCards() {
        JPanel topCardsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        topCardsPanel.setBackground(Theme.BG);
        topCardsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

        lblProdHariIni = createAnimatedLabel();
        lblStokKedelai = createAnimatedLabel();
        lblPendapatan = createAnimatedLabel();
        lblTahuSiapJual = createAnimatedLabel();

        topCardsPanel.add(createStatCard("PRODUKSI HARI INI", lblProdHariIni, "potong", "Terbaru hari ini", Theme.GREEN));
        topCardsPanel.add(createStatCard("STOK KEDELAI", lblStokKedelai, "kg", "Sesuai gudang", Theme.WARNING));
        topCardsPanel.add(createStatCard("PENDAPATAN", lblPendapatan, "jt", "Bulan ini", Theme.GREEN));
        topCardsPanel.add(createStatCard("TAHU SIAP JUAL", lblTahuSiapJual, "potong", "Total semua jenis", Theme.TEXT_SECONDARY));
        return topCardsPanel;
    }

    private JPanel buildMiddlePanel() {
        JPanel middlePanel = new JPanel(new BorderLayout(20, 0));
        middlePanel.setBackground(Theme.BG);
        middlePanel.setMinimumSize(new Dimension(800, 300));
        middlePanel.setPreferredSize(new Dimension(800, 300));
        middlePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));

        RoundedPanel chartPanel = new RoundedPanel(20, Theme.CARD);
        chartPanel.setLayout(new BorderLayout(0, 15));
        chartPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel chartHeader = new JPanel(new BorderLayout());
        chartHeader.setOpaque(false);
        JLabel chartTitle = new JLabel("Produksi Hari Terakhir");
        chartTitle.setForeground(Theme.TEXT_PRIMARY);
        chartTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        chartHeader.add(chartTitle, BorderLayout.WEST);

        JComboBox<String> cbTimeframe = new JComboBox<>(new String[]{"1W", "1M", "3M", "ALL"});
        cbTimeframe.setBackground(Theme.BG);
        cbTimeframe.setForeground(Theme.TEXT_PRIMARY);
        cbTimeframe.setCursor(new Cursor(Cursor.HAND_CURSOR));
        chartHeader.add(cbTimeframe, BorderLayout.EAST);
        chartPanel.add(chartHeader, BorderLayout.NORTH);

        JPanel chartFooter = new JPanel(new BorderLayout());
        chartFooter.setOpaque(false);
        chartFooter.setBorder(new EmptyBorder(10, 0, 0, 0));

        JLabel lblTotalSummary = new JLabel("Menghitung...");
        lblTotalSummary.setForeground(Theme.TEXT_PRIMARY);
        lblTotalSummary.setFont(new Font("SansSerif", Font.BOLD, 14));

        JLabel lblAvgSummary = new JLabel("Menghitung...");
        lblAvgSummary.setForeground(Theme.TEXT_SECONDARY);
        lblAvgSummary.setFont(new Font("SansSerif", Font.PLAIN, 14));

        chartFooter.add(lblTotalSummary, BorderLayout.WEST);
        chartFooter.add(lblAvgSummary, BorderLayout.EAST);
        chartPanel.add(chartFooter, BorderLayout.SOUTH);

        final List<String> chartLabels = new ArrayList<>();
        final List<Integer> chartValues = new ArrayList<>();
        JPanel mockChart = createMockChartPanel(chartLabels, chartValues);

        Runnable triggerChartFetch = () -> {
            String selected = (String) cbTimeframe.getSelectedItem();
            List<Object[]> dbData = dashboardDAO.getChartData(selected);

            chartLabels.clear();
            chartValues.clear();
            int totalSum = 0;
            for (Object[] row : dbData) {
                chartLabels.add((String) row[0]);
                chartValues.add((Integer) row[1]);
                totalSum += (Integer) row[1];
            }

            if (chartValues.isEmpty()) {
                chartLabels.add("-");
                chartValues.add(0);
            }

            int divisor = "1W".equals(selected) ? 7 : ("1M".equals(selected) ? 30 : ("3M".equals(selected) ? 90 : Math.max(1, chartValues.size() * 30)));
            String timeText = "1W".equals(selected) ? "minggu ini" : ("1M".equals(selected) ? "bulan ini" : ("3M".equals(selected) ? "3 bulan terakhir" : "keseluruhan"));
            String cTitle = "1W".equals(selected) ? "Produksi 7 Hari Terakhir" : ("1M".equals(selected) ? "Produksi 30 Hari Terakhir" : ("3M".equals(selected) ? "Produksi 3 Bulan Terakhir" : "Total Produksi Keseluruhan"));

            int avgPerDay = totalSum / divisor;
            String fTotal = "Total " + timeText + ": " + FormatUtil.formatAngka(totalSum) + " potong";
            String fAvg = "Rata-rata: " + FormatUtil.formatAngka(avgPerDay) + " potong/hari";

            SwingUtilities.invokeLater(() -> {
                chartTitle.setText(cTitle);
                lblTotalSummary.setText(fTotal);
                lblAvgSummary.setText(fAvg);
                mockChart.repaint();
            });
        };

        new Thread(triggerChartFetch).start();
        cbTimeframe.addActionListener(e -> {
            mockChart.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            lblTotalSummary.setText("Menghitung...");
            lblAvgSummary.setText("Menghitung...");
            new Thread(triggerChartFetch).start();
        });

        chartPanel.add(mockChart, BorderLayout.CENTER);

        // Status Stok
        RoundedPanel statusPanel = new RoundedPanel(20, Theme.CARD);
        statusPanel.setPreferredSize(new Dimension(320, 0));
        statusPanel.setLayout(new BorderLayout());
        statusPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel statusTitle = new JLabel("Status Stok Bahan");
        statusTitle.setForeground(Theme.TEXT_PRIMARY);
        statusTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        statusPanel.add(statusTitle, BorderLayout.NORTH);

        statusListPanel = new JPanel();
        statusListPanel.setLayout(new BoxLayout(statusListPanel, BoxLayout.Y_AXIS));
        statusListPanel.setOpaque(false);

        JLabel lblLoadingStatus = new JLabel("Memuat data...");
        lblLoadingStatus.setForeground(Theme.TEXT_SECONDARY);
        lblLoadingStatus.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusListPanel.add(Box.createVerticalStrut(20));
        statusListPanel.add(lblLoadingStatus);

        JScrollPane statusScroll = new JScrollPane(statusListPanel);
        statusScroll.setOpaque(false);
        statusScroll.getViewport().setOpaque(false);
        statusScroll.setBorder(null);
        statusScroll.getVerticalScrollBar().setUnitIncrement(16);
        statusScroll.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        statusScroll.getHorizontalScrollBar().setUI(new ModernScrollBarUI());
        statusScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        statusPanel.add(statusScroll, BorderLayout.CENTER);

        middlePanel.add(chartPanel, BorderLayout.CENTER);
        middlePanel.add(statusPanel, BorderLayout.EAST);
        return middlePanel;
    }

    private ActivityTable buildActivityTable() {
        String[] dashboardHeaders = {"Tanggal", "Batch", "Kedelai Digunakan", "Hasil Tahu", "Operator", "Status"};
        return new ActivityTable("Aktivitas Produksi Terbaru", dashboardHeaders, 5, new ActivityTable.DataProvider() {
            @Override
            public int getTotalRowCount(String keyword) {
                return dashboardDAO.getTableTotalRows(keyword);
            }

            @Override
            public List<String[]> getPageData(int limit, int offset, String keyword) {
                List<Produksi> listProduksi = dashboardDAO.getListProduksiLengkap(limit, offset, keyword);
                List<String[]> dataTabel = new ArrayList<>();

                for (Produksi p : listProduksi) {
                    double totalKedelai = 0;
                    String satuan = "-";
                    for (RecordProduksi rp : p.getRecords()) {
                        totalKedelai += rp.getJumlah();
                        satuan = rp.getSatuan();
                    }

                    dataTabel.add(new String[]{
                        p.getTanggal().toString(),
                        "Batch-" + p.getIdProduksi(),
                        FormatUtil.formatAngka(totalKedelai) + " " + satuan,
                        FormatUtil.formatAngka(p.getHasilTahu()) + " potong",
                        p.getNamaOperator(),
                        p.getStatus()
                    });
                }
                return dataTabel;
            }
        });
    }

    private JLabel createAnimatedLabel() {
        JLabel l = new JLabel("...", SwingConstants.CENTER);
        l.setForeground(Theme.TEXT_PRIMARY);
        l.setFont(new Font("SansSerif", Font.BOLD, 32));
        return l;
    }

    private JPanel createStatCard(String title, JLabel lblValue, String unit, String status, Color statusColor) {
        RoundedPanel card = new RoundedPanel(20, Theme.CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(15, 10, 15, 10));

        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setForeground(Theme.TEXT_SECONDARY);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 10));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel valuePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        valuePanel.setBackground(Theme.CARD);
        card.setOpaque(false);
        valuePanel.setOpaque(false);
        valuePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        valuePanel.add(lblValue);
        valuePanel.add(new JLabel(unit) {
            {
                setForeground(Theme.TEXT_SECONDARY);
            }
        });

        JLabel lblStatus = new JLabel(status, SwingConstants.CENTER);
        lblStatus.setForeground(statusColor);
        lblStatus.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblStatus.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(lblTitle);
        card.add(Box.createVerticalStrut(10));
        card.add(valuePanel);
        card.add(Box.createVerticalGlue());
        card.add(lblStatus);
        return card;
    }

    private JPanel createStatusRow(String name, String sub, String val, String badgeText, Color badgeColor) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(Theme.CARD);
        JPanel leftPanel = new JPanel(new GridLayout(2, 1));
        leftPanel.setBackground(Theme.CARD);
        leftPanel.add(new JLabel(name) {
            {
                setForeground(Theme.TEXT_PRIMARY);
                setFont(new Font("SansSerif", Font.BOLD, 14));
            }
        });
        leftPanel.add(new JLabel(sub) {
            {
                setForeground(Theme.TEXT_SECONDARY);
                setFont(new Font("SansSerif", Font.PLAIN, 10));
            }
        });

        JPanel rightPanel = new JPanel(new GridLayout(2, 1));
        rightPanel.setBackground(Theme.CARD);
        rightPanel.add(new JLabel(val, SwingConstants.RIGHT) {
            {
                setForeground(badgeColor);
                setFont(new Font("SansSerif", Font.BOLD, 14));
            }
        });

        RoundedPanel badge = new RoundedPanel(10, new Color(badgeColor.getRed(), badgeColor.getGreen(), badgeColor.getBlue(), 50));
        badge.setLayout(new BorderLayout());
        badge.add(new JLabel(badgeText, SwingConstants.CENTER) {
            {
                setForeground(badgeColor);
                setFont(new Font("SansSerif", Font.BOLD, 10));
            }
        }, BorderLayout.CENTER);

        rightPanel.add(badge);
        row.add(leftPanel, BorderLayout.WEST);
        row.add(rightPanel, BorderLayout.EAST);
        return row;
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

    private JPanel createMockChartPanel(List<String> chartLabels, List<Integer> chartValues) {
        JPanel mockChart = new JPanel() {
            private int hoveredBarIndex = -1;

            {
                addMouseMotionListener(new MouseMotionAdapter() {
                    @Override
                    public void mouseMoved(MouseEvent e) {
                        int index = getHoveredBarIndex(e.getX(), e.getY());
                        if (index != hoveredBarIndex) {
                            hoveredBarIndex = index;
                            setCursor(new Cursor(hoveredBarIndex != -1 ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
                            repaint();
                        }
                    }
                });
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseExited(MouseEvent e) {
                        if (hoveredBarIndex != -1) {
                            hoveredBarIndex = -1;
                            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                            repaint();
                        }
                    }
                });
            }

            private int getHoveredBarIndex(int mouseX, int mouseY) {
                if (chartValues.isEmpty()) {
                    return -1;
                }
                int n = chartValues.size(), maxVal = 1;
                for (int h : chartValues) {
                    if (h > maxVal) {
                        maxVal = h;
                    }
                }
                maxVal = (int) (maxVal * 1.15);

                int paddingLeft = 50, paddingBottom = 30, paddingTop = 10, paddingRight = 10;
                int chartWidth = getWidth() - paddingLeft - paddingRight;
                int chartHeight = getHeight() - paddingTop - paddingBottom;
                if (chartHeight <= 0 || chartWidth <= 0) {
                    return -1;
                }

                double scale = (double) chartHeight / maxVal;
                int space = n > 15 ? 4 : (n > 7 ? 8 : 15);
                int width = Math.max(2, Math.min((chartWidth - (space * (n - 1))) / n, 40));
                int startX = paddingLeft + (chartWidth - ((width * n) + (space * (n - 1)))) / 2;

                for (int i = 0; i < n; i++) {
                    int barX = startX + (i * (width + space));
                    int scaledHeight = (int) (chartValues.get(i) * scale);
                    int barY = paddingTop + chartHeight - scaledHeight;
                    if (mouseX >= barX && mouseX <= barX + width && mouseY >= barY && mouseY <= barY + scaledHeight) {
                        return i;
                    }
                }
                return -1;
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (chartValues.isEmpty()) {
                    return;
                }

                int n = chartValues.size(), maxVal = 1;
                for (int h : chartValues) {
                    if (h > maxVal) {
                        maxVal = h;
                    }
                }
                maxVal = (int) (maxVal * 1.15);

                int paddingLeft = 50, paddingBottom = 30, paddingTop = 10, paddingRight = 10;
                int chartWidth = getWidth() - paddingLeft - paddingRight;
                int chartHeight = getHeight() - paddingTop - paddingBottom;
                if (chartHeight <= 0 || chartWidth <= 0) {
                    return;
                }

                double scale = (double) chartHeight / maxVal;
                g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
                for (int i = 0; i <= 4; i++) {
                    int y = paddingTop + chartHeight - (i * chartHeight / 4);
                    int value = maxVal * i / 4;
                    g2.setColor(new Color(Theme.BORDER.getRed(), Theme.BORDER.getGreen(), Theme.BORDER.getBlue(), 120));
                    g2.drawLine(paddingLeft, y, paddingLeft + chartWidth, y);
                    g2.setColor(Theme.TEXT_SECONDARY);
                    String yLabel = String.valueOf(value);
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(yLabel, paddingLeft - fm.stringWidth(yLabel) - 10, y + (fm.getAscent() / 2) - 2);
                }

                int space = n > 15 ? 4 : (n > 7 ? 8 : 15);
                int width = Math.max(2, Math.min((chartWidth - (space * (n - 1))) / n, 40));
                int startX = paddingLeft + (chartWidth - ((width * n) + (space * (n - 1)))) / 2;

                for (int i = 0; i < n; i++) {
                    int barX = startX + (i * (width + space));
                    int scaledHeight = (int) (chartValues.get(i) * scale);
                    int barY = paddingTop + chartHeight - scaledHeight;

                    if (n <= 15 || i % 3 == 0 || i == n - 1) {
                        g2.setColor(Theme.TEXT_SECONDARY);
                        String xLabel = chartLabels.get(i);
                        FontMetrics fm = g2.getFontMetrics();
                        g2.drawString(xLabel, barX + (width / 2) - (fm.stringWidth(xLabel) / 2), paddingTop + chartHeight + 20);
                    }

                    g2.setColor(i == hoveredBarIndex ? Theme.BLUE_ACCENT.brighter() : Theme.BLUE_ACCENT);
                    g2.fillRect(barX, barY, width, scaledHeight);
                }

                if (hoveredBarIndex != -1) {
                    int barX = startX + (hoveredBarIndex * (width + space));
                    int scaledHeight = (int) (chartValues.get(hoveredBarIndex) * scale);
                    int barY = paddingTop + chartHeight - scaledHeight;
                    String valueText = FormatUtil.formatAngka(chartValues.get(hoveredBarIndex)) + " potong";
                    String dateText = chartLabels.get(hoveredBarIndex);
                    FontMetrics fm = g2.getFontMetrics();
                    int textWidth = Math.max(fm.stringWidth(valueText), fm.stringWidth(dateText));
                    int tooltipX = barX + (width / 2) - (textWidth / 2) - 8;
                    int tooltipY = Math.max(barY - 45, 0);

                    g2.setColor(new Color(20, 20, 20, 220));
                    g2.fillRoundRect(tooltipX, tooltipY, textWidth + 16, 38, 8, 8);
                    g2.setColor(Color.WHITE);
                    g2.drawString(valueText, tooltipX + 8, tooltipY + 16);
                    g2.setColor(Theme.TEXT_SECONDARY);
                    g2.drawString(dateText, tooltipX + 8, tooltipY + 30);
                }
            }
        };
        mockChart.setBackground(Theme.CARD);
        return mockChart;
    }
}
