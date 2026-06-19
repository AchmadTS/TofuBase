package views;

import components.ActivityTable;
import components.RoundedPanel;
import dao.PenjualanDAO;
import models.Penjualan;
import models.RecordPenjualan;
import utils.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import utils.FormatUtil;

public class ModalRiwayatPenjualan extends JDialog {

    private static final int WIDTH = 900;
    private static final int HEIGHT = 680;
    private static final String TITLE = "Detail Penjualan";
    private static final String ICON_CLOSE = "X";
    private static final String BTN_EXPORT = "Export PDF";

    private final PenjualanDAO penjualanDAO = new PenjualanDAO();
    private final int idPenjualan;
    private JLabel lblTanggal;
    private JLabel lblPelanggan;
    private JLabel lblTotal;
    private JLabel lblKeterangan;
    private ActivityTable tableDetail;

    public ModalRiwayatPenjualan(Frame parent, int idPenjualan) {
        super(parent, TITLE, true);
        this.idPenjualan = idPenjualan;
        setupDialog(parent);
        buildLayout();
        loadDetailData();
    }

    private void setupDialog(Frame parent) {
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(parent);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));
    }

    private void buildLayout() {
        RoundedPanel wrapper = new RoundedPanel(20, Theme.BG);
        wrapper.setLayout(new BorderLayout());
        wrapper.setBorder(BorderFactory.createLineBorder(Theme.BORDER, 1));
        wrapper.add(buildHeader(), BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(new EmptyBorder(20, 25, 25, 25));

        body.add(buildInfoPanel());
        body.add(Box.createVerticalStrut(20));
        body.add(buildDetailTable());

        wrapper.add(body, BorderLayout.CENTER);
        getContentPane().add(wrapper);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(20, 25, 10, 25));

        JLabel title = new JLabel("Rincian Penjualan");
        title.setForeground(Theme.TEXT_PRIMARY);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setOpaque(false);
        buttons.add(createButton(BTN_EXPORT, 120, Theme.TEXT_PRIMARY, this::handleExportPdf));
        buttons.add(createButton(ICON_CLOSE, 40, Theme.TEXT_SECONDARY, this::dispose));

        header.add(title, BorderLayout.WEST);
        header.add(buttons, BorderLayout.EAST);
        return header;
    }

    private JPanel buildInfoPanel() {
        JPanel info = new JPanel(new GridLayout(2, 2, 20, 15));
        info.setOpaque(false);

        lblTanggal = new JLabel("-");
        lblPelanggan = new JLabel("-");
        lblTotal = new JLabel("-");
        lblKeterangan = new JLabel("-");

        info.add(createInfoGroup("Tanggal", lblTanggal));
        info.add(createInfoGroup("Pelanggan", lblPelanggan));
        info.add(createInfoGroup("Total", lblTotal));
        info.add(createInfoGroup("Keterangan", lblKeterangan));

        return info;
    }

    private JPanel createInfoGroup(String title, JLabel valueLabel) {
        JLabel lblTitle = new JLabel(title);
        lblTitle.setForeground(Theme.TEXT_SECONDARY);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 11));

        valueLabel.setForeground(Theme.TEXT_PRIMARY);
        valueLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JPanel panel = new JPanel();
        panel.setOpaque(true);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setBackground(Theme.CARD);
        panel.add(lblTitle);
        panel.add(Box.createVerticalStrut(8));
        panel.add(valueLabel);
        return panel;
    }

    private JPanel buildDetailTable() {
        String[] headers = {"Produk", "Jumlah", "Satuan", "Harga", "Subtotal", "Aksi"};
        tableDetail = new ActivityTable("Detail Produk Penjualan", headers, -1, new ActivityTable.DataProvider() {
            @Override
            public int getTotalRowCount(String keyword) {
                return penjualanDAO.getRecordPenjualanByPenjualanId(idPenjualan).size();
            }

            @Override
            public List<String[]> getPageData(int limit, int offset, String keyword) {
                List<RecordPenjualan> records = penjualanDAO.getRecordPenjualanByPenjualanId(idPenjualan);
                return records.stream().map(r -> new String[]{
                        r.getNamaProduk(),
                        FormatUtil.formatAngka(r.getJumlah()),
                        r.getSatuan(),
                        "Rp " + FormatUtil.formatAngka(r.getHarga()),
                        "Rp " + FormatUtil.formatAngka(r.getSubtotal()),
                        String.valueOf(r.getIdRecordPenjualan()),
                        String.valueOf(r.getIdRecordPenjualan())
                }).toList();
            }
        });
        tableDetail.setTableActionListener((id, name) -> {
            JOptionPane.showMessageDialog(this, "Detail produk: " + name, "Info", JOptionPane.INFORMATION_MESSAGE);
        });
        return tableDetail;
    }

    private JLabel createInfoLabel(String title, String value) {
        JLabel lblTitle = new JLabel(title);
        lblTitle.setForeground(Theme.TEXT_SECONDARY);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 11));

        JLabel lblValue = new JLabel(value);
        lblValue.setForeground(Theme.TEXT_PRIMARY);
        lblValue.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(lblTitle);
        panel.add(Box.createVerticalStrut(8));
        panel.add(lblValue);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setBackground(Theme.CARD);
        panel.setOpaque(true);

        return lblValue;
    }

    private RoundedPanel createButton(String text, int width, Color textColor, Runnable action) {
        RoundedPanel button = new RoundedPanel(10, Theme.CARD);
        button.setLayout(new BorderLayout());
        button.setPreferredSize(new Dimension(width, 32));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createLineBorder(Theme.BORDER, 1));

        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setForeground(textColor);
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.add(label, BorderLayout.CENTER);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                action.run();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(Theme.SIDEBAR);
                button.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(Theme.CARD);
                button.repaint();
            }
        });

        return button;
    }

    private void handleExportPdf() {
        JOptionPane.showMessageDialog(this, "Fitur export PDF belum tersedia.", "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void loadDetailData() {
        new SwingWorker<Penjualan, Void>() {
            @Override
            protected Penjualan doInBackground() {
                return penjualanDAO.getPenjualanDetail(idPenjualan);
            }

            @Override
            protected void done() {
                try {
                    Penjualan p = get();
                    if (p != null) {
                        lblTanggal.setText(p.getTanggal() != null ? DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.forLanguageTag("id-ID")).format(p.getTanggal().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate()) : "-");
                        lblPelanggan.setText(p.getNamaPelanggan());
                        lblTotal.setText("Rp " + FormatUtil.formatAngka(p.getTotal()));
                        lblKeterangan.setText(p.getKeterangan());
                        tableDetail.updateTableModel();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }
}
