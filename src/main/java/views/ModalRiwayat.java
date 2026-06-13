package views;

import components.ActivityTable;
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

public class ModalRiwayat extends JDialog {

    private final BahanBakuDAO bahanDAO = new BahanBakuDAO();
    private String targetNamaBahan;
    private Point initialClick;
    private JLabel lblTotalTransaksi, lblNilaiPembelian, lblStokTerpakai;

    public ModalRiwayat(Frame parent, String idBahan, String namaBahan) {
        super(parent, "Riwayat Bahan Baku", true);
        this.targetNamaBahan = namaBahan;

        setSize(950, 750);
        setLocationRelativeTo(parent);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));

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
        loadTopCardsData();
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(20, 25, 10, 25));
        header.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
            }
        });
        header.addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                int thisX = getLocation().x;
                int thisY = getLocation().y;
                int xMoved = e.getX() - initialClick.x;
                int yMoved = e.getY() - initialClick.y;
                setLocation(thisX + xMoved, thisY + yMoved);
            }
        });

        // Icon + Titles
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
        JLabel title = new JLabel("Daftar Riwayat Bahan Baku");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(Theme.TEXT_PRIMARY);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", Locale.forLanguageTag("id-ID"));
        JLabel subtitle = new JLabel(LocalDate.now().format(formatter) + "  •  Menampilkan data: " + targetNamaBahan);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitle.setForeground(Theme.TEXT_SECONDARY);

        titlePanel.add(title);
        titlePanel.add(subtitle);

        leftHeader.add(iconPanel);
        leftHeader.add(titlePanel);

        // Buttons
        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        rightHeader.setOpaque(false);

        RoundedPanel btnExport = new RoundedPanel(10, Theme.CARD);
        btnExport.setPreferredSize(new Dimension(100, 35));
        btnExport.setLayout(new BorderLayout());
        btnExport.setBorder(BorderFactory.createLineBorder(Theme.BORDER, 1));
        btnExport.setCursor(new Cursor(Cursor.HAND_CURSOR));
        JLabel lblExport = new JLabel("Export PDF", SwingConstants.CENTER);
        lblExport.setForeground(Theme.TEXT_PRIMARY);
        lblExport.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btnExport.add(lblExport, BorderLayout.CENTER);

        RoundedPanel btnClose = new RoundedPanel(10, Theme.CARD);
        btnClose.setPreferredSize(new Dimension(35, 35));
        btnClose.setLayout(new BorderLayout());
        btnClose.setBorder(BorderFactory.createLineBorder(Theme.BORDER, 1));
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        JLabel lblClose = new JLabel("X", SwingConstants.CENTER);
        lblClose.setForeground(Theme.TEXT_SECONDARY);
        lblClose.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnClose.add(lblClose, BorderLayout.CENTER);
        btnClose.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                dispose();
            }

            public void mouseEntered(MouseEvent e) {
                btnClose.setBackground(Theme.BORDER);
                btnClose.repaint();
            }

            public void mouseExited(MouseEvent e) {
                btnClose.setBackground(Theme.CARD);
                btnClose.repaint();
            }
        });

        rightHeader.add(btnExport);
        rightHeader.add(btnClose);
        header.add(leftHeader, BorderLayout.WEST);
        header.add(rightHeader, BorderLayout.EAST);
        return header;
    }

    private JPanel buildTopCards() {
        JPanel topCardsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        topCardsPanel.setOpaque(false);
        topCardsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        lblTotalTransaksi = new JLabel("0", SwingConstants.LEFT);
        lblTotalTransaksi.setForeground(Theme.TEXT_PRIMARY);
        lblTotalTransaksi.setFont(new Font("SansSerif", Font.BOLD, 24));

        lblNilaiPembelian = new JLabel("Rp 0", SwingConstants.LEFT);
        lblNilaiPembelian.setForeground(Theme.GREEN);
        lblNilaiPembelian.setFont(new Font("SansSerif", Font.BOLD, 24));

        lblStokTerpakai = new JLabel("0 kg", SwingConstants.LEFT);
        lblStokTerpakai.setForeground(Theme.WARNING);
        lblStokTerpakai.setFont(new Font("SansSerif", Font.BOLD, 24));

        topCardsPanel.add(createCard("TOTAL TRANSAKSI", lblTotalTransaksi, "Semua riwayat"));
        topCardsPanel.add(createCard("NILAI PEMBELIAN", lblNilaiPembelian, "Akumulasi harga beli"));
        topCardsPanel.add(createCard("STOK MASUK", lblStokTerpakai, "Total kuantitas tercatat"));
        return topCardsPanel;
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
        ActivityTable table = new ActivityTable("Riwayat: " + targetNamaBahan, headers, -1, new ActivityTable.DataProvider() {
            @Override
            public int getTotalRowCount(String keyword) {
                return bahanDAO.getRiwayatTotalRows(targetNamaBahan, keyword);
            }

            @Override
            public List<String[]> getPageData(int limit, int offset, String keyword) {
                return bahanDAO.getRiwayatPageData(limit, offset, targetNamaBahan, keyword);
            }
        });

        // Pensil dan tong sampah
        table.setTableEditDeleteListener(new ActivityTable.TableEditDeleteListener() {
            @Override
            public void onEdit(String id, String name) {
                JOptionPane.showMessageDialog(ModalRiwayat.this,
                        "Fungsi Edit untuk baris data dengan ID/Tanggal: " + id,
                        "Mode Edit Transaksi",
                        JOptionPane.INFORMATION_MESSAGE);
            }

            @Override
            public void onDelete(String id, String name) {
                int confirm = JOptionPane.showConfirmDialog(ModalRiwayat.this,
                        "Apakah Anda yakin ingin menghapus data transaksi ini secara permanen?",
                        "Konfirmasi Hapus",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);

                if (confirm == JOptionPane.YES_OPTION) {
                    JOptionPane.showMessageDialog(ModalRiwayat.this, "Data transaksi dihapus.");
                }
            }
        });

        return table;
    }

    private void loadTopCardsData() {
        new Thread(() -> {
            Map<String, String> data = bahanDAO.getRiwayatTopCardsData(targetNamaBahan);
            SwingUtilities.invokeLater(() -> {
                lblTotalTransaksi.setText(data.getOrDefault("total_transaksi", "0"));
                lblNilaiPembelian.setText(data.getOrDefault("nilai_pembelian", "Rp 0"));
                lblStokTerpakai.setText(data.getOrDefault("stok_terpakai", "0"));
            });
        }).start();
    }
}
