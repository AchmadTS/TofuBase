package components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.List;
import java.util.Locale;
import utils.Theme;

public class ActivityTable extends RoundedPanel {
    public interface DataProvider {
        int getTotalRowCount(String keyword);

        List<String[]> getPageData(int limit, int offset, String keyword);
    }

    private String title;
    private String[] headers;
    private int statusColIndex;
    private DataProvider dataProvider;
    private int currentPage = 1;
    private int entriesPerPage = 5;
    private int totalData = 0;
    private JPanel tableContentPanel;
    private JLabel lblPageInfo;
    private JButton btnPageNum, btnPrev, btnNext;
    private JTextField txtSearch;
    private JComboBox<String> cbEntries;

    public ActivityTable(String title, String[] headers, int statusColIndex, DataProvider dataProvider) {
        super(20, Theme.CARD);
        this.title = title;
        this.headers = headers;
        this.statusColIndex = statusColIndex;
        this.dataProvider = dataProvider;

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setMinimumSize(new Dimension(0, 390));
        buildUI();
        updateTableModel();
    }

    private void buildUI() {
        JPanel topHeader = new JPanel(new BorderLayout());
        topHeader.setOpaque(false);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setForeground(Theme.TEXT_PRIMARY);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 16));

        JPanel controlRow = new JPanel(new BorderLayout());
        controlRow.setOpaque(false);
        controlRow.setBorder(new EmptyBorder(15, 0, 15, 0));

        JPanel leftControl = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        leftControl.setOpaque(false);
        JLabel lblShow = new JLabel("Tampilkan ");
        lblShow.setForeground(Theme.TEXT_SECONDARY);

        cbEntries = new JComboBox<>(new String[] { "5", "10", "25", "50" });
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
        txtSearch.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Theme.BORDER),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
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

        // --- Grid Tabel sesuai jumlah Header ---
        tableContentPanel = new JPanel();
        tableContentPanel.setLayout(new BoxLayout(tableContentPanel, BoxLayout.Y_AXIS));
        tableContentPanel.setOpaque(false);

        int cols = headers.length;
        JPanel headerRowPanel = new JPanel(new GridLayout(1, cols, 0, 0));
        headerRowPanel.setOpaque(true);
        headerRowPanel.setBackground(Theme.CARD);
        headerRowPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER));

        for (int i = 0; i < cols; i++) {
            JLabel l = new JLabel(headers[i]);
            l.setForeground(Theme.TEXT_SECONDARY);
            l.setFont(new Font("SansSerif", Font.PLAIN, 12));
            if (i < cols - 1) {
                l.setBorder(
                        BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Theme.BORDER),
                                BorderFactory.createEmptyBorder(10, 5, 10, 5)));
            } else {
                l.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
            }
            headerRowPanel.add(l);
        }

        JScrollPane tableScroll = new JScrollPane(tableContentPanel);
        tableScroll.setOpaque(false);
        tableScroll.getViewport().setOpaque(false);
        tableScroll.setBorder(null);
        tableScroll.setColumnHeaderView(headerRowPanel);
        tableScroll.getColumnHeader().setOpaque(false);
        tableScroll.getVerticalScrollBar().setUnitIncrement(16);
        tableScroll.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        tableScroll.setPreferredSize(new Dimension(0, 200));
        tableScroll.setMinimumSize(new Dimension(0, 200));
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
        String keyword = txtSearch.getText().trim();
        tableContentPanel.removeAll();

        // Get Total Data dari Provider
        totalData = dataProvider.getTotalRowCount(keyword);

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

        int offset = (currentPage - 1) * entriesPerPage;

        // Get Baris Data Matang dari Provider
        List<String[]> pageData = dataProvider.getPageData(entriesPerPage, offset, keyword);

        // Render Tabel
        if (pageData.isEmpty()) {
            JLabel lblEmpty = new JLabel("Data tidak ditemukan");
            lblEmpty.setForeground(Theme.TEXT_SECONDARY);
            lblEmpty.setBorder(new EmptyBorder(20, 0, 20, 0));
            lblEmpty.setAlignmentX(Component.CENTER_ALIGNMENT);
            tableContentPanel.add(lblEmpty);
        } else {
            for (int i = 0; i < pageData.size(); i++) {
                String[] rowData = pageData.get(i);
                boolean isLastRow = (i == pageData.size() - 1);
                tableContentPanel.add(createDynamicTableRow(rowData, isLastRow));
            }
        }

        tableContentPanel.add(Box.createVerticalGlue());
        int startItem = (totalData == 0) ? 0 : offset + 1;
        int endItem = Math.min(offset + entriesPerPage, totalData);
        java.text.NumberFormat nf = java.text.NumberFormat.getInstance(Locale.forLanguageTag("id-ID"));
        String info = String.format("Menampilkan %s sampai %s dari %s data", nf.format(startItem), nf.format(endItem),
                nf.format(totalData));
        lblPageInfo.setText(info);
        btnPageNum.setText(String.valueOf(currentPage));
        btnPrev.setEnabled(currentPage > 1);
        btnNext.setEnabled(currentPage < totalPages);
        tableContentPanel.revalidate();
        tableContentPanel.repaint();
    }

    private JPanel createDynamicTableRow(String[] rowData, boolean isLastRow) {
        int cols = headers.length;
        JPanel row = new JPanel(new GridLayout(1, cols, 0, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        if (!isLastRow) {
            row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER));
        }

        for (int i = 0; i < cols; i++) {
            boolean addRightBorder = (i < cols - 1);
            String cellData = (i < rowData.length && rowData[i] != null) ? rowData[i] : "-";

            if (i == statusColIndex) {
                Color badgeColor = Theme.TEXT_PRIMARY;
                String lowerStr = cellData.toLowerCase();
                if (lowerStr.contains("selesai") || lowerStr.contains("lunas") || lowerStr.contains("aman")) {
                    badgeColor = Theme.GREEN;
                } else if (lowerStr.contains("proses") || lowerStr.contains("hutang") || lowerStr.contains("rendah")) {
                    badgeColor = Theme.WARNING;
                } else if (lowerStr.contains("batal") || lowerStr.contains("kritis")) {
                    badgeColor = Theme.RED;
                }

                JPanel badgeWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 8));
                badgeWrapper.setOpaque(false);
                if (addRightBorder) {
                    badgeWrapper.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Theme.BORDER));
                }

                JLabel lblBadge = new JLabel(cellData, SwingConstants.CENTER);
                lblBadge.setOpaque(true);
                lblBadge.setBackground(new Color(0, 0, 0, 0));
                lblBadge.setForeground(badgeColor);
                lblBadge.setFont(new Font("SansSerif", Font.BOLD, 11));
                lblBadge.setBorder(new EmptyBorder(4, 10, 4, 10));
                RoundedPanel pillPanel = new RoundedPanel(12,
                        new Color(badgeColor.getRed(), badgeColor.getGreen(), badgeColor.getBlue(), 40));
                pillPanel.setLayout(new BorderLayout());
                pillPanel.add(lblBadge, BorderLayout.CENTER);
                badgeWrapper.add(pillPanel);
                row.add(badgeWrapper);
            } else {
                row.add(createTableCell(cellData, (i == 1) ? Theme.TEXT_SECONDARY : Theme.TEXT_PRIMARY,
                        addRightBorder));
            }
        }
        return row;
    }

    private JLabel createTableCell(String text, Color color, boolean addRightBorder) {
        JLabel l = new JLabel(text);
        l.setForeground(color);
        l.setFont(new Font("SansSerif", Font.PLAIN, 13));

        if (addRightBorder) {
            l.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Theme.BORDER),
                    BorderFactory.createEmptyBorder(10, 5, 10, 5)));
        } else {
            l.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
        }
        return l;
    }
}
