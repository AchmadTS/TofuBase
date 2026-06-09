package views;

import components.ActivityTable;
import components.ModernScrollBarUI;
import components.RoundedPanel;
import components.Sidebar;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import utils.Theme;

public class Dashboard extends JFrame {

    public Dashboard(String userName, String userRole) {
        setTitle("TofuBase - Pabrik Tahu");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Theme.BG);

        add(new Sidebar(userName, userRole, "Dashboard"), BorderLayout.WEST);
        add(createMainContent(), BorderLayout.CENTER);
    }

    private JPanel createMainContent() {
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(Theme.BG);

        // HEADER
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
        new javax.swing.Timer(60_000, e -> headerDate.setText(LocalDate.now().format(formatter))).start();
        titlePanel.add(headerTitle);
        titlePanel.add(headerDate);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Theme.BG);
        JButton btnExport = new JButton("Export PDF");
        JButton btnAdd = new JButton("+ Tambah Data");
        buttonPanel.add(btnExport);
        buttonPanel.add(btnAdd);

        header.add(titlePanel, BorderLayout.WEST);
        header.add(buttonPanel, BorderLayout.EAST);

        // KONTEN DASHBOARD
        JPanel dashboardContainer = new JPanel();
        dashboardContainer.setLayout(new BoxLayout(dashboardContainer, BoxLayout.Y_AXIS));
        dashboardContainer.setBackground(Theme.BG);
        dashboardContainer.setBorder(new EmptyBorder(10, 30, 30, 30));

        String prodHariIni = "0", stokKedelai = "0", pendapatan = "0", tahuSiapJual = "0";
        try {
            Connection conn = utils.DatabaseConfig.getKoneksi();
            Statement stmt = conn.createStatement();

            // Produksi Hari Ini
            ResultSet rsProd = stmt.executeQuery("SELECT SUM(hasil_tahu) AS total FROM produksi WHERE tanggal = CURDATE()");
            if (rsProd.next() && rsProd.getString("total") != null) {
                prodHariIni = rsProd.getString("total");
            }

            // Stok Kedelai
            ResultSet rsKed = stmt.executeQuery("SELECT stok FROM bahan_baku WHERE nama LIKE '%Kedelai%' LIMIT 1");
            if (rsKed.next()) {
                stokKedelai = String.valueOf(rsKed.getInt("stok"));
            }

            // Pendapatan (Total Penjualan Bulan Ini)
            ResultSet rsPend = stmt.executeQuery("SELECT SUM(total) AS total FROM penjualan WHERE MONTH(tanggal) = MONTH(CURDATE()) AND YEAR(tanggal) = YEAR(CURDATE())");
            if (rsPend.next() && rsPend.getString("total") != null) {
                double totalRp = rsPend.getDouble("total");
                pendapatan = String.format(Locale.forLanguageTag("id-ID"), "%.1f", totalRp / 1000000.0);
            }

            // Tahu Siap Jual
            ResultSet rsTahu = stmt.executeQuery("SELECT SUM(stok) AS total FROM produk");
            if (rsTahu.next() && rsTahu.getString("total") != null) {
                tahuSiapJual = rsTahu.getString("total");
            }

        } catch (Exception e) {
            System.err.println("Gagal memuat Top Cards: " + e.getMessage());
        }

        JPanel topCardsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        topCardsPanel.setBackground(Theme.BG);
        topCardsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        topCardsPanel.add(createStatCard("PRODUKSI HARI INI", prodHariIni, "potong", "Terbaru hari ini", Theme.GREEN));
        topCardsPanel.add(createStatCard("STOK KEDELAI", stokKedelai, "kg", "Sesuai gudang", Theme.WARNING));
        topCardsPanel.add(createStatCard("PENDAPATAN", "Rp " + pendapatan, "jt", "Bulan ini", Theme.GREEN));
        topCardsPanel.add(createStatCard("TAHU SIAP JUAL", tahuSiapJual, "potong", "Total semua jenis", Theme.TEXT_SECONDARY));

        // GRAFIK & STATUS STOK
        JPanel middlePanel = new JPanel(new BorderLayout(20, 0));
        middlePanel.setBackground(Theme.BG);
        middlePanel.setMinimumSize(new Dimension(800, 300));
        middlePanel.setPreferredSize(new Dimension(800, 300));
        middlePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));

        // --- Grafik Panel ---
        RoundedPanel chartPanel = new RoundedPanel(20, Theme.CARD);
        chartPanel.setLayout(new BorderLayout(0, 15));
        chartPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel chartHeader = new JPanel(new BorderLayout());
        chartHeader.setOpaque(false);
        JLabel chartTitle = new JLabel("Produksi 7 Hari Terakhir");
        chartTitle.setForeground(Theme.TEXT_PRIMARY);
        chartTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        chartHeader.add(chartTitle, BorderLayout.WEST);

        JComboBox<String> cbTimeframe = new JComboBox<>(new String[]{"1W", "1M", "3M", "ALL"});
        cbTimeframe.setBackground(Theme.BG);
        cbTimeframe.setForeground(Theme.TEXT_PRIMARY);
        cbTimeframe.setCursor(new Cursor(Cursor.HAND_CURSOR));
        chartHeader.add(cbTimeframe, BorderLayout.EAST);
        chartPanel.add(chartHeader, BorderLayout.NORTH);

        // RINGKASAN TOTAL & RATA-RATA)
        JPanel chartFooter = new JPanel(new BorderLayout());
        chartFooter.setOpaque(false);
        chartFooter.setBorder(new EmptyBorder(10, 0, 0, 0));

        JLabel lblTotalSummary = new JLabel("Total minggu ini: 0 potong");
        lblTotalSummary.setForeground(Theme.TEXT_PRIMARY);
        lblTotalSummary.setFont(new Font("SansSerif", Font.BOLD, 14));

        JLabel lblAvgSummary = new JLabel("Rata-rata: 0 potong/hari");
        lblAvgSummary.setForeground(Theme.TEXT_SECONDARY);
        lblAvgSummary.setFont(new Font("SansSerif", Font.PLAIN, 14));

        chartFooter.add(lblTotalSummary, BorderLayout.WEST);
        chartFooter.add(lblAvgSummary, BorderLayout.EAST);

        chartPanel.add(chartFooter, BorderLayout.SOUTH);

        // --- DATA GRAFIK DINAMIS ---
        final List<String> chartLabels = new ArrayList<>();
        final List<Integer> chartValues = new ArrayList<>();

        Runnable fetchChartData = () -> {
            chartLabels.clear();
            chartValues.clear();
            String selected = (String) cbTimeframe.getSelectedItem();
            String query = "";
            String timeText = "";
            int divisor = 1;

            if ("1W".equals(selected)) {
                chartTitle.setText("Produksi 7 Hari Terakhir");
                query = "SELECT DATE_FORMAT(tanggal, '%d %b') as label, SUM(hasil_tahu) as total FROM produksi WHERE tanggal >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) GROUP BY tanggal ORDER BY tanggal ASC";
                timeText = "minggu ini";
                divisor = 7;
            } else if ("1M".equals(selected)) {
                chartTitle.setText("Produksi 30 Hari Terakhir");
                query = "SELECT DATE_FORMAT(tanggal, '%d %b') as label, SUM(hasil_tahu) as total FROM produksi WHERE tanggal >= DATE_SUB(CURDATE(), INTERVAL 1 MONTH) GROUP BY tanggal ORDER BY tanggal ASC";
                timeText = "bulan ini";
                divisor = 30;
            } else if ("3M".equals(selected)) {
                chartTitle.setText("Produksi 3 Bulan Terakhir");
                query = "SELECT DATE_FORMAT(tanggal, '%b %Y') as label, SUM(hasil_tahu) as total FROM produksi WHERE tanggal >= DATE_SUB(CURDATE(), INTERVAL 3 MONTH) GROUP BY YEAR(tanggal), MONTH(tanggal) ORDER BY YEAR(tanggal), MONTH(tanggal) ASC";
                timeText = "3 bulan terakhir";
                divisor = 90;
            } else {
                chartTitle.setText("Total Produksi Keseluruhan");
                query = "SELECT DATE_FORMAT(tanggal, '%b %Y') as label, SUM(hasil_tahu) as total FROM produksi GROUP BY YEAR(tanggal), MONTH(tanggal) ORDER BY YEAR(tanggal), MONTH(tanggal) ASC";
                timeText = "keseluruhan";
            }

            int totalSum = 0;

            try {
                Connection conn = utils.DatabaseConfig.getKoneksi();
                Statement stmt = conn.createStatement();
                ResultSet rsChart = stmt.executeQuery(query);
                while (rsChart.next()) {
                    String label = rsChart.getString("label");
                    int total = rsChart.getInt("total");

                    chartLabels.add(label);
                    chartValues.add(total);
                    totalSum += total;
                }
            } catch (Exception e) {
                System.err.println("Gagal memuat data grafik: " + e.getMessage());
            }
            if (chartValues.isEmpty()) {
                chartLabels.add("-");
                chartValues.add(0);
            } else if ("ALL".equals(selected)) {
                // Untuk "ALL", asumsi 1 bulan = 30 hari untuk rata-rata harian
                divisor = Math.max(1, chartValues.size() * 30);
            }

            int avgPerDay = (divisor > 0) ? (totalSum / divisor) : 0;
            lblTotalSummary.setText("Total " + timeText + ": " + String.format(Locale.forLanguageTag("id-ID"), "%,d", totalSum) + " potong");
            lblAvgSummary.setText("Rata-rata: " + String.format(Locale.forLanguageTag("id-ID"), "%,d", avgPerDay) + " potong/hari");
        };
        fetchChartData.run();
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

                int n = chartValues.size();
                int maxVal = 1;
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
                int width = (chartWidth - (space * (n - 1))) / n;
                width = Math.max(2, Math.min(width, 40));

                int totalContentWidth = (width * n) + (space * (n - 1));
                int startX = paddingLeft + (chartWidth - totalContentWidth) / 2;

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

                int n = chartValues.size();
                int maxVal = 1;
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
                int numGridLines = 4;
                g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
                for (int i = 0; i <= numGridLines; i++) {
                    int y = paddingTop + chartHeight - (i * chartHeight / numGridLines);
                    int value = maxVal * i / numGridLines;
                    g2.setColor(new Color(Theme.BORDER.getRed(), Theme.BORDER.getGreen(), Theme.BORDER.getBlue(), 120));
                    g2.drawLine(paddingLeft, y, paddingLeft + chartWidth, y);
                    g2.setColor(Theme.TEXT_SECONDARY);
                    String yLabel = String.valueOf(value);
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(yLabel, paddingLeft - fm.stringWidth(yLabel) - 10, y + (fm.getAscent() / 2) - 2);
                }

                int space = n > 15 ? 4 : (n > 7 ? 8 : 15);
                int width = (chartWidth - (space * (n - 1))) / n;
                width = Math.max(2, Math.min(width, 40));
                int totalContentWidth = (width * n) + (space * (n - 1));
                int startX = paddingLeft + (chartWidth - totalContentWidth) / 2;
                for (int i = 0; i < n; i++) {
                    int barX = startX + (i * (width + space));
                    int scaledHeight = (int) (chartValues.get(i) * scale);
                    int barY = paddingTop + chartHeight - scaledHeight;
                    if (n <= 15 || i % 3 == 0 || i == n - 1) {
                        g2.setColor(Theme.TEXT_SECONDARY);
                        String xLabel = chartLabels.get(i);
                        FontMetrics fm = g2.getFontMetrics();
                        int labelX = barX + (width / 2) - (fm.stringWidth(xLabel) / 2);
                        g2.drawString(xLabel, labelX, paddingTop + chartHeight + 20);
                    }

                    if (i == hoveredBarIndex) {
                        g2.setColor(Theme.BLUE_ACCENT.brighter());
                    } else {
                        g2.setColor(Theme.BLUE_ACCENT);
                    }
                    g2.fillRect(barX, barY, width, scaledHeight);
                }

                if (hoveredBarIndex != -1) {
                    int barX = startX + (hoveredBarIndex * (width + space));
                    int scaledHeight = (int) (chartValues.get(hoveredBarIndex) * scale);
                    int barY = paddingTop + chartHeight - scaledHeight;
                    String valueText = chartValues.get(hoveredBarIndex) + " potong";
                    String dateText = chartLabels.get(hoveredBarIndex);
                    FontMetrics fm = g2.getFontMetrics();
                    int textWidth = Math.max(fm.stringWidth(valueText), fm.stringWidth(dateText));
                    g2.setColor(new Color(20, 20, 20, 220));
                    int tooltipX = barX + (width / 2) - (textWidth / 2) - 8;
                    int tooltipY = barY - 45;

                    if (tooltipY < 0) {
                        tooltipY = 0;
                    }

                    g2.fillRoundRect(tooltipX, tooltipY, textWidth + 16, 38, 8, 8);
                    g2.setColor(Color.WHITE);
                    g2.drawString(valueText, tooltipX + 8, tooltipY + 16);
                    g2.setColor(Theme.TEXT_SECONDARY);
                    g2.drawString(dateText, tooltipX + 8, tooltipY + 30);
                }
            }
        };
        mockChart.setBackground(Theme.CARD);
        chartPanel.add(mockChart, BorderLayout.CENTER);
        cbTimeframe.addActionListener(e -> {
            mockChart.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            fetchChartData.run();
            mockChart.repaint();
        });

        // --- STATUS STOK (DINAMIS) ---
        RoundedPanel statusPanel = new RoundedPanel(20, Theme.CARD);
        statusPanel.setPreferredSize(new Dimension(320, 0));
        statusPanel.setLayout(new BorderLayout());
        statusPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel statusTitle = new JLabel("Status Stok Bahan");
        statusTitle.setForeground(Theme.TEXT_PRIMARY);
        statusTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        statusPanel.add(statusTitle, BorderLayout.NORTH);

        JPanel statusListPanel = new JPanel();
        statusListPanel.setLayout(new BoxLayout(statusListPanel, BoxLayout.Y_AXIS));
        statusListPanel.setOpaque(false);
        statusListPanel.add(Box.createVerticalStrut(20));

        try {
            Connection conn = utils.DatabaseConfig.getKoneksi();
            Statement stmt = conn.createStatement();
            ResultSet rsBahan = stmt.executeQuery("SELECT * FROM bahan_baku ORDER BY id_bahan ASC");

            DecimalFormat df = new DecimalFormat("#.##");

            while (rsBahan.next()) {
                String nama = rsBahan.getString("nama");
                double minStok = rsBahan.getDouble("min_stok");
                double stok = rsBahan.getDouble("stok");
                String satuan = rsBahan.getString("satuan");

                String sub = "Min. stok: " + df.format(minStok) + " " + satuan;
                String val = df.format(stok) + " " + satuan;

                String badgeText;
                Color badgeColor;

                if (stok <= minStok / 2) {
                    badgeText = "Kritis";
                    badgeColor = Theme.RED;
                } else if (stok <= minStok) {
                    badgeText = "Rendah";
                    badgeColor = Theme.WARNING;
                } else {
                    badgeText = "Aman";
                    badgeColor = Theme.GREEN;
                }

                statusListPanel.add(createStatusRow(nama, sub, val, badgeText, badgeColor));
                statusListPanel.add(Box.createVerticalStrut(15));
            }
        } catch (Exception e) {
            System.err.println("Gagal memuat status bahan baku: " + e.getMessage());
            statusListPanel.add(new JLabel("Gagal memuat data dari server."));
        }

        JScrollPane statusScroll = new JScrollPane(statusListPanel);
        statusScroll.setOpaque(false);
        statusScroll.getViewport().setOpaque(false);
        statusScroll.setBorder(null);
        statusScroll.getVerticalScrollBar().setUnitIncrement(16);
        statusScroll.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        statusPanel.add(statusScroll, BorderLayout.CENTER);

        middlePanel.add(chartPanel, BorderLayout.CENTER);
        middlePanel.add(statusPanel, BorderLayout.EAST);

        // Panggil Class ActivityTable
        ActivityTable bottomPanel = new ActivityTable();

        dashboardContainer.add(topCardsPanel);
        dashboardContainer.add(Box.createVerticalStrut(20));
        dashboardContainer.add(middlePanel);
        dashboardContainer.add(Box.createVerticalStrut(20));
        dashboardContainer.add(bottomPanel);

        JScrollPane mainScrollPane = new JScrollPane(dashboardContainer);
        mainScrollPane.setBorder(null);
        mainScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainScrollPane.getViewport().setBackground(Theme.BG);
        mainScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mainScrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());

        mainContent.add(header, BorderLayout.NORTH);
        mainContent.add(mainScrollPane, BorderLayout.CENTER);

        return mainContent;
    }

    private JPanel createStatCard(String title, String value, String unit, String status, Color statusColor) {
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
        JLabel lblValue = new JLabel(value != null ? value : "0");
        lblValue.setForeground(Theme.TEXT_PRIMARY);
        lblValue.setFont(new Font("SansSerif", Font.BOLD, 32));
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
}
