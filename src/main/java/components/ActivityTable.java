package components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import utils.Theme;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Date;
import java.text.SimpleDateFormat;

public class ActivityTable extends RoundedPanel {

    private List<String[]> allActivityData = new ArrayList<>();
    private List<String[]> filteredData = new ArrayList<>();
    private int currentPage = 1;
    private int entriesPerPage = 5;

    private JPanel tableContentPanel;
    private JLabel lblPageInfo;
    private JButton btnPageNum;
    private JButton btnPrev;
    private JButton btnNext;
    private JTextField txtSearch;
    private JComboBox<String> cbEntries;

    public ActivityTable() {
        super(20, Theme.CARD);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));

        setPreferredSize(new Dimension(0, 390));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 390));
        loadDataDariDatabase();

        buildUI();
        updateTableModel();
    }

    private void loadDataDariDatabase() {
        allActivityData.clear();

        try {
            Connection conn = utils.DatabaseConfig.getKoneksi();
            Statement stmt = conn.createStatement();
            String query = "SELECT "
                    + "  p.tanggal, "
                    + "  p.batch, "
                    + "  p.hasil_tahu, "
                    + "  p.status, "
                    + "  u.nama AS nama_operator, "
                    + "  rp.jumlah AS jumlah_kedelai, "
                    + "  rp.satuan AS satuan_kedelai "
                    + "FROM produksi p "
                    + "JOIN users u ON p.id_user = u.id_user "
                    + "LEFT JOIN record_produksi rp ON p.id_produksi = rp.id_produksi AND rp.id_bahan = 1 "
                    + "ORDER BY p.tanggal DESC, p.id_produksi DESC";

            ResultSet rs = stmt.executeQuery(query);
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");

            while (rs.next()) {
                Date dbDate = rs.getDate("tanggal");
                String date = (dbDate != null) ? sdf.format(dbDate) : "-";
                String batch = rs.getString("batch");
                String hasil = rs.getInt("hasil_tahu") + " potong";
                String kedelai = "-";
                if (rs.getString("jumlah_kedelai") != null) {
                    double jumlah = rs.getDouble("jumlah_kedelai");
                    String satuan = rs.getString("satuan_kedelai");
                    if (jumlah == (long) jumlah) {
                        kedelai = String.format("%d %s", (long) jumlah, satuan);
                    } else {
                        kedelai = String.format("%s %s", jumlah, satuan);
                    }
                }

                String operator = rs.getString("nama_operator");
                if (operator != null && operator.contains(" ")) {
                    operator = operator.split(" ")[0];
                }
                String status = rs.getString("status");
                allActivityData.add(new String[]{date, batch, kedelai, hasil, operator, status});
            }

        } catch (Exception e) {
            System.err.println("Gagal menarik data dari database: " + e.getMessage());
            e.printStackTrace();
        }

        if (allActivityData.isEmpty()) {
            System.out.println("Tidak ada aktivitas produksi terbaru...");
        } else {
            filteredData.addAll(allActivityData);
        }
    }

    private void buildUI() {
        // --- Judul & Kontrol ---
        JPanel topHeader = new JPanel(new BorderLayout());
        topHeader.setOpaque(false);

        JLabel lblTitle = new JLabel("Aktivitas Produksi Terbaru");
        lblTitle.setForeground(Theme.TEXT_PRIMARY);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 16));

        JPanel controlRow = new JPanel(new BorderLayout());
        controlRow.setOpaque(false);
        controlRow.setBorder(new EmptyBorder(15, 0, 15, 0));

        JPanel leftControl = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        leftControl.setOpaque(false);
        JLabel lblShow = new JLabel("Tampilkan ");
        lblShow.setForeground(Theme.TEXT_SECONDARY);

        cbEntries = new JComboBox<>(new String[]{"5", "10", "25", "50"});
        cbEntries.setBackground(Theme.BG);
        cbEntries.setForeground(Theme.TEXT_PRIMARY);
        cbEntries.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cbEntries.addActionListener(e -> {
            entriesPerPage = Integer.parseInt((String) cbEntries.getSelectedItem());
            currentPage = 1;
            updateTableModel();
        });

        leftControl.add(lblShow);
        leftControl.add(cbEntries);
        leftControl.add(new JLabel(" data") {
            {
                setForeground(Theme.TEXT_SECONDARY);
            }
        });

        JPanel rightControl = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        rightControl.setOpaque(false);
        JLabel lblSearch = new JLabel("Cari: ");
        lblSearch.setForeground(Theme.TEXT_SECONDARY);

        txtSearch = new JTextField(15);
        txtSearch.setBackground(Theme.BG);
        txtSearch.setForeground(Theme.TEXT_PRIMARY);
        txtSearch.setCaretColor(Theme.TEXT_PRIMARY);
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
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

        topHeader.add(lblTitle, BorderLayout.NORTH);
        topHeader.add(controlRow, BorderLayout.CENTER);
        add(topHeader, BorderLayout.NORTH);

        // --- Tabel & Internal Scroll ---
        tableContentPanel = new JPanel();
        tableContentPanel.setLayout(new BoxLayout(tableContentPanel, BoxLayout.Y_AXIS));
        tableContentPanel.setOpaque(false);

        JPanel headerRowPanel = new JPanel(new GridLayout(1, 6, 0, 0));
        headerRowPanel.setOpaque(true);
        headerRowPanel.setBackground(Theme.CARD);
        headerRowPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER));

        String[] headers = {"Tanggal", "Batch", "Kedelai Digunakan", "Hasil Tahu", "Operator", "Status"};
        for (int i = 0; i < headers.length; i++) {
            JLabel l = new JLabel(headers[i]);
            l.setForeground(Theme.TEXT_SECONDARY);
            l.setFont(new Font("SansSerif", Font.PLAIN, 12));
            if (i < headers.length - 1) {
                l.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 0, 1, Theme.BORDER),
                        BorderFactory.createEmptyBorder(10, 5, 10, 5)
                ));
            } else {
                l.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
            }
            headerRowPanel.add(l);
        }

        JScrollPane tableScroll = new JScrollPane(tableContentPanel);
        tableScroll.setOpaque(false);
        tableScroll.getViewport().setOpaque(false);
        tableScroll.setBorder(null);

        // Header
        tableScroll.setColumnHeaderView(headerRowPanel);
        tableScroll.getColumnHeader().setOpaque(false);

        tableScroll.getVerticalScrollBar().setUnitIncrement(16);
        tableScroll.getVerticalScrollBar().setUI(new ModernScrollBarUI());

        tableScroll.setPreferredSize(new Dimension(0, 200));
        tableScroll.setMinimumSize(new Dimension(0, 200));
        tableScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        add(tableScroll, BorderLayout.CENTER);

        // --- Pagination ---
        JPanel paginationRow = new JPanel(new BorderLayout());
        paginationRow.setOpaque(false);
        paginationRow.setBorder(new EmptyBorder(15, 0, 0, 0));

        lblPageInfo = new JLabel();
        lblPageInfo.setForeground(Theme.TEXT_SECONDARY);
        lblPageInfo.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        btnPanel.setOpaque(false);

        btnPrev = new JButton("Sebelumnya");
        btnPrev.setBackground(Theme.BG);
        btnPrev.setForeground(Theme.TEXT_PRIMARY);
        btnPrev.setFocusPainted(false);
        btnPrev.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnPrev.addActionListener(e -> {
            currentPage--;
            updateTableModel();
        });

        btnPageNum = new JButton("1");
        btnPageNum.setBackground(Theme.BLUE_ACCENT);
        btnPageNum.setForeground(Color.WHITE);
        btnPageNum.setFocusPainted(false);
        btnPageNum.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnNext = new JButton("Selanjutnya");
        btnNext.setBackground(Theme.BG);
        btnNext.setForeground(Theme.TEXT_PRIMARY);
        btnNext.setFocusPainted(false);
        btnNext.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnNext.addActionListener(e -> {
            currentPage++;
            updateTableModel();
        });

        btnPanel.add(btnPrev);
        btnPanel.add(btnPageNum);
        btnPanel.add(btnNext);

        paginationRow.add(lblPageInfo, BorderLayout.WEST);
        paginationRow.add(btnPanel, BorderLayout.EAST);

        add(paginationRow, BorderLayout.SOUTH);
    }

    private void updateTableModel() {
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

        tableContentPanel.removeAll();

        if (totalData == 0) {
            JLabel lblEmpty = new JLabel("Data tidak ditemukan");
            lblEmpty.setForeground(Theme.TEXT_SECONDARY);
            lblEmpty.setBorder(new EmptyBorder(20, 0, 20, 0));
            lblEmpty.setAlignmentX(Component.CENTER_ALIGNMENT);
            tableContentPanel.add(lblEmpty);
        } else {
            for (int i = startIndex; i < endIndex; i++) {
                String[] row = filteredData.get(i);
                Color badgeColor = row[5].equals("Selesai") ? Theme.GREEN : Theme.WARNING;
                boolean isLastRow = (i == endIndex - 1);
                tableContentPanel.add(createTableRow(row[0], row[1], row[2], row[3], row[4], row[5], badgeColor, isLastRow));
            }
        }

        tableContentPanel.add(Box.createVerticalGlue());

        String infoText = String.format("Menampilkan %d sampai %d dari %d data",
                (totalData == 0 ? 0 : startIndex + 1), endIndex, totalData);
        lblPageInfo.setText(infoText);
        btnPageNum.setText(String.valueOf(currentPage));

        btnPrev.setEnabled(currentPage > 1);
        btnNext.setEnabled(currentPage < totalPages);

        tableContentPanel.revalidate();
        tableContentPanel.repaint();
    }

    private JPanel createTableRow(String date, String batch, String kedelai, String hasil, String operator, String status, Color badgeColor, boolean isLastRow) {
        JPanel row = new JPanel(new GridLayout(1, 6, 0, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        if (!isLastRow) {
            row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER));
        }

        row.add(createTableCell(date, Theme.TEXT_PRIMARY, true));
        row.add(createTableCell(batch, Theme.TEXT_SECONDARY, true));
        row.add(createTableCell(kedelai, Theme.TEXT_PRIMARY, true));
        row.add(createTableCell(hasil, Theme.TEXT_PRIMARY, true));
        row.add(createTableCell(operator, Theme.TEXT_PRIMARY, true));

        JPanel badgeWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 8));
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
                    BorderFactory.createMatteBorder(0, 0, 0, 1, Theme.BORDER),
                    BorderFactory.createEmptyBorder(10, 5, 10, 5)
            ));
        } else {
            l.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
        }
        return l;
    }
}
