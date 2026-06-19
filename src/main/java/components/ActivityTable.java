package components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Locale;
import utils.Theme;

public class ActivityTable extends RoundedPanel {

    public interface DataProvider {

        int getTotalRowCount(String keyword);

        List<String[]> getPageData(int limit, int offset, String keyword);
    }

    public interface TableActionListener {

        void onViewHistory(String id, String name);
    }

    public interface TableEditDeleteListener {

        void onEdit(String id, String name);

        void onDelete(String id, String name);
    }

    private static final String COL_AKSI = "Aksi";
    private static final String ICON_VIEW = "👁";
    private static final String ICON_EDIT = "✏";
    private static final String ICON_DELETE = "🗑";
    private static final int SCROLL_SPEED = 16;
    private static final int SEARCH_DELAY_MS = 400;
    private static final int TABLE_MIN_HEIGHT = 390;
    private static final int ROW_MAX_HEIGHT = 40;
    private final String title;
    private final String[] headers;
    private final int statusColIndex;
    private final DataProvider dataProvider;
    private TableActionListener tableActionListener;
    private TableEditDeleteListener tableEditDeleteListener;
    private int currentPage = 1;
    private int entriesPerPage = 5;
    private int totalData = 0;
    private JPanel tableContentPanel;
    private JLabel lblPageInfo;
    private JButton btnPageNum, btnPrev, btnNext;
    private JTextField txtSearch;
    private JComboBox<String> cbEntries;
    private Timer searchTimer;
    private JPanel headerRowPanel;
    private JScrollPane tableScroll;
    private String lastSelectedId = null;

    public ActivityTable(String title, String[] headers, int statusColIndex, DataProvider dataProvider) {
        super(20, Theme.CARD);
        this.title = title;
        this.headers = headers;
        this.statusColIndex = statusColIndex;
        this.dataProvider = dataProvider;

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setMinimumSize(new Dimension(0, TABLE_MIN_HEIGHT));
        buildUI();
        updateTableModel();
    }

    public void setTableActionListener(TableActionListener listener) {
        this.tableActionListener = listener;
    }

    public void setTableEditDeleteListener(TableEditDeleteListener listener) {
        this.tableEditDeleteListener = listener;
    }

    public String getLastSelectedId() {
        return this.lastSelectedId;
    }

    private void buildUI() {
        add(buildTopHeader(), BorderLayout.NORTH);
        add(buildTableContent(), BorderLayout.CENTER);
        add(buildPaginationRow(), BorderLayout.SOUTH);
    }

    private JPanel buildTopHeader() {
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
        txtSearch.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Theme.BORDER), BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        setupSearchDebouncer();

        rightControl.add(lblSearch);
        rightControl.add(txtSearch);

        controlRow.add(leftControl, BorderLayout.WEST);
        controlRow.add(rightControl, BorderLayout.EAST);

        topHeader.add(lblTitle, BorderLayout.NORTH);
        topHeader.add(controlRow, BorderLayout.CENTER);
        return topHeader;
    }

    private void setupSearchDebouncer() {
        searchTimer = new Timer(SEARCH_DELAY_MS, e -> {
            currentPage = 1;
            updateTableModel();
        });
        searchTimer.setRepeats(false);

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                searchTimer.restart();
            }

            public void removeUpdate(DocumentEvent e) {
                searchTimer.restart();
            }

            public void changedUpdate(DocumentEvent e) {
                searchTimer.restart();
            }
        });
    }

    private JScrollPane buildTableContent() {
        tableContentPanel = new JPanel();
        tableContentPanel.setLayout(new BoxLayout(tableContentPanel, BoxLayout.Y_AXIS));
        tableContentPanel.setOpaque(false);

        int cols = headers.length;

        headerRowPanel = new JPanel(new GridLayout(1, cols, 0, 0));
        headerRowPanel.setOpaque(true);
        headerRowPanel.setBackground(Theme.CARD);
        headerRowPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER));

        for (int i = 0; i < cols; i++) {
            JLabel l = new JLabel(headers[i]);
            l.setForeground(Theme.TEXT_SECONDARY);
            l.setFont(new Font("SansSerif", Font.PLAIN, 12));
            if (i < cols - 1) {
                l.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Theme.BORDER), BorderFactory.createEmptyBorder(10, 5, 10, 5)));
            } else {
                l.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
            }
            headerRowPanel.add(l);
        }

        tableScroll = new JScrollPane(tableContentPanel);
        tableScroll.setOpaque(false);
        tableScroll.getViewport().setOpaque(false);
        tableScroll.setBorder(null);
        tableScroll.setColumnHeaderView(headerRowPanel);
        tableScroll.getColumnHeader().setOpaque(false);
        tableScroll.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        tableScroll.getHorizontalScrollBar().setUI(new ModernScrollBarUI());
        tableScroll.getVerticalScrollBar().setUnitIncrement(SCROLL_SPEED);
        tableScroll.getHorizontalScrollBar().setUnitIncrement(SCROLL_SPEED);
        tableScroll.setPreferredSize(new Dimension(0, 200));
        tableScroll.setMinimumSize(new Dimension(0, 200));

        tableScroll.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                syncTableWidths();
            }
        });

        return tableScroll;
    }

    private JPanel buildPaginationRow() {
        JPanel paginationRow = new JPanel(new BorderLayout());
        paginationRow.setOpaque(false);
        paginationRow.setBorder(new EmptyBorder(15, 0, 0, 0));

        lblPageInfo = new JLabel();
        lblPageInfo.setForeground(Theme.TEXT_SECONDARY);
        lblPageInfo.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        btnPanel.setOpaque(false);

        btnPrev = createPaginationButton("Sebelumnya", e -> {
            currentPage--;
            updateTableModel();
        });
        btnPageNum = createPaginationButton("1", null);
        btnPageNum.setBackground(Theme.BLUE_ACCENT);
        btnPageNum.setForeground(Color.WHITE);
        btnNext = createPaginationButton("Selanjutnya", e -> {
            currentPage++;
            updateTableModel();
        });

        btnPanel.add(btnPrev);
        btnPanel.add(btnPageNum);
        btnPanel.add(btnNext);

        paginationRow.add(lblPageInfo, BorderLayout.WEST);
        paginationRow.add(btnPanel, BorderLayout.EAST);
        return paginationRow;
    }

    private JButton createPaginationButton(String text, java.awt.event.ActionListener action) {
        JButton btn = new JButton(text);
        btn.setBackground(Theme.BG);
        btn.setForeground(Theme.TEXT_PRIMARY);
        btn.setFocusPainted(false);
        if (action != null) {
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.addActionListener(action);
        }
        return btn;
    }

    public void updateTableModel() {
        String keyword = txtSearch.getText().trim();
        showLoadingState();
        new SwingWorker<TableFetchResult, Void>() {
            @Override
            protected TableFetchResult doInBackground() {
                int dbTotalData = dataProvider.getTotalRowCount(keyword);
                int tempTotalPages = Math.max(1, (int) Math.ceil((double) dbTotalData / entriesPerPage));

                int safeCurrentPage = Math.min(Math.max(1, currentPage), tempTotalPages);
                int offset = (safeCurrentPage - 1) * entriesPerPage;

                List<String[]> pageData = dataProvider.getPageData(entriesPerPage, offset, keyword);
                return new TableFetchResult(dbTotalData, tempTotalPages, safeCurrentPage, offset, pageData);
            }

            @Override
            protected void done() {
                try {
                    renderTableData(get());
                } catch (Exception e) {
                    showEmptyState("Terjadi kesalahan sistem saat memuat data.");
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    private void showLoadingState() {
        tableContentPanel.removeAll();
        JLabel lblLoading = new JLabel("Mencari data...");
        lblLoading.setForeground(Theme.TEXT_SECONDARY);
        lblLoading.setBorder(new EmptyBorder(20, 0, 20, 0));
        lblLoading.setAlignmentX(Component.CENTER_ALIGNMENT);
        tableContentPanel.add(lblLoading);
        refreshUI();
    }

    private void showEmptyState(String message) {
        tableContentPanel.removeAll();
        JLabel lblEmpty = new JLabel(message);
        lblEmpty.setForeground(Theme.TEXT_SECONDARY);
        lblEmpty.setBorder(new EmptyBorder(20, 0, 20, 0));
        lblEmpty.setAlignmentX(Component.CENTER_ALIGNMENT);
        tableContentPanel.add(lblEmpty);
        refreshUI();
    }

    private void renderTableData(TableFetchResult result) {
        totalData = result.totalData;
        currentPage = result.currentPage;
        tableContentPanel.removeAll();

        if (result.pageData.isEmpty()) {
            showEmptyState("Data tidak ditemukan");
        } else {
            for (int i = 0; i < result.pageData.size(); i++) {
                boolean isLastRow = (i == result.pageData.size() - 1);
                tableContentPanel.add(createDynamicTableRow(result.pageData.get(i), isLastRow));
            }
        }

        tableContentPanel.add(Box.createVerticalGlue());

        int startItem = (totalData == 0) ? 0 : result.offset + 1;
        int endItem = Math.min(result.offset + entriesPerPage, totalData);
        java.text.NumberFormat nf = java.text.NumberFormat.getInstance(Locale.forLanguageTag("id-ID"));
        lblPageInfo.setText(String.format("Menampilkan %s sampai %s dari %s data", nf.format(startItem), nf.format(endItem), nf.format(totalData)));
        btnPageNum.setText(String.valueOf(currentPage));
        btnPrev.setEnabled(currentPage > 1);
        btnNext.setEnabled(currentPage < result.totalPages);
        refreshUI();
        syncTableWidths();
    }

    private void syncTableWidths() {
        SwingUtilities.invokeLater(() -> {
            if (tableScroll != null && headerRowPanel != null) {
                for (Component c : tableContentPanel.getComponents()) {
                    if (c instanceof JPanel) {
                        c.setPreferredSize(null);
                        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, ROW_MAX_HEIGHT));
                    }
                }
                headerRowPanel.setPreferredSize(null);

                int maxContentWidth = tableContentPanel.getPreferredSize().width;
                int viewportWidth = tableScroll.getViewport().getWidth();
                int finalWidth = Math.max(maxContentWidth, viewportWidth);
                int headerHeight = Math.max(headerRowPanel.getPreferredSize().height, 35);
                headerRowPanel.setPreferredSize(new Dimension(finalWidth, headerHeight));
                headerRowPanel.revalidate();

                for (Component c : tableContentPanel.getComponents()) {
                    if (c instanceof JPanel) {
                        c.setMaximumSize(new Dimension(finalWidth, ROW_MAX_HEIGHT));
                        c.setPreferredSize(new Dimension(finalWidth, ROW_MAX_HEIGHT));
                        c.revalidate();
                    }
                }

                tableContentPanel.repaint();
                headerRowPanel.repaint();
            }
        });
    }

    private JPanel createDynamicTableRow(String[] rowData, boolean isLastRow) {
        int cols = headers.length;
        JPanel row = new JPanel(new GridLayout(1, cols, 0, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, ROW_MAX_HEIGHT));

        if (!isLastRow) {
            row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER));
        }

        final String targetId = extractTargetId(rowData, cols);
        row.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastSelectedId = targetId;
            }
        });

        for (int i = 0; i < cols; i++) {
            boolean addRightBorder = (i < cols - 1);
            String cellData = (i < rowData.length && rowData[i] != null) ? rowData[i] : "-";

            if (headers[i].equalsIgnoreCase(COL_AKSI)) {
                row.add(createActionCell(rowData, cols, addRightBorder));
            } else if (i == statusColIndex) {
                row.add(createStatusCell(cellData, addRightBorder));
            } else {
                row.add(createTableCell(cellData, (i == 1) ? Theme.TEXT_SECONDARY : Theme.TEXT_PRIMARY, addRightBorder));
            }
        }
        return row;
    }

    private JPanel createActionCell(String[] rowData, int cols, boolean addRightBorder) {
        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 4));
        wrapper.setOpaque(false);
        if (addRightBorder) {
            wrapper.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Theme.BORDER));
        }

        final String targetId = extractTargetId(rowData, cols);
        final String targetName = rowData.length > 1 ? rowData[1] : "-";

        if (tableActionListener != null) {
            wrapper.add(createActionButton(ICON_VIEW, Theme.BLUE_ACCENT, "Lihat Riwayat",
                    () -> {
                        lastSelectedId = targetId;
                        tableActionListener.onViewHistory(targetId, targetName);
                    }));
        }
        if (tableEditDeleteListener != null) {
            wrapper.add(createActionButton(ICON_EDIT, Theme.WARNING, "Edit Data",
                    () -> {
                        lastSelectedId = targetId;
                        tableEditDeleteListener.onEdit(targetId, targetName);
                    }));
            wrapper.add(createActionButton(ICON_DELETE, Theme.RED, "Hapus Data",
                    () -> {
                        lastSelectedId = targetId;
                        tableEditDeleteListener.onDelete(targetId, targetName);
                    }));
        }
        return wrapper;
    }

    private String extractTargetId(String[] rowData, int cols) {
        if (rowData.length > cols) {
            return rowData[rowData.length - 1];
        }
        if (rowData.length > 0) {
            return rowData[0];
        }
        return "-";
    }

    private JPanel createStatusCell(String cellData, boolean addRightBorder) {
        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 8));
        wrapper.setOpaque(false);
        if (addRightBorder) {
            wrapper.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Theme.BORDER));
        }

        Color badgeColor = getStatusColor(cellData);

        JLabel lblBadge = new JLabel(cellData, SwingConstants.CENTER);
        lblBadge.setOpaque(true);
        lblBadge.setBackground(new Color(0, 0, 0, 0));
        lblBadge.setForeground(badgeColor);
        lblBadge.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblBadge.setBorder(new EmptyBorder(4, 10, 4, 10));

        RoundedPanel pillPanel = new RoundedPanel(12, new Color(badgeColor.getRed(), badgeColor.getGreen(), badgeColor.getBlue(), 40));
        pillPanel.setLayout(new BorderLayout());
        pillPanel.add(lblBadge, BorderLayout.CENTER);

        wrapper.add(pillPanel);
        return wrapper;
    }

    private Color getStatusColor(String status) {
        String lowerStr = status.toLowerCase();
        if (lowerStr.matches(".*(selesai|lunas|aman).*")) {
            return Theme.GREEN;
        }
        if (lowerStr.matches(".*(proses|hutang|rendah).*")) {
            return Theme.WARNING;
        }
        if (lowerStr.matches(".*(batal|kritis).*")) {
            return Theme.RED;
        }
        return Theme.TEXT_PRIMARY;
    }

    private JLabel createTableCell(String text, Color color, boolean addRightBorder) {
        JLabel l = new JLabel(text);
        l.setForeground(color);
        l.setFont(new Font("SansSerif", Font.PLAIN, 13));
        if (addRightBorder) {
            l.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Theme.BORDER), BorderFactory.createEmptyBorder(10, 5, 10, 5)));
        } else {
            l.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
        }
        return l;
    }

    private RoundedPanel createActionButton(String icon, Color hoverColor, String tooltip, Runnable onClick) {
        RoundedPanel btn = new RoundedPanel(8, Theme.CARD);
        btn.setPreferredSize(new Dimension(32, 28));
        btn.setLayout(new BorderLayout());
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setToolTipText(tooltip);
        btn.setBorder(BorderFactory.createLineBorder(Theme.BORDER, 1));

        JLabel lblIcon = new JLabel(icon, SwingConstants.CENTER);
        lblIcon.setForeground(Theme.TEXT_SECONDARY);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
        btn.add(lblIcon, BorderLayout.CENTER);
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(hoverColor);
                btn.setBorder(BorderFactory.createLineBorder(hoverColor, 1));
                lblIcon.setForeground(Color.WHITE);
                btn.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(Theme.CARD);
                btn.setBorder(BorderFactory.createLineBorder(Theme.BORDER, 1));
                lblIcon.setForeground(Theme.TEXT_SECONDARY);
                btn.repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (onClick != null) {
                    onClick.run();
                }
            }
        });
        return btn;
    }

    private void refreshUI() {
        tableContentPanel.revalidate();
        tableContentPanel.repaint();
    }

    private static class TableFetchResult {

        final int totalData, totalPages, currentPage, offset;
        final List<String[]> pageData;

        TableFetchResult(int totalData, int totalPages, int currentPage, int offset, List<String[]> pageData) {
            this.totalData = totalData;
            this.totalPages = totalPages;
            this.currentPage = currentPage;
            this.offset = offset;
            this.pageData = pageData;
        }
    }
}