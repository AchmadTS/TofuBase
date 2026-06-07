package com.tubes.tofubase.Dashboard;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

import java.util.List;

public class Dashboard extends JFrame {

    // Palet Warna
    private static final Color COLOR_BG = new Color(30, 30, 30);
    private static final Color COLOR_SIDEBAR = new Color(40, 40, 40);
    private static final Color COLOR_CARD = new Color(45, 45, 45);
    private static final Color COLOR_TEXT_PRIMARY = Color.WHITE;
    private static final Color COLOR_TEXT_SECONDARY = new Color(170, 170, 170);
    private static final Color COLOR_BLUE_ACCENT = new Color(40, 120, 255);
    private static final Color COLOR_GREEN = new Color(46, 204, 113);
    private static final Color COLOR_RED = new Color(231, 76, 60);
    private static final Color COLOR_WARNING = new Color(243, 156, 18);
    private static final Color COLOR_BORDER = new Color(75, 75, 75);

    // --- VARIABEL UNTUK PAGINATION & SEARCH ---
    private List<String[]> allActivityData = new ArrayList<>();
    private List<String[]> filteredData = new ArrayList<>();
    private int currentPage = 1;
    private int entriesPerPage = 5;

    // Komponen UI yang perlu diakses global
    private JPanel tableContentPanel;
    private JPanel headerRowPanel;
    private JLabel lblPageInfo;
    private JButton btnPageNum;
    private JButton btnPrev;
    private JButton btnNext;
    private JTextField txtSearch;
    private JComboBox<String> cbEntries;

    public Dashboard() {
        setTitle("TofuBase - Pabrik Tahu Sejahtera");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(COLOR_BG);

        generateDummyData(); // Menyiapkan data sebelum UI digambar

        add(createSidebar(), BorderLayout.WEST);
        add(createMainContent(), BorderLayout.CENTER);

        updateTableModel(); // Menampilkan data pertama kali
    }

    // --- GENERATE DUMMY DATA ---
    private void generateDummyData() {
        // Membuat 24 data dummy agar pagination bisa diuji
        for (int i = 1; i <= 24; i++) {
            String date = (10 + (i % 15)) + " Mar 2026";
            String batch = String.format("#B-%03d", 40 + i);
            String kedelai = (20 + (i % 10)) + " kg";
            String hasil = (180 + (i % 50)) + " potong";
            String operator = (i % 3 == 0) ? "Bu Wati" : "Pak Slamet";
            String status = (i == 1) ? "Proses" : "Selesai";
            allActivityData.add(new String[]{date, batch, kedelai, hasil, operator, status});
        }
        filteredData.addAll(allActivityData);
    }

    // --- LOGIKA UPDATE TABEL (SEARCH & PAGINATION) ---
    private void updateTableModel() {
        // 1. Lakukan Filtering
        String keyword = txtSearch.getText().toLowerCase();
        filteredData.clear();
        for (String[] row : allActivityData) {
            boolean match = false;
            for (String cell : row) {
                if (cell.toLowerCase().contains(keyword)) {
                    match = true;
                    break;
                }
            }
            if (match) {
                filteredData.add(row);
            }
        }

        // 2. Hitung Pagination
        int totalData = filteredData.size();
        int totalPages = (int) Math.ceil((double) totalData / entriesPerPage);

        if (totalPages == 0) {
            totalPages = 1;
        }
        if (currentPage > totalPages) {
            currentPage = totalPages;
        }
        if (currentPage < 1) {
            currentPage = 1;
        }

        int startIndex = (currentPage - 1) * entriesPerPage;
        int endIndex = Math.min(startIndex + entriesPerPage, totalData);

        // 3. Render Ulang UI Tabel
        tableContentPanel.removeAll();
        tableContentPanel.add(headerRowPanel); // Pasang header kolom

        if (totalData == 0) {
            JLabel lblEmpty = new JLabel("Data tidak ditemukan");
            lblEmpty.setForeground(COLOR_TEXT_SECONDARY);
            lblEmpty.setBorder(new EmptyBorder(20, 0, 20, 0));
            lblEmpty.setAlignmentX(Component.CENTER_ALIGNMENT);
            tableContentPanel.add(lblEmpty);
        } else {
            for (int i = startIndex; i < endIndex; i++) {
                String[] row = filteredData.get(i);
                Color badgeColor = row[5].equals("Selesai") ? COLOR_GREEN : COLOR_WARNING;
                boolean isLastRow = (i == endIndex - 1);
                tableContentPanel.add(createTableRow(row[0], row[1], row[2], row[3], row[4], row[5], badgeColor, isLastRow));
            }
        }

        // 4. Update Info & Tombol
        String infoText = String.format("Menampilkan %d sampai %d dari %d data",
                (totalData == 0 ? 0 : startIndex + 1), endIndex, totalData);
        lblPageInfo.setText(infoText);
        btnPageNum.setText(String.valueOf(currentPage));

        btnPrev.setEnabled(currentPage > 1);
        btnNext.setEnabled(currentPage < totalPages);

        tableContentPanel.revalidate();
        tableContentPanel.repaint();
    }

    // --- 1. SIDEBAR ---
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setBackground(COLOR_SIDEBAR);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("TofuBase");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(COLOR_TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Pabrik Tahu Sejahtera");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitle.setForeground(COLOR_TEXT_SECONDARY);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(title);
        sidebar.add(subtitle);
        sidebar.add(Box.createVerticalStrut(40));

        JLabel menuLabel = new JLabel("MENU UTAMA");
        menuLabel.setFont(new Font("SansSerif", Font.BOLD, 10));
        menuLabel.setForeground(COLOR_TEXT_SECONDARY);
        menuLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(menuLabel);
        sidebar.add(Box.createVerticalStrut(10));

        RoundedPanel activeMenu = new RoundedPanel(15, new Color(220, 235, 255));
        activeMenu.setLayout(new BoxLayout(activeMenu, BoxLayout.X_AXIS));
        activeMenu.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        activeMenu.setBorder(new EmptyBorder(0, 15, 0, 15));
        activeMenu.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel dashboardIcon = new JLabel("⊞", SwingConstants.CENTER);
        dashboardIcon.setFont(new Font("SansSerif", Font.BOLD, 18));
        dashboardIcon.setForeground(COLOR_BLUE_ACCENT);
        dashboardIcon.setPreferredSize(new Dimension(24, 24));
        dashboardIcon.setMaximumSize(new Dimension(24, 24));

        JLabel dashboardLabel = new JLabel("Dashboard");
        dashboardLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        dashboardLabel.setForeground(COLOR_BLUE_ACCENT);

        activeMenu.add(dashboardIcon);
        activeMenu.add(Box.createHorizontalStrut(10));
        activeMenu.add(dashboardLabel);
        sidebar.add(activeMenu);

        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(createMenuItem("○", "Bahan Baku"));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(createMenuItem("≡", "Produksi"));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(createMenuItem("◇", "Stok & Distribusi"));

        sidebar.add(Box.createVerticalStrut(30));

        JLabel keuanganLabel = new JLabel("KEUANGAN");
        keuanganLabel.setFont(new Font("SansSerif", Font.BOLD, 10));
        keuanganLabel.setForeground(COLOR_TEXT_SECONDARY);
        keuanganLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(keuanganLabel);
        sidebar.add(Box.createVerticalStrut(10));

        sidebar.add(createMenuItem("↗", "Laporan Keuangan"));

        sidebar.add(Box.createVerticalGlue());

        JPanel userPanel = new JPanel(new BorderLayout(10, 0));
        userPanel.setOpaque(false);
        userPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        userPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        RoundedPanel avatar = new RoundedPanel(36, new Color(150, 200, 255));
        avatar.setPreferredSize(new Dimension(36, 36));
        avatar.setMinimumSize(new Dimension(36, 36));
        avatar.setMaximumSize(new Dimension(36, 36));
        avatar.setLayout(new BorderLayout());

        JLabel initLabel = new JLabel("OW", SwingConstants.CENTER);
        initLabel.setForeground(COLOR_SIDEBAR);
        initLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        avatar.add(initLabel, BorderLayout.CENTER);

        JPanel userInfo = new JPanel(new GridLayout(2, 1));
        userInfo.setOpaque(false);
        JLabel userName = new JLabel("Pak Budi");
        userName.setForeground(COLOR_TEXT_PRIMARY);
        userName.setFont(new Font("SansSerif", Font.BOLD, 14));
        JLabel userRole = new JLabel("Owner");
        userRole.setForeground(COLOR_TEXT_SECONDARY);
        userRole.setFont(new Font("SansSerif", Font.PLAIN, 12));
        userInfo.add(userName);
        userInfo.add(userRole);

        userPanel.add(avatar, BorderLayout.WEST);
        userPanel.add(userInfo, BorderLayout.CENTER);

        sidebar.add(userPanel);

        return sidebar;
    }

    private JPanel createMenuItem(String icon, String text) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        panel.setBorder(new EmptyBorder(0, 15, 0, 15));

        JLabel lblIcon = new JLabel(icon, SwingConstants.CENTER);
        lblIcon.setForeground(COLOR_TEXT_SECONDARY);
        lblIcon.setFont(new Font("SansSerif", Font.PLAIN, 16));
        lblIcon.setPreferredSize(new Dimension(24, 24));
        lblIcon.setMaximumSize(new Dimension(24, 24));

        JLabel lblText = new JLabel(text);
        lblText.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblText.setForeground(COLOR_TEXT_SECONDARY);

        panel.add(lblIcon);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(lblText);
        return panel;
    }

// --- MAIN CONTENT & SCROLL PANE ---
    private JPanel createMainContent() {
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(COLOR_BG);

        // HEADER
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_BG);
        header.setBorder(new EmptyBorder(20, 30, 10, 30));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setBackground(COLOR_BG);
        JLabel headerTitle = new JLabel("Dashboard");
        headerTitle.setFont(new Font("SansSerif", Font.BOLD, 28));
        headerTitle.setForeground(COLOR_TEXT_PRIMARY);
        JLabel headerDate = new JLabel("Minggu, 15 Maret 2026");
        headerDate.setFont(new Font("SansSerif", Font.PLAIN, 14));
        headerDate.setForeground(COLOR_TEXT_SECONDARY);
        titlePanel.add(headerTitle);
        titlePanel.add(headerDate);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(COLOR_BG);
        JButton btnExport = new JButton("Export PDF");
        JButton btnAdd = new JButton("+ Tambah Data");
        buttonPanel.add(btnExport);
        buttonPanel.add(btnAdd);

        header.add(titlePanel, BorderLayout.WEST);
        header.add(buttonPanel, BorderLayout.EAST);

        // CONTAINER UNTUK SEMUA ELEMEN DASHBOARD
        JPanel dashboardContainer = new JPanel();
        dashboardContainer.setLayout(new BoxLayout(dashboardContainer, BoxLayout.Y_AXIS));
        dashboardContainer.setBackground(COLOR_BG);
        dashboardContainer.setBorder(new EmptyBorder(10, 30, 30, 30));

        // KARTU ATAS
        JPanel topCardsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        topCardsPanel.setBackground(COLOR_BG);
        topCardsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        topCardsPanel.add(createStatCard("PRODUKSI HARI INI", "240", "potong", "▲ 12% dari kemarin", COLOR_GREEN));
        topCardsPanel.add(createStatCard("STOK KEDELAI", "85", "kg", "▼ Perlu restok", COLOR_RED));
        topCardsPanel.add(createStatCard("PENDAPATAN", "Rp 4,2", "Jt", "▲ 8% dari bulan lalu", COLOR_GREEN));
        topCardsPanel.add(createStatCard("TAHU SIAP JUAL", "180", "potong", "Update 2 jam lalu", COLOR_TEXT_SECONDARY));

        // GRAFIK & STATUS STOK
        JPanel middlePanel = new JPanel(new BorderLayout(20, 0));
        middlePanel.setBackground(COLOR_BG);
        middlePanel.setMinimumSize(new Dimension(800, 260));
        middlePanel.setPreferredSize(new Dimension(800, 260));
        middlePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));

        // --- Grafik dengan Dropdown Filter ---
        RoundedPanel chartPanel = new RoundedPanel(20, COLOR_CARD);
        chartPanel.setLayout(new BorderLayout(0, 15));
        chartPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header Grafik
        JPanel chartHeader = new JPanel(new BorderLayout());
        chartHeader.setOpaque(false);

        JLabel chartTitle = new JLabel("Produksi 7 Hari Terakhir");
        chartTitle.setForeground(COLOR_TEXT_PRIMARY);
        chartTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        chartHeader.add(chartTitle, BorderLayout.WEST);

        // Dropdown Timeframe
        String[] timeframes = {"1D", "1W", "1M", "3M", "1Y", "5Y", "ALL"};
        JComboBox<String> cbTimeframe = new JComboBox<>(timeframes);
        cbTimeframe.setSelectedItem("1W"); // Default
        cbTimeframe.setBackground(COLOR_BG);
        cbTimeframe.setForeground(COLOR_TEXT_PRIMARY);
        cbTimeframe.setFocusable(false);
        chartHeader.add(cbTimeframe, BorderLayout.EAST);

        chartPanel.add(chartHeader, BorderLayout.NORTH);

        final int[][] currentChartData = {{60, 80, 50, 90, 70, 85, 120}};
        JPanel mockChart = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_BLUE_ACCENT);

                int[] heights = currentChartData[0];
                int n = heights.length;
                if (n == 0) {
                    return;
                }
                
                int maxPanelWidth = getWidth() - 40;
                int space = n > 15 ? 4 : (n > 7 ? 8 : 15);
                int width = (maxPanelWidth - (space * (n - 1))) / n;

                if (width < 2) {
                    width = 2;
                }
                if (width > 40) {
                    width = 40;
                }
                int totalContentWidth = (width * n) + (space * (n - 1));
                int startX = 20 + (maxPanelWidth - totalContentWidth) / 2;

                for (int i = 0; i < n; i++) {
                    g2.fillRect(startX + (i * (width + space)), getHeight() - heights[i] - 10, width, heights[i]);
                }
            }
        };
        mockChart.setBackground(COLOR_CARD);
        chartPanel.add(mockChart, BorderLayout.CENTER);

        // Dropdown: Mengubah Teks & Data, lalu me-Refresh Grafik
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
        RoundedPanel statusPanel = new RoundedPanel(20, COLOR_CARD);
        statusPanel.setPreferredSize(new Dimension(320, 0));
        statusPanel.setLayout(new BorderLayout());
        statusPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel statusTitle = new JLabel("Status Stok Bahan");
        statusTitle.setForeground(COLOR_TEXT_PRIMARY);
        statusTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        statusPanel.add(statusTitle, BorderLayout.NORTH);

        JPanel statusListPanel = new JPanel();
        statusListPanel.setLayout(new BoxLayout(statusListPanel, BoxLayout.Y_AXIS));
        statusListPanel.setOpaque(false);
        statusListPanel.add(Box.createVerticalStrut(20));

        statusListPanel.add(createStatusRow("Kedelai", "Min. stok: 100 kg", "85 kg", "Kritis", COLOR_RED));
        statusListPanel.add(Box.createVerticalStrut(15));
        statusListPanel.add(createStatusRow("Garam", "Min. stok: 5 kg", "18 kg", "Aman", COLOR_GREEN));
        statusListPanel.add(Box.createVerticalStrut(15));
        statusListPanel.add(createStatusRow("Kayu bakar", "Min. stok: 50 ikat", "32 ikat", "Rendah", COLOR_WARNING));
        statusListPanel.add(Box.createVerticalStrut(15));
        statusListPanel.add(createStatusRow("Kunyit", "Min. stok: 2 kg", "5 kg", "Aman", COLOR_GREEN));
        statusListPanel.add(Box.createVerticalStrut(15));
        statusListPanel.add(createStatusRow("Plastik", "Min. 500 pcs", "800 pcs", "Aman", COLOR_GREEN));

        JScrollPane statusScroll = new JScrollPane(statusListPanel);
        statusScroll.setOpaque(false);
        statusScroll.getViewport().setOpaque(false);
        statusScroll.setBorder(null);
        statusScroll.getVerticalScrollBar().setUnitIncrement(16);
        statusScroll.getVerticalScrollBar().setUI(new ModernScrollBarUI());

        statusPanel.add(statusScroll, BorderLayout.CENTER);

        middlePanel.add(chartPanel, BorderLayout.CENTER);
        middlePanel.add(statusPanel, BorderLayout.EAST);

        // TABEL AKTIVITAS
        JPanel bottomPanel = createActivityTable();

        dashboardContainer.add(topCardsPanel);
        dashboardContainer.add(Box.createVerticalStrut(20));
        dashboardContainer.add(middlePanel);
        dashboardContainer.add(Box.createVerticalStrut(20));
        dashboardContainer.add(bottomPanel);

        JScrollPane scrollPane = new JScrollPane(dashboardContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(COLOR_BG);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());

        mainContent.add(header, BorderLayout.NORTH);
        mainContent.add(scrollPane, BorderLayout.CENTER);

        return mainContent;
    }

    // --- 3. KOMPONEN TABEL AKTIVITAS ---
    private JPanel createActivityTable() {
        RoundedPanel panel = new RoundedPanel(20, COLOR_CARD);
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // PERBAIKAN: Jangan membatasi Maximum Size terlalu ketat agar bisa merenggang mengikuti isi tabel
        panel.setMinimumSize(new Dimension(800, 300));

        // --- BAGIAN ATAS (Judul) ---
        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);
        JLabel lblTitle = new JLabel("Aktivitas Produksi Terbaru");
        lblTitle.setForeground(COLOR_TEXT_PRIMARY);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleRow.add(lblTitle, BorderLayout.WEST);

        // --- BAGIAN KONTROL (Search & Show Entries) ---
        JPanel controlRow = new JPanel(new BorderLayout());
        controlRow.setOpaque(false);
        controlRow.setBorder(new EmptyBorder(15, 0, 15, 0));

        JPanel leftControl = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        leftControl.setOpaque(false);
        JLabel lblShow = new JLabel("Tampilkan ");
        lblShow.setForeground(COLOR_TEXT_SECONDARY);

        cbEntries = new JComboBox<>(new String[]{"5", "10", "25", "50"});
        cbEntries.setBackground(COLOR_BG);
        cbEntries.setForeground(COLOR_TEXT_PRIMARY);

        // Listener Combobox
        cbEntries.addActionListener(e -> {
            entriesPerPage = Integer.parseInt((String) cbEntries.getSelectedItem());
            currentPage = 1;
            updateTableModel();
        });

        JLabel lblData = new JLabel(" data");
        lblData.setForeground(COLOR_TEXT_SECONDARY);
        leftControl.add(lblShow);
        leftControl.add(cbEntries);
        leftControl.add(lblData);

        JPanel rightControl = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        rightControl.setOpaque(false);
        JLabel lblSearch = new JLabel("Cari: ");
        lblSearch.setForeground(COLOR_TEXT_SECONDARY);

        txtSearch = new JTextField(15);
        txtSearch.setBackground(COLOR_BG);
        txtSearch.setForeground(COLOR_TEXT_PRIMARY);
        txtSearch.setCaretColor(COLOR_TEXT_PRIMARY);
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        // Listener Search (Real-time)
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                search();
            }

            public void removeUpdate(DocumentEvent e) {
                search();
            }

            public void changedUpdate(DocumentEvent e) {
                search();
            }

            private void search() {
                currentPage = 1;
                updateTableModel();
            }
        });

        rightControl.add(lblSearch);
        rightControl.add(txtSearch);

        controlRow.add(leftControl, BorderLayout.WEST);
        controlRow.add(rightControl, BorderLayout.EAST);

        JPanel topHeader = new JPanel(new BorderLayout());
        topHeader.setOpaque(false);
        topHeader.add(titleRow, BorderLayout.NORTH);
        topHeader.add(controlRow, BorderLayout.CENTER);
        panel.add(topHeader, BorderLayout.NORTH);

        // --- BAGIAN TENGAH (Isi Tabel) ---
        tableContentPanel = new JPanel();
        tableContentPanel.setLayout(new BoxLayout(tableContentPanel, BoxLayout.Y_AXIS));
        tableContentPanel.setOpaque(false);

        // Header Kolom (Disimpan di class level agar mudah dipakai ulang)
        headerRowPanel = new JPanel(new GridLayout(1, 6, 0, 0));
        headerRowPanel.setOpaque(false);
        headerRowPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER));

        String[] headers = {"Tanggal", "Batch", "Kedelai Digunakan", "Hasil Tahu", "Operator", "Status"};
        for (int i = 0; i < headers.length; i++) {
            JLabel l = new JLabel(headers[i]);
            l.setForeground(COLOR_TEXT_SECONDARY);
            l.setFont(new Font("SansSerif", Font.PLAIN, 12));
            if (i < headers.length - 1) {
                l.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 0, 1, COLOR_BORDER),
                        BorderFactory.createEmptyBorder(10, 5, 10, 5)
                ));
            } else {
                l.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
            }
            headerRowPanel.add(l);
        }

        panel.add(tableContentPanel, BorderLayout.CENTER);

        // --- BAGIAN BAWAH (Pagination Controls) ---
        JPanel paginationRow = new JPanel(new BorderLayout());
        paginationRow.setOpaque(false);
        paginationRow.setBorder(new EmptyBorder(15, 0, 0, 0));

        lblPageInfo = new JLabel();
        lblPageInfo.setForeground(COLOR_TEXT_SECONDARY);
        lblPageInfo.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        btnPanel.setOpaque(false);

        btnPrev = new JButton("Sebelumnya");
        btnPrev.setBackground(COLOR_BG);
        btnPrev.setForeground(COLOR_TEXT_PRIMARY);
        btnPrev.setFocusPainted(false);
        btnPrev.addActionListener(e -> {
            currentPage--;
            updateTableModel();
        });

        btnPageNum = new JButton("1");
        btnPageNum.setBackground(COLOR_BLUE_ACCENT);
        btnPageNum.setForeground(Color.WHITE);
        btnPageNum.setFocusPainted(false);

        btnNext = new JButton("Selanjutnya");
        btnNext.setBackground(COLOR_BG);
        btnNext.setForeground(COLOR_TEXT_PRIMARY);
        btnNext.setFocusPainted(false);
        btnNext.addActionListener(e -> {
            currentPage++;
            updateTableModel();
        });

        btnPanel.add(btnPrev);
        btnPanel.add(btnPageNum);
        btnPanel.add(btnNext);

        paginationRow.add(lblPageInfo, BorderLayout.WEST);
        paginationRow.add(btnPanel, BorderLayout.EAST);

        panel.add(paginationRow, BorderLayout.SOUTH);

        return panel;
    }

    // --- HELPER TABEL & STATS ---
    private JPanel createTableRow(String date, String batch, String kedelai, String hasil, String operator, String status, Color badgeColor, boolean isLastRow) {
        JPanel row = new JPanel(new GridLayout(1, 6, 0, 0));
        row.setOpaque(false);

        if (!isLastRow) {
            row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER));
        }

        row.add(createTableCell(date, COLOR_TEXT_PRIMARY, true));
        row.add(createTableCell(batch, COLOR_TEXT_SECONDARY, true));
        row.add(createTableCell(kedelai, COLOR_TEXT_PRIMARY, true));
        row.add(createTableCell(hasil, COLOR_TEXT_PRIMARY, true));
        row.add(createTableCell(operator, COLOR_TEXT_PRIMARY, true));

        JPanel badgeWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 12));
        badgeWrapper.setOpaque(false);

        JLabel lblBadge = new JLabel(status, SwingConstants.CENTER);
        lblBadge.setOpaque(true);
        lblBadge.setBackground(new Color(0, 0, 0, 0));
        lblBadge.setForeground(badgeColor);
        lblBadge.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblBadge.setBorder(new EmptyBorder(4, 10, 4, 10));

        RoundedPanel pillPanel = new RoundedPanel(12, new Color(badgeColor.getRed(), badgeColor.getGreen(), badgeColor.getBlue(), 40));
        pillPanel.setLayout(new BorderLayout());
        pillPanel.add(lblBadge, BorderLayout.CENTER);

        badgeWrapper.add(pillPanel);
        row.add(badgeWrapper);

        return row;
    }

    private JLabel createTableCell(String text, Color color, boolean addRightBorder) {
        JLabel l = new JLabel(text);
        l.setForeground(color);
        l.setFont(new Font("SansSerif", Font.PLAIN, 13));

        if (addRightBorder) {
            l.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 0, 1, COLOR_BORDER),
                    BorderFactory.createEmptyBorder(12, 5, 12, 5)
            ));
        } else {
            l.setBorder(BorderFactory.createEmptyBorder(12, 5, 12, 5));
        }
        return l;
    }

    private JPanel createStatCard(String title, String value, String unit, String status, Color statusColor) {
        RoundedPanel card = new RoundedPanel(20, COLOR_CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setForeground(COLOR_TEXT_SECONDARY);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 10));

        JPanel valuePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        valuePanel.setBackground(COLOR_CARD);
        valuePanel.setOpaque(false);
        JLabel lblValue = new JLabel(value);
        lblValue.setForeground(COLOR_TEXT_PRIMARY);
        lblValue.setFont(new Font("SansSerif", Font.BOLD, 32));
        JLabel lblUnit = new JLabel(unit);
        lblUnit.setForeground(COLOR_TEXT_SECONDARY);
        valuePanel.add(lblValue);
        valuePanel.add(lblUnit);

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
        row.setBackground(COLOR_CARD);

        JPanel leftPanel = new JPanel(new GridLayout(2, 1));
        leftPanel.setBackground(COLOR_CARD);
        JLabel lblName = new JLabel(name);
        lblName.setForeground(COLOR_TEXT_PRIMARY);
        lblName.setFont(new Font("SansSerif", Font.BOLD, 14));
        JLabel lblSub = new JLabel(sub);
        lblSub.setForeground(COLOR_TEXT_SECONDARY);
        lblSub.setFont(new Font("SansSerif", Font.PLAIN, 10));
        leftPanel.add(lblName);
        leftPanel.add(lblSub);

        JPanel rightPanel = new JPanel(new GridLayout(2, 1));
        rightPanel.setBackground(COLOR_CARD);
        JLabel lblVal = new JLabel(val, SwingConstants.RIGHT);
        lblVal.setForeground(badgeColor);
        lblVal.setFont(new Font("SansSerif", Font.BOLD, 14));

        RoundedPanel badge = new RoundedPanel(10, new Color(badgeColor.getRed(), badgeColor.getGreen(), badgeColor.getBlue(), 50));
        badge.setLayout(new BorderLayout());
        JLabel lblBadgeText = new JLabel(badgeText, SwingConstants.CENTER);
        lblBadgeText.setForeground(badgeColor);
        lblBadgeText.setFont(new Font("SansSerif", Font.BOLD, 10));
        badge.add(lblBadgeText, BorderLayout.CENTER);

        rightPanel.add(lblVal);
        rightPanel.add(badge);

        row.add(leftPanel, BorderLayout.WEST);
        row.add(rightPanel, BorderLayout.EAST);
        return row;
    }

    // --- CUSTOM CLASS: ROUNDED PANEL ---
    class RoundedPanel extends JPanel {

        private int radius;

        public RoundedPanel(int radius, Color bgColor) {
            super();
            this.radius = radius;
            setOpaque(false);
            setBackground(bgColor);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, radius, radius));
            g2.dispose();
        }
    }

    // --- CUSTOM CLASS: MODERN SCROLLBAR UI ---
    // Memodifikasi desain bawaan scrollbar Windows/Java agar tipis dan elegan
    class ModernScrollBarUI extends BasicScrollBarUI {

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            g.setColor(COLOR_BG); // Warna latar belakang lintasan
            g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(100, 100, 100)); // Warna abu-abu elegan untuk scrollbar
            // Membuat thumb lebih tipis (padding 4px kiri-kanan) dan melengkung
            g2.fillRoundRect(thumbBounds.x + 4, thumbBounds.y + 2, thumbBounds.width - 8, thumbBounds.height - 4, 8, 8);
            g2.dispose();
        }

        // Menghilangkan tombol panah atas/bawah bawaan Java
        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createZeroButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createZeroButton();
        }

        private JButton createZeroButton() {
            JButton jbutton = new JButton();
            jbutton.setPreferredSize(new Dimension(0, 0));
            jbutton.setMinimumSize(new Dimension(0, 0));
            jbutton.setMaximumSize(new Dimension(0, 0));
            return jbutton;
        }
    }
}
