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
import utils.Theme;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Dashboard extends JFrame {

    public Dashboard() {
        setTitle("TofuBase - Pabrik Tahu Sejahtera");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Theme.BG);

        add(new Sidebar(), BorderLayout.WEST);
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", new Locale("id", "ID"));
        JLabel headerDate = new JLabel(LocalDate.now().format(formatter));
        headerDate.setFont(new Font("SansSerif", Font.PLAIN, 14));
        headerDate.setForeground(Theme.TEXT_SECONDARY);
        new javax.swing.Timer(60_000, e -> headerDate.setText(LocalDate.now().format(formatter))).start();
        headerDate.setFont(new Font("SansSerif", Font.PLAIN, 14));
        headerDate.setForeground(Theme.TEXT_SECONDARY);
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

        // KARTU ATAS
        JPanel topCardsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        topCardsPanel.setBackground(Theme.BG);
        topCardsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        topCardsPanel.add(createStatCard("PRODUKSI HARI INI", "240", "potong", "▲ 12% dari kemarin", Theme.GREEN));
        topCardsPanel.add(createStatCard("STOK KEDELAI", "85", "kg", "▼ Perlu restok", Theme.RED));
        topCardsPanel.add(createStatCard("PENDAPATAN", "Rp 4,2", "Jt", "▲ 8% dari bulan lalu", Theme.GREEN));
        topCardsPanel.add(createStatCard("TAHU SIAP JUAL", "180", "potong", "Update 2 jam lalu", Theme.TEXT_SECONDARY));

        // GRAFIK & STATUS STOK
        JPanel middlePanel = new JPanel(new BorderLayout(20, 0));
        middlePanel.setBackground(Theme.BG);
        middlePanel.setMinimumSize(new Dimension(800, 260));
        middlePanel.setPreferredSize(new Dimension(800, 260));
        middlePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));

        // --- Grafik ---
        RoundedPanel chartPanel = new RoundedPanel(20, Theme.CARD);
        chartPanel.setLayout(new BorderLayout(0, 15));
        chartPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel chartHeader = new JPanel(new BorderLayout());
        chartHeader.setOpaque(false);
        JLabel chartTitle = new JLabel("Produksi 7 Hari Terakhir");
        chartTitle.setForeground(Theme.TEXT_PRIMARY);
        chartTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        chartHeader.add(chartTitle, BorderLayout.WEST);

        JComboBox<String> cbTimeframe = new JComboBox<>(new String[]{"1D", "1W", "1M", "3M", "1Y", "5Y", "ALL"});
        cbTimeframe.setSelectedItem("1W");
        cbTimeframe.setBackground(Theme.BG);
        cbTimeframe.setForeground(Theme.TEXT_PRIMARY);
        cbTimeframe.setCursor(new Cursor(Cursor.HAND_CURSOR));
        chartHeader.add(cbTimeframe, BorderLayout.EAST);
        chartPanel.add(chartHeader, BorderLayout.NORTH);

        final int[][] currentChartData = {{60, 80, 50, 90, 70, 85, 120}};

        // --- MOCK CHART & FITUR HOVER ---
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

            // Fungsi untuk deteksi apakah kursor ada di atas bar
            private int getHoveredBarIndex(int mouseX, int mouseY) {
                int[] heights = currentChartData[0];
                int n = heights.length;
                if (n == 0) {
                    return -1;
                }

                int maxPanelWidth = getWidth() - 40;
                int space = n > 15 ? 4 : (n > 7 ? 8 : 15);
                int width = (maxPanelWidth - (space * (n - 1))) / n;
                width = Math.max(2, Math.min(width, 40));

                int totalContentWidth = (width * n) + (space * (n - 1));
                int startX = 20 + (maxPanelWidth - totalContentWidth) / 2;

                for (int i = 0; i < n; i++) {
                    int barX = startX + (i * (width + space));
                    int barY = getHeight() - heights[i] - 10;
                    if (mouseX >= barX && mouseX <= barX + width && mouseY >= barY && mouseY <= barY + heights[i]) {
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

                int[] heights = currentChartData[0];
                int n = heights.length;
                if (n == 0) {
                    return;
                }

                int maxPanelWidth = getWidth() - 40;
                int space = n > 15 ? 4 : (n > 7 ? 8 : 15);
                int width = (maxPanelWidth - (space * (n - 1))) / n;
                width = Math.max(2, Math.min(width, 40));

                int totalContentWidth = (width * n) + (space * (n - 1));
                int startX = 20 + (maxPanelWidth - totalContentWidth) / 2;

                // Gambar semua bar
                for (int i = 0; i < n; i++) {
                    int barX = startX + (i * (width + space));
                    int barY = getHeight() - heights[i] - 10;

                    // Efek highlight (warna lebih cerah) saat bar disentuh
                    if (i == hoveredBarIndex) {
                        g2.setColor(Theme.BLUE_ACCENT.brighter());
                    } else {
                        g2.setColor(Theme.BLUE_ACCENT);
                    }
                    g2.fillRect(barX, barY, width, heights[i]);
                }

                // Gambar tooltip angka DI ATAS semua bar
                if (hoveredBarIndex != -1) {
                    int barX = startX + (hoveredBarIndex * (width + space));
                    int barY = getHeight() - heights[hoveredBarIndex] - 10;
                    String valueText = String.valueOf(heights[hoveredBarIndex]);

                    FontMetrics fm = g2.getFontMetrics();
                    int textWidth = fm.stringWidth(valueText);

                    g2.setColor(new Color(20, 20, 20, 220));
                    int tooltipX = barX + (width / 2) - (textWidth / 2) - 8;
                    int tooltipY = barY - 30;
                    g2.fillRoundRect(tooltipX, tooltipY, textWidth + 16, 24, 8, 8);

                    g2.setColor(Color.WHITE);
                    g2.drawString(valueText, tooltipX + 8, tooltipY + 16);
                }
            }
        };
        mockChart.setBackground(Theme.CARD);
        chartPanel.add(mockChart, BorderLayout.CENTER);

        cbTimeframe.addActionListener(e -> {
            String selected = (String) cbTimeframe.getSelectedItem();
            switch (selected) {
                case "1D":
                    chartTitle.setText("Produksi 1 Hari Terakhir");
                    currentChartData[0] = new int[]{70, 90, 110, 80, 130, 140, 120, 150};
                    break;
                case "1W":
                    chartTitle.setText("Produksi 7 Hari Terakhir");
                    currentChartData[0] = new int[]{60, 80, 50, 90, 70, 85, 120};
                    break;
                case "1M":
                    chartTitle.setText("Produksi 1 Bulan Terakhir");
                    currentChartData[0] = new int[]{40, 55, 45, 60, 75, 80, 95, 110, 90, 130, 120, 140, 135};
                    break;
                case "3M":
                    chartTitle.setText("Produksi 3 Bulan Terakhir");
                    currentChartData[0] = new int[]{30, 45, 60, 50, 70, 85, 75, 90, 110, 100, 120, 140};
                    break;
                case "1Y":
                    chartTitle.setText("Produksi 1 Tahun Terakhir");
                    currentChartData[0] = new int[]{40, 50, 65, 80, 95, 110, 105, 90, 120, 130, 145, 150};
                    break;
                case "5Y":
                    chartTitle.setText("Produksi 5 Tahun Terakhir");
                    currentChartData[0] = new int[]{60, 90, 110, 130, 150};
                    break;
                case "ALL":
                    chartTitle.setText("Total Produksi Keseluruhan");
                    currentChartData[0] = new int[]{30, 50, 40, 70, 60, 90, 85, 110, 130, 120, 140, 150};
                    break;
            }
            mockChart.repaint();
        });

        // --- Status Stok ---
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

        statusListPanel.add(createStatusRow("Kedelai", "Min. stok: 100 kg", "85 kg", "Kritis", Theme.RED));
        statusListPanel.add(Box.createVerticalStrut(15));
        statusListPanel.add(createStatusRow("Garam", "Min. stok: 5 kg", "18 kg", "Aman", Theme.GREEN));
        statusListPanel.add(Box.createVerticalStrut(15));
        statusListPanel.add(createStatusRow("Kayu bakar", "Min. stok: 50 ikat", "32 ikat", "Rendah", Theme.WARNING));
        statusListPanel.add(Box.createVerticalStrut(15));
        statusListPanel.add(createStatusRow("Kunyit", "Min. stok: 2 kg", "5 kg", "Aman", Theme.GREEN));
        statusListPanel.add(Box.createVerticalStrut(15));
        statusListPanel.add(createStatusRow("Plastik", "Min. 500 pcs", "800 pcs", "Aman", Theme.GREEN));

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
        card.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setForeground(Theme.TEXT_SECONDARY);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 10));

        JPanel valuePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        valuePanel.setBackground(Theme.CARD);
        valuePanel.setOpaque(false);
        JLabel lblValue = new JLabel(value);
        lblValue.setForeground(Theme.TEXT_PRIMARY);
        lblValue.setFont(new Font("SansSerif", Font.BOLD, 32));
        valuePanel.add(lblValue);
        valuePanel.add(new JLabel(unit) {
            {
                setForeground(Theme.TEXT_SECONDARY);
            }
        });

        JLabel lblStatus = new JLabel(status);
        lblStatus.setForeground(statusColor);
        lblStatus.setFont(new Font("SansSerif", Font.PLAIN, 12));

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
