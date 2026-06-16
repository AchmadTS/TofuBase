package views;

import components.ActivityTable;
import components.ModernScrollBarUI;
import components.RoundedPanel;
import dao.SupplierDAO;
import utils.Theme;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

public class ModalRiwayatSupplier extends JDialog {

    private static final int MODAL_WIDTH = 950;
    private static final int MODAL_HEIGHT = 750;
    private static final String TITLE_DIALOG = "Riwayat Supplier";
    private static final String TITLE_HEADER = "Daftar Riwayat Supplier";
    private final SupplierDAO supplierDAO = new SupplierDAO();
    private final String targetIdSupplier;
    private final String targetNamaSupplier;
    private Point initialClick;
    private JLabel lblTotalTransaksi, lblBahanDisuplai, lblTotalNilai;
    private ActivityTable tableRiwayat;

    public ModalRiwayatSupplier(Frame parent, String idSupplier, String namaSupplier) {
        super(parent, TITLE_DIALOG, true);
        this.targetIdSupplier = idSupplier;
        this.targetNamaSupplier = namaSupplier;
        setupDialogProperties(parent);
        buildMainLayout();
        setupEscapeKey();
        loadTopCardsData();
    }

    private void setupDialogProperties(Frame parent) {
        setSize(MODAL_WIDTH, MODAL_HEIGHT);
        setLocationRelativeTo(parent);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));
    }

    private void buildMainLayout() {
        RoundedPanel mainWrapper = new RoundedPanel(20, Theme.BG);
        mainWrapper.setLayout(new BorderLayout());
        mainWrapper.setBorder(BorderFactory.createLineBorder(Theme.BORDER, 1));
        mainWrapper.add(buildHeader(), BorderLayout.NORTH);

        JPanel bodyPanel = new JPanel();
        bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.Y_AXIS));
        bodyPanel.setOpaque(false);
        bodyPanel.setBorder(new EmptyBorder(10, 25, 25, 25));
        bodyPanel.add(buildTopCards());
        bodyPanel.add(Box.createVerticalStrut(20));
        bodyPanel.add(wrapInScroll(buildRiwayatTable()));
        
        mainWrapper.add(bodyPanel, BorderLayout.CENTER);
        getContentPane().add(mainWrapper, BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(20, 25, 10, 25));
        enableWindowDrag(header);

        JPanel leftHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftHeader.setOpaque(false);

        RoundedPanel iconPanel = new RoundedPanel(12, Theme.BLUE_ACCENT);
        iconPanel.setPreferredSize(new Dimension(45, 45));
        iconPanel.setLayout(new BorderLayout());
        JLabel lblIcon = new JLabel("🕒", SwingConstants.CENTER);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        lblIcon.setForeground(Color.WHITE);
        iconPanel.add(lblIcon, BorderLayout.CENTER);

        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setOpaque(false);
        JLabel title = new JLabel(TITLE_HEADER);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(Theme.TEXT_PRIMARY);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", Locale.forLanguageTag("id-ID"));
        JLabel subtitle = new JLabel(LocalDate.now().format(formatter) + "  •  Menampilkan data: " + targetNamaSupplier);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitle.setForeground(Theme.TEXT_SECONDARY);
        titlePanel.add(title);
        titlePanel.add(subtitle);

        leftHeader.add(iconPanel);
        leftHeader.add(titlePanel);

        // Export & Close
        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        rightHeader.setOpaque(false);
        rightHeader.add(createActionButton("Export PDF", 100, Theme.TEXT_PRIMARY, this::handleExportPdf));
        rightHeader.add(createCloseButton());

        header.add(leftHeader, BorderLayout.WEST);
        header.add(rightHeader, BorderLayout.EAST);
        return header;
    }

    private JPanel buildTopCards() {
        JPanel topCardsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        topCardsPanel.setOpaque(false);
        topCardsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        lblTotalTransaksi = createCardValueLabel(Theme.TEXT_PRIMARY);
        lblBahanDisuplai = createCardValueLabel(Theme.GREEN);
        lblTotalNilai = createCardValueLabel(Theme.BLUE_ACCENT);

        topCardsPanel.add(createCard("TOTAL TRANSAKSI", lblTotalTransaksi, "Semua riwayat"));
        topCardsPanel.add(createCard("BAHAN DISUPLAI", lblBahanDisuplai, "Jenis bahan"));
        topCardsPanel.add(createCard("TOTAL NILAI PASOKAN", lblTotalNilai, "Akumulasi nilai"));
        return topCardsPanel;
    }

    private ActivityTable buildRiwayatTable() {
        String[] headers = {"Tgl Transaksi", "Nama Bahan", "Jumlah", "Satuan", "Total Nilai"};
        return new ActivityTable("Riwayat: " + targetNamaSupplier, headers, -1, new ActivityTable.DataProvider() {
            @Override
            public int getTotalRowCount(String keyword) {
                return supplierDAO.getRiwayatTotalRows(targetIdSupplier, keyword);
            }

            @Override
            public java.util.List<String[]> getPageData(int limit, int offset, String keyword) {
                return supplierDAO.getRiwayatPageData(limit, offset, targetIdSupplier, keyword);
            }
        });
    }

    private JScrollPane wrapInScroll(Component content) {
        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    private RoundedPanel createCloseButton() {
        RoundedPanel btn = new RoundedPanel(10, Theme.CARD);
        btn.setPreferredSize(new Dimension(35, 35));
        btn.setLayout(new BorderLayout());
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        JLabel lbl = new JLabel("X", SwingConstants.CENTER);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        lbl.setForeground(Theme.TEXT_SECONDARY);
        btn.add(lbl, BorderLayout.CENTER);
        btn.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                dispose();
            }

            public void mouseEntered(MouseEvent e) {
                btn.setBackground(Theme.BORDER);
                btn.repaint();
            }

            public void mouseExited(MouseEvent e) {
                btn.setBackground(Theme.CARD);
                btn.repaint();
            }
        });
        return btn;
    }

    private RoundedPanel createActionButton(String text, int width, Color textColor, Runnable action) {
        RoundedPanel btn = new RoundedPanel(10, Theme.CARD);
        btn.setPreferredSize(new Dimension(width, 35));
        btn.setLayout(new BorderLayout());
        btn.setBorder(BorderFactory.createLineBorder(Theme.BORDER, 1));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setForeground(textColor);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btn.add(lbl, BorderLayout.CENTER);
        btn.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                action.run();
            }

            public void mouseEntered(MouseEvent e) {
                btn.setBackground(Theme.BORDER);
                btn.repaint();
            }

            public void mouseExited(MouseEvent e) {
                btn.setBackground(Theme.CARD);
                btn.repaint();
            }
        });
        return btn;
    }

    private void handleExportPdf() {
        JOptionPane.showMessageDialog(this, "Fitur Export PDF akan segera hadir!", "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void loadTopCardsData() {
        new SwingWorker<Map<String, String>, Void>() {
            @Override
            protected Map<String, String> doInBackground() {
                return supplierDAO.getRiwayatTopCardsData(targetIdSupplier);
            }

            @Override
            protected void done() {
                try {
                    Map<String, String> data = get();
                    lblTotalTransaksi.setText(data.getOrDefault("total_transaksi", "0"));
                    lblBahanDisuplai.setText(data.getOrDefault("bahan_disuplai", "0"));
                    lblTotalNilai.setText(data.getOrDefault("total_nilai", "Rp 0"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    private void setupEscapeKey() {
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "closeModal");
        getRootPane().getActionMap().put("closeModal", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    private RoundedPanel createCard(String title, JLabel valueLabel, String subtitle) {
        RoundedPanel card = new RoundedPanel(15, Theme.CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setForeground(Theme.TEXT_SECONDARY);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 10));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSub = new JLabel(subtitle);
        lblSub.setForeground(Theme.TEXT_SECONDARY);
        lblSub.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(lblTitle);
        card.add(Box.createVerticalStrut(10));
        card.add(valueLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(lblSub);
        return card;
    }

    private JLabel createCardValueLabel(Color color) {
        JLabel label = new JLabel("Memuat...", SwingConstants.LEFT);
        label.setForeground(color);
        label.setFont(new Font("SansSerif", Font.BOLD, 24));
        return label;
    }

    private void enableWindowDrag(JPanel header) {
        header.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
            }
        });
        header.addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                setLocation(getLocation().x + e.getX() - initialClick.x, getLocation().y + e.getY() - initialClick.y);
            }
        });
    }
}
