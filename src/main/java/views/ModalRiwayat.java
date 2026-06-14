package views;

import components.ActivityTable;
import components.RoundedPanel;
import dao.BahanBakuDAO;
import utils.Theme;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ModalRiwayat extends JDialog {

    private static final int MODAL_WIDTH = 950;
    private static final int MODAL_HEIGHT = 750;
    private static final String TITLE_DIALOG = "Riwayat Bahan Baku";
    private static final String TITLE_HEADER = "Daftar Riwayat Bahan Baku";
    private static final String ICON_CLOCK = "🕒";
    private static final String ICON_CLOSE = "X";
    private static final String CARD_TITLE_1 = "TOTAL TRANSAKSI";
    private static final String CARD_DESC_1 = "Semua riwayat";
    private static final String CARD_TITLE_2 = "NILAI PEMBELIAN";
    private static final String CARD_DESC_2 = "Akumulasi harga beli";
    private static final String CARD_TITLE_3 = "STOK MASUK";
    private static final String CARD_DESC_3 = "Total kuantitas tercatat";
    private final BahanBakuDAO bahanDAO = new BahanBakuDAO();
    private final String targetNamaBahan;
    private Point initialClick;
    private JLabel lblTotalTransaksi;
    private JLabel lblNilaiPembelian;
    private JLabel lblStokTerpakai;
    private ActivityTable tableRiwayat;

    public ModalRiwayat(Frame parent, String idBahan, String namaBahan) {
        super(parent, TITLE_DIALOG, true);
        this.targetNamaBahan = namaBahan;

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
        bodyPanel.add(buildRiwayatTable());

        mainWrapper.add(bodyPanel, BorderLayout.CENTER);
        getContentPane().add(mainWrapper, BorderLayout.CENTER);
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

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(20, 25, 10, 25));
        enableWindowDrag(header);

        // Icon & Titles
        JPanel leftHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftHeader.setOpaque(false);
        leftHeader.add(createHeaderIcon());
        leftHeader.add(createHeaderTitleText());

        // Buttons
        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        rightHeader.setOpaque(false);
        rightHeader.add(createActionButton("Export PDF", 100, Theme.TEXT_PRIMARY, this::handleExportPdf));
        rightHeader.add(createActionButton(ICON_CLOSE, 35, Theme.TEXT_SECONDARY, this::dispose));

        header.add(leftHeader, BorderLayout.WEST);
        header.add(rightHeader, BorderLayout.EAST);
        return header;
    }

    private JPanel buildTopCards() {
        JPanel topCardsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        topCardsPanel.setOpaque(false);
        topCardsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        lblTotalTransaksi = createCardValueLabel(Theme.TEXT_PRIMARY);
        lblNilaiPembelian = createCardValueLabel(Theme.GREEN);
        lblStokTerpakai = createCardValueLabel(Theme.WARNING);

        topCardsPanel.add(createCard(CARD_TITLE_1, lblTotalTransaksi, CARD_DESC_1));
        topCardsPanel.add(createCard(CARD_TITLE_2, lblNilaiPembelian, CARD_DESC_2));
        topCardsPanel.add(createCard(CARD_TITLE_3, lblStokTerpakai, CARD_DESC_3));

        return topCardsPanel;
    }

    private JLabel createCardValueLabel(Color color) {
        JLabel label = new JLabel("Memuat...", SwingConstants.LEFT);
        label.setForeground(color);
        label.setFont(new Font("SansSerif", Font.BOLD, 24));
        return label;
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

    private ActivityTable buildRiwayatTable() {
        String[] headers = {"Tgl Masuk", "Nama Bahan", "Supplier", "Jumlah", "Satuan", "Total Nilai", "Aksi"};
        tableRiwayat = new ActivityTable("Riwayat: " + targetNamaBahan, headers, -1, new ActivityTable.DataProvider() {
            @Override
            public int getTotalRowCount(String keyword) {
                return bahanDAO.getRiwayatTotalRows(targetNamaBahan, keyword);
            }

            @Override
            public List<String[]> getPageData(int limit, int offset, String keyword) {
                return bahanDAO.getRiwayatPageData(limit, offset, targetNamaBahan, keyword);
            }
        });

        tableRiwayat.setTableEditDeleteListener(new ActivityTable.TableEditDeleteListener() {
            @Override
            public void onEdit(String id, String name) {
                handleEdit(id);
            }

            @Override
            public void onDelete(String id, String name) {
                handleDelete(id);
            }
        });

        return tableRiwayat;
    }

    private void handleEdit(String id) {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(ModalRiwayat.this);
        ModalEditBahan modalEdit = new ModalEditBahan(parentFrame, id);
        modalEdit.setVisible(true);

        if (modalEdit.isSaved()) {
            JOptionPane.showMessageDialog(this, "Data berhasil diperbarui!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            refreshData();
        }
    }

    private void handleDelete(String id) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Apakah Anda yakin ingin menghapus data transaksi ini secara permanen?",
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            if (bahanDAO.deleteRiwayatById(id)) {
                JOptionPane.showMessageDialog(this, "Data transaksi berhasil dihapus dari database.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                refreshData();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus data transaksi.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleExportPdf() {
        JOptionPane.showMessageDialog(this, "Fitur Export PDF akan segera hadir!", "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void refreshData() {
        tableRiwayat.updateTableModel();
        loadTopCardsData();
    }

    private void loadTopCardsData() {
        new SwingWorker<Map<String, String>, Void>() {
            @Override
            protected Map<String, String> doInBackground() {
                return bahanDAO.getRiwayatTopCardsData(targetNamaBahan);
            }

            @Override
            protected void done() {
                try {
                    Map<String, String> data = get();
                    lblTotalTransaksi.setText(data.getOrDefault("total_transaksi", "0"));
                    lblNilaiPembelian.setText(data.getOrDefault("nilai_pembelian", "Rp 0"));
                    lblStokTerpakai.setText(data.getOrDefault("stok_terpakai", "0"));
                } catch (Exception e) {
                    e.printStackTrace();
                    lblTotalTransaksi.setText("-");
                    lblNilaiPembelian.setText("-");
                    lblStokTerpakai.setText("-");
                }
            }
        }.execute();
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

    private RoundedPanel createHeaderIcon() {
        RoundedPanel iconPanel = new RoundedPanel(12, Theme.BLUE_ACCENT);
        iconPanel.setPreferredSize(new Dimension(45, 45));
        iconPanel.setLayout(new BorderLayout());
        JLabel lblIcon = new JLabel(ICON_CLOCK, SwingConstants.CENTER);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        lblIcon.setForeground(Color.WHITE);
        iconPanel.add(lblIcon, BorderLayout.CENTER);
        return iconPanel;
    }

    private JPanel createHeaderTitleText() {
        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setOpaque(false);

        JLabel title = new JLabel(TITLE_HEADER);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(Theme.TEXT_PRIMARY);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", Locale.forLanguageTag("id-ID"));
        JLabel subtitle = new JLabel(LocalDate.now().format(formatter) + "  •  Menampilkan data: " + targetNamaBahan);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitle.setForeground(Theme.TEXT_SECONDARY);

        titlePanel.add(title);
        titlePanel.add(subtitle);
        return titlePanel;
    }

    private RoundedPanel createActionButton(String text, int width, Color textColor, Runnable action) {
        RoundedPanel btn = new RoundedPanel(10, Theme.CARD);
        btn.setPreferredSize(new Dimension(width, 35));
        btn.setLayout(new BorderLayout());
        btn.setBorder(BorderFactory.createLineBorder(Theme.BORDER, 1));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setForeground(textColor);
        lbl.setFont(new Font("SansSerif", text.equals(ICON_CLOSE) ? Font.BOLD : Font.PLAIN, text.equals(ICON_CLOSE) ? 14 : 12));
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
}
