package views;

import components.ModernScrollBarUI;
import components.RoundedPanel;
import utils.Theme;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BahanBaku extends JPanel {

    public BahanBaku(String userName, String userRole) {
        setLayout(new BorderLayout());
        setBackground(Theme.BG);
        add(createMainContent(), BorderLayout.CENTER);
    }

    private JPanel createMainContent() {
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(Theme.BG);

        // --- HEADER ---
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.BG);
        header.setBorder(new EmptyBorder(20, 30, 10, 30));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setBackground(Theme.BG);
        JLabel headerTitle = new JLabel("Bahan Baku");
        headerTitle.setFont(new Font("SansSerif", Font.BOLD, 28));
        headerTitle.setForeground(Theme.TEXT_PRIMARY);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", Locale.forLanguageTag("id-ID"));
        JLabel headerDate = new JLabel(LocalDate.now().format(formatter));
        headerDate.setFont(new Font("SansSerif", Font.PLAIN, 14));
        headerDate.setForeground(Theme.TEXT_SECONDARY);
        new Timer(60_000, e -> headerDate.setText(LocalDate.now().format(formatter))).start();

        titlePanel.add(headerTitle);
        titlePanel.add(headerDate);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Theme.BG);

        RoundedPanel btnExport = createHeaderButton("Export PDF", false);
        RoundedPanel btnAdd = createHeaderButton("+ Tambah Data", true);

        buttonPanel.add(btnExport);
        buttonPanel.add(btnAdd);

        header.add(titlePanel, BorderLayout.WEST);
        header.add(buttonPanel, BorderLayout.EAST);

        // --- CONTAINER UTAMA ---
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(Theme.BG);
        container.setBorder(new EmptyBorder(10, 30, 30, 30));

        // --- TOP CARDS ---
        JPanel topCardsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        topCardsPanel.setBackground(Theme.BG);
        topCardsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

        JLabel lblTotalAset = createAnimatedLabel();
        JLabel lblStatusAset = createStatusLabel("Total nilai gudang", Theme.TEXT_SECONDARY);

        JLabel lblStokKedelai = createAnimatedLabel();
        JLabel lblStatusKedelai = createStatusLabel("Menghitung...", Theme.TEXT_SECONDARY);

        JLabel lblPemasok = createAnimatedLabel();
        JLabel lblStatusPemasok = createStatusLabel("Terdaftar unik", Theme.TEXT_SECONDARY);

        topCardsPanel.add(createStatCard("NILAI ASET STOK", lblTotalAset, "Jt", lblStatusAset));
        topCardsPanel.add(createStatCard("STOK KEDELAI", lblStokKedelai, "kg", lblStatusKedelai));
        topCardsPanel.add(createStatCard("PEMASOK AKTIF", lblPemasok, "orang", lblStatusPemasok));

        new Thread(() -> {
            try {
                Connection conn = utils.DatabaseConfig.getKoneksi();
                Statement stmt = conn.createStatement();

                // Total Aset Stok
                ResultSet rsAset = stmt.executeQuery("SELECT SUM(stok * harga_beli) AS total FROM bahan_baku");
                String asetStr = "0";
                if (rsAset.next() && rsAset.getString("total") != null) {
                    asetStr = String.format(Locale.forLanguageTag("id-ID"), "%.1f",
                            rsAset.getDouble("total") / 1000000.0);
                }

                // Stok Kedelai
                ResultSet rsKed = stmt.executeQuery(
                        "SELECT SUM(stok) AS total_stok, MAX(min_stok) AS batas_stok FROM bahan_baku WHERE nama LIKE '%Kedelai%'");
                String kedelaiStr = "0";
                String statusKedTxt = "Tidak ada data";
                Color statusKedColor = Theme.TEXT_SECONDARY;

                if (rsKed.next() && rsKed.getString("total_stok") != null) {
                    double stok = rsKed.getDouble("total_stok");
                    double min = rsKed.getDouble("batas_stok");

                    kedelaiStr = (stok == (long) stok) ? String.valueOf((long) stok) : String.valueOf(stok);

                    if (stok <= min / 2) {
                        statusKedTxt = "▼ Kritis";
                        statusKedColor = Theme.RED;
                    } else if (stok <= min) {
                        statusKedTxt = "▼ Rendah";
                        statusKedColor = Theme.WARNING;
                    } else {
                        statusKedTxt = "▲ Aman";
                        statusKedColor = Theme.GREEN;
                    }
                }

                // Jumlah Supplier Terdaftar
                ResultSet rsSup = stmt.executeQuery("SELECT COUNT(DISTINCT id_supplier) AS total FROM bahan_baku");
                String supStr = "0";
                if (rsSup.next()) {
                    supStr = String.valueOf(rsSup.getInt("total"));
                }

                final String fAset = asetStr;
                final String fKedelai = kedelaiStr;
                final String fStatusK = statusKedTxt;
                final Color fColorK = statusKedColor;
                final String fSup = supStr;

                SwingUtilities.invokeLater(() -> {
                    lblTotalAset.setText(fAset);
                    lblStokKedelai.setText(fKedelai);
                    lblStatusKedelai.setText(fStatusK);
                    lblStatusKedelai.setForeground(fColorK);
                    lblPemasok.setText(fSup);
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        // --- TABEL ACTIVITY ---
        String[] bahanHeaders = { "ID", "Nama Bahan", "Stok Tersedia", "Satuan", "Rata-rata harga Beli", "Min. Stok",
                "Status Stok" };
        components.ActivityTable tableBahan = new components.ActivityTable("Daftar Stok Bahan Baku", bahanHeaders, 6,
                new components.ActivityTable.DataProvider() {

                    private String buildWhereClause(String keyword) {
                        if (keyword.isEmpty()) {
                            return "";
                        }
                        return "WHERE nama LIKE '" + keyword + "%' OR id_bahan LIKE '" + keyword + "%' ";
                    }

                    @Override
                    public int getTotalRowCount(String keyword) {
                        try {
                            Connection conn = utils.DatabaseConfig.getKoneksi();
                            Statement stmt = conn.createStatement();
                            String query = "SELECT COUNT(*) AS total FROM (SELECT 1 FROM bahan_baku "
                                    + buildWhereClause(keyword) + " GROUP BY nama, satuan) AS sub";
                            ResultSet rs = stmt.executeQuery(query);
                            if (rs.next()) {
                                return rs.getInt("total");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return 0;
                    }

                    @Override
                    public List<String[]> getPageData(int limit, int offset, String keyword) {
                        List<String[]> data = new ArrayList<>();
                        try {
                            Connection conn = utils.DatabaseConfig.getKoneksi();
                            Statement stmt = conn.createStatement();
                            String query = "SELECT MIN(b.id_bahan) AS id_bahan, b.nama, b.satuan, "
                                    + "SUM(b.stok) AS total_stok, "
                                    + "AVG(b.harga_beli) AS rata_harga, "
                                    + "MAX(b.min_stok) AS batas_stok "
                                    + "FROM ("
                                    + "   SELECT nama, satuan FROM bahan_baku "
                                    + buildWhereClause(keyword)
                                    + "   GROUP BY nama, satuan "
                                    + "   ORDER BY nama ASC LIMIT " + limit + " OFFSET " + offset
                                    + ") AS filter_b "
                                    + "JOIN bahan_baku b ON b.nama = filter_b.nama AND b.satuan = filter_b.satuan "
                                    + "GROUP BY b.nama, b.satuan "
                                    + "ORDER BY b.nama ASC";
                            ResultSet rs = stmt.executeQuery(query);
                            java.text.DecimalFormat df = new java.text.DecimalFormat("#,###");
                            while (rs.next()) {
                                String id = "BHN-" + rs.getInt("id_bahan");
                                String nama = rs.getString("nama");
                                String satuan = rs.getString("satuan");

                                double stok = rs.getDouble("total_stok");
                                double hargaBeliAvg = rs.getDouble("rata_harga");
                                double minStok = rs.getDouble("batas_stok");

                                String harga = "Rp " + df.format(hargaBeliAvg);
                                String stokStr = (stok == (long) stok) ? String.valueOf((long) stok)
                                        : String.valueOf(stok);
                                String minStokStr = (minStok == (long) minStok) ? String.valueOf((long) minStok)
                                        : String.valueOf(minStok);
                                String status = "Aman";
                                if (stok <= minStok / 2) {
                                    status = "Kritis";
                                } else if (stok <= minStok) {
                                    status = "Rendah";
                                }

                                data.add(new String[] { id, nama, stokStr, satuan, harga, minStokStr, status });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return data;
                    }
                });

        container.add(topCardsPanel);
        container.add(Box.createVerticalStrut(20));
        container.add(tableBahan);

        JScrollPane mainScroll = new JScrollPane(container);
        mainScroll.setBorder(null);
        mainScroll.getViewport().setBackground(Theme.BG);
        mainScroll.getVerticalScrollBar().setUnitIncrement(16);
        mainScroll.getVerticalScrollBar().setUI(new ModernScrollBarUI());

        mainContent.add(header, BorderLayout.NORTH);
        mainContent.add(mainScroll, BorderLayout.CENTER);

        return mainContent;
    }

    private RoundedPanel createHeaderButton(String text, boolean isPrimary) {
        Color defaultBg = isPrimary ? Theme.BLUE_ACCENT : Theme.BG;
        Color hoverBg = isPrimary ? Theme.BLUE_ACCENT.darker() : Theme.CARD;
        Color borderColor = isPrimary ? Theme.BLUE_ACCENT : Theme.BORDER;

        RoundedPanel panel = new RoundedPanel(10, defaultBg);
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createLineBorder(borderColor, 1));
        panel.setPreferredSize(new Dimension(130, 35));
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setForeground(isPrimary ? Color.WHITE : Theme.TEXT_PRIMARY);
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        panel.add(label, BorderLayout.CENTER);
        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                panel.setBackground(hoverBg);
                if (!isPrimary) {
                    panel.setBorder(BorderFactory.createLineBorder(Theme.TEXT_SECONDARY, 1));
                }
                panel.repaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                panel.setBackground(defaultBg);
                if (!isPrimary) {
                    panel.setBorder(BorderFactory.createLineBorder(borderColor, 1));
                }
                panel.repaint();
            }
        });

        return panel;
    }

    private JLabel createAnimatedLabel() {
        JLabel l = new JLabel("...", SwingConstants.CENTER);
        l.setForeground(Theme.TEXT_PRIMARY);
        l.setFont(new Font("SansSerif", Font.BOLD, 32));
        return l;
    }

    private JLabel createStatusLabel(String text, Color color) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setForeground(color);
        l.setFont(new Font("SansSerif", Font.PLAIN, 12));
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        return l;
    }

    private JPanel createStatCard(String title, JLabel lblValue, String unit, JLabel lblStatus) {
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

        valuePanel.add(lblValue);
        valuePanel.add(new JLabel(unit) {
            {
                setForeground(Theme.TEXT_SECONDARY);
            }
        });

        card.add(lblTitle);
        card.add(Box.createVerticalStrut(10));
        card.add(valuePanel);
        card.add(Box.createVerticalGlue());
        card.add(lblStatus);
        return card;
    }
}
