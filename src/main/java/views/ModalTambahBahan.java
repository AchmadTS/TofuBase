package views;

import dao.BahanBakuDAO;
import components.RoundedPanel;
import utils.Theme;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;

public class ModalTambahBahan extends JDialog {

    private final BahanBakuDAO bahanDAO = new BahanBakuDAO();
    private boolean isSaved = false;
    private JTextField txtNama;
    private JComboBox<ComboItem> cbSupplier;
    private JComboBox<String> cbSatuan;
    private JTextField txtSatuanBaru;
    private boolean isSatuanBaruMode = false;
    private JTextField txtQty, txtMinStok, txtHarga;
    private Point initialClick;

    public ModalTambahBahan(Frame parent) {
        super(parent, "Tambah Bahan Baku", true);
        setSize(580, 680);
        setLocationRelativeTo(parent);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));

        UIManager.put("ComboBox.disabledBackground", Theme.CARD);
        UIManager.put("ComboBox.disabledForeground", Theme.TEXT_SECONDARY);

        RoundedPanel mainWrapper = new RoundedPanel(25, Theme.BG);
        mainWrapper.setLayout(new BorderLayout());
        mainWrapper.add(buildHeader(), BorderLayout.NORTH);

        JPanel bodyWrapper = new JPanel(new BorderLayout());
        bodyWrapper.setOpaque(false);
        bodyWrapper.add(createThinLine(), BorderLayout.NORTH);
        bodyWrapper.add(buildForm(), BorderLayout.CENTER);

        mainWrapper.add(bodyWrapper, BorderLayout.CENTER);
        mainWrapper.add(buildFooter(), BorderLayout.SOUTH);
        getContentPane().add(mainWrapper, BorderLayout.CENTER);
        setupEscapeKey();
        loadDropdownData();
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

    private JPanel createThinLine() {
        JPanel line = new JPanel();
        line.setBackground(Theme.BORDER);
        line.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        line.setPreferredSize(new Dimension(Integer.MAX_VALUE, 1));
        line.setAlignmentX(Component.LEFT_ALIGNMENT);
        return line;
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(20, 30, 15, 30));
        header.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
            }
        });

        header.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int thisX = getLocation().x;
                int thisY = getLocation().y;
                int xMoved = e.getX() - initialClick.x;
                int yMoved = e.getY() - initialClick.y;
                setLocation(thisX + xMoved, thisY + yMoved);
            }
        });

        RoundedPanel iconPanel = new RoundedPanel(12, Theme.BLUE_ACCENT);
        iconPanel.setPreferredSize(new Dimension(40, 40));
        iconPanel.setLayout(new BorderLayout());
        JLabel lblPlus = new JLabel("+", SwingConstants.CENTER);
        lblPlus.setFont(new Font("SansSerif", Font.PLAIN, 22));
        lblPlus.setForeground(Color.WHITE);
        iconPanel.add(lblPlus, BorderLayout.CENTER);

        JPanel titleTextPanel = new JPanel(new GridLayout(2, 1, 0, 0));
        titleTextPanel.setOpaque(false);
        titleTextPanel.setBorder(new EmptyBorder(0, 15, 0, 0));

        JLabel title = new JLabel("Tambah Bahan Baku");
        title.setFont(new Font("SansSerif", Font.BOLD, 17));
        title.setForeground(Theme.TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Tambahkan data bahan baku baru ke inventaris");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 11));
        subtitle.setForeground(Theme.TEXT_SECONDARY);

        titleTextPanel.add(title);
        titleTextPanel.add(subtitle);

        JPanel leftHeader = new JPanel(new BorderLayout());
        leftHeader.setOpaque(false);
        leftHeader.add(iconPanel, BorderLayout.WEST);
        leftHeader.add(titleTextPanel, BorderLayout.CENTER);

        RoundedPanel btnClose = new RoundedPanel(10, Theme.CARD);
        btnClose.setPreferredSize(new Dimension(35, 35));
        btnClose.setLayout(new BorderLayout());
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        JLabel lblClose = new JLabel("X", SwingConstants.CENTER);
        lblClose.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblClose.setForeground(Theme.TEXT_SECONDARY);
        btnClose.add(lblClose, BorderLayout.CENTER);
        btnClose.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                btnClose.setBackground(Theme.BORDER);
                btnClose.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnClose.setBackground(Theme.CARD);
                btnClose.repaint();
            }
        });
        header.add(leftHeader, BorderLayout.WEST);
        header.add(btnClose, BorderLayout.EAST);
        return header;
    }

    private JPanel buildForm() {
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setOpaque(false);
        form.setBorder(new EmptyBorder(25, 35, 10, 35));
        form.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtNama = createRawTextField("cth. Kedelai, Garam, Cuka...");
        JPanel grpNama = createFormGroup("NAMA BAHAN", wrapInput(txtNama), null);
        grpNama.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(grpNama);
        form.add(Box.createVerticalStrut(20));

        JPanel row2 = new JPanel(new GridLayout(1, 2, 20, 0));
        row2.setOpaque(false);
        row2.setAlignmentX(Component.LEFT_ALIGNMENT);

        cbSupplier = new JComboBox<>();
        styleComboBox(cbSupplier);
        row2.add(createFormGroup("SUPPLIER", wrapInput(cbSupplier), null));
        row2.add(buildSatuanGroup());

        form.add(row2);
        form.add(Box.createVerticalStrut(30));
        form.add(createThinLine());
        form.add(Box.createVerticalStrut(20));

        JLabel lblSection = new JLabel("📋 DATA STOK & HARGA");
        lblSection.setForeground(Theme.TEXT_SECONDARY);
        lblSection.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblSection.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(lblSection);
        form.add(Box.createVerticalStrut(15));

        JPanel row3 = new JPanel(new GridLayout(1, 2, 20, 0));
        row3.setOpaque(false);
        row3.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtQty = createRawTextField("0");
        setupNumberFormatListener(txtQty, "");

        txtMinStok = createRawTextField("0");
        setupNumberFormatListener(txtMinStok, "");

        row3.add(createFormGroup("QTY (STOK DITAMBAHKAN)", wrapInput(txtQty), "Jumlah ditambahkan ke stok saat ini"));
        row3.add(createFormGroup("MIN. STOK", wrapInput(txtMinStok), "Batas bawah pemicu peringatan"));

        form.add(row3);
        form.add(Box.createVerticalStrut(15));

        txtHarga = createRawTextField("Rp 0");
        setupNumberFormatListener(txtHarga, "Rp ");

        JPanel grpHarga = createFormGroup("HARGA BELI", wrapInput(txtHarga), "Harga per satuan dari supplier");
        grpHarga.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(grpHarga);
        return form;
    }

    private void setupNumberFormatListener(JTextField txt, String prefix) {
        txt.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                int keyCode = e.getKeyCode();
                if (keyCode == java.awt.event.KeyEvent.VK_LEFT || keyCode == java.awt.event.KeyEvent.VK_RIGHT
                        || keyCode == java.awt.event.KeyEvent.VK_UP || keyCode == java.awt.event.KeyEvent.VK_DOWN) {
                    return;
                }

                String rawText = txt.getText().replaceAll("[^\\d]", "");

                if (!rawText.isEmpty()) {
                    try {
                        long number = Long.parseLong(rawText);
                        java.text.NumberFormat formatter = java.text.NumberFormat.getInstance(new java.util.Locale("id", "ID"));
                        txt.setText(prefix + formatter.format(number));
                    } catch (NumberFormatException ex) {
                        // Abaikan kalau angka terlalu panjang
                    }
                } else if (keyCode != java.awt.event.KeyEvent.VK_BACK_SPACE) {
                    txt.setText("");
                }
            }
        });
    }

    private JPanel buildFooter() {
        JPanel footerWrapper = new JPanel(new BorderLayout());
        footerWrapper.setOpaque(false);
        footerWrapper.add(createThinLine(), BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(20, 30, 25, 30));

        RoundedPanel btnBatal = new RoundedPanel(12, Theme.CARD);
        btnBatal.setPreferredSize(new Dimension(90, 40));
        btnBatal.setLayout(new BorderLayout());
        btnBatal.setCursor(new Cursor(Cursor.HAND_CURSOR));
        JLabel lblBatal = new JLabel("Batal", SwingConstants.CENTER);
        lblBatal.setForeground(Theme.TEXT_PRIMARY);
        lblBatal.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnBatal.add(lblBatal, BorderLayout.CENTER);
        btnBatal.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                btnBatal.setBackground(Theme.BORDER);
                btnBatal.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnBatal.setBackground(Theme.CARD);
                btnBatal.repaint();
            }
        });

        RoundedPanel btnSimpan = new RoundedPanel(12, Theme.BLUE_ACCENT);
        btnSimpan.setPreferredSize(new Dimension(180, 40));
        btnSimpan.setLayout(new BorderLayout());
        btnSimpan.setCursor(new Cursor(Cursor.HAND_CURSOR));
        JLabel lblSimpan = new JLabel("💾 Simpan Bahan Baku", SwingConstants.CENTER);
        lblSimpan.setForeground(Color.WHITE);
        lblSimpan.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnSimpan.add(lblSimpan, BorderLayout.CENTER);
        btnSimpan.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleSimpan();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                btnSimpan.setBackground(Theme.BLUE_ACCENT.brighter());
                btnSimpan.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnSimpan.setBackground(Theme.BLUE_ACCENT);
                btnSimpan.repaint();
            }
        });

        buttonPanel.add(btnBatal);
        buttonPanel.add(btnSimpan);
        footerWrapper.add(buttonPanel, BorderLayout.CENTER);
        return footerWrapper;
    }

    private JPanel createFormGroup(String title, JComponent input, String desc) {
        JPanel pnl = new JPanel();
        pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
        pnl.setOpaque(false);
        pnl.setAlignmentX(Component.LEFT_ALIGNMENT);

        String hexColor = String.format("#%06x", (Theme.TEXT_SECONDARY.getRGB() & 0xFFFFFF));
        String titleHtml = "<html><span style='color:" + hexColor + "; font-family:SansSerif; font-size:10px; font-weight:bold;'>" + title + "</span> <span style='color:#FF4747;'>*</span></html>";

        JLabel lblTitle = new JLabel(titleHtml);
        lblTitle.setBorder(new EmptyBorder(0, 0, 5, 0));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        input.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        input.setPreferredSize(new Dimension(Integer.MAX_VALUE, 42));
        input.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnl.add(lblTitle);
        pnl.add(input);

        if (desc != null) {
            JLabel lblDesc = new JLabel(desc);
            lblDesc.setForeground(Theme.TEXT_SECONDARY);
            lblDesc.setFont(new Font("SansSerif", Font.PLAIN, 10));
            lblDesc.setBorder(new EmptyBorder(5, 0, 0, 0));
            lblDesc.setAlignmentX(Component.LEFT_ALIGNMENT);
            pnl.add(lblDesc);
        }
        return pnl;
    }

    private RoundedPanel wrapInput(JComponent inputComp) {
        RoundedPanel wrapper = new RoundedPanel(12, Theme.CARD);
        wrapper.setLayout(new BorderLayout());
        wrapper.setBorder(BorderFactory.createLineBorder(Theme.BORDER, 1));
        wrapper.add(inputComp, BorderLayout.CENTER);
        return wrapper;
    }

    private JTextField createRawTextField(String placeholder) {
        JTextField txt = new JTextField();
        txt.setOpaque(false);
        txt.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        txt.setForeground(Theme.TEXT_PRIMARY);
        txt.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txt.setCaretColor(Theme.TEXT_PRIMARY);
        txt.setText(placeholder);
        txt.setForeground(Theme.TEXT_SECONDARY);
        txt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (txt.getText().equals(placeholder)) {
                    txt.setText("");
                    txt.setForeground(Theme.TEXT_PRIMARY);
                }
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                if (txt.getText().isEmpty()) {
                    txt.setText(placeholder);
                    txt.setForeground(Theme.TEXT_SECONDARY);
                }
            }
        });
        return txt;
    }

    private void styleComboBox(JComboBox<?> cb) {
        cb.setOpaque(true);
        cb.setBackground(Theme.CARD);
        cb.setForeground(Theme.TEXT_PRIMARY);
        cb.setFont(new Font("SansSerif", Font.PLAIN, 13));
        cb.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 5));
        cb.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setBackground(isSelected ? Theme.BORDER : Theme.CARD);
                label.setForeground(Theme.TEXT_PRIMARY);
                label.setOpaque(true);
                return label;
            }
        });

        cb.setUI(new BasicComboBoxUI() {
            @Override
            public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
                g.setColor(Theme.CARD);
                g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
            }

            @Override
            public void paintCurrentValue(Graphics g, Rectangle bounds, boolean hasFocus) {
                ListCellRenderer renderer = comboBox.getRenderer();
                Component c = renderer.getListCellRendererComponent(listBox, comboBox.getSelectedItem(), -1, false, false);
                c.setFont(comboBox.getFont());
                c.setForeground(comboBox.isEnabled() ? Theme.TEXT_PRIMARY : Theme.TEXT_SECONDARY);
                c.setBackground(Theme.CARD);

                boolean shouldValidate = false;
                if (c instanceof JPanel) {
                    shouldValidate = true;
                }
                currentValuePane.paintComponent(g, c, comboBox, bounds.x, bounds.y, bounds.width, bounds.height, shouldValidate);
            }

            @Override
            protected JButton createArrowButton() {
                JButton btn = new JButton() {
                    @Override
                    public void paint(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(comboBox.isEnabled() ? Theme.TEXT_SECONDARY : new Color(138, 146, 166, 100));
                        g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                        int w = getWidth(), h = getHeight();
                        int[] x = {w / 2 - 4, w / 2, w / 2 + 4}, y = {h / 2 - 2, h / 2 + 3, h / 2 - 2};
                        g2.drawPolyline(x, y, 3);
                        g2.dispose();
                    }
                };
                btn.setOpaque(false);
                btn.setContentAreaFilled(false);
                btn.setBorder(BorderFactory.createEmptyBorder());
                return btn;
            }
        });
    }

    private JPanel buildSatuanGroup() {
        JPanel pnlSatuan = new JPanel();
        pnlSatuan.setLayout(new BoxLayout(pnlSatuan, BoxLayout.Y_AXIS));
        pnlSatuan.setOpaque(false);
        pnlSatuan.setAlignmentX(Component.LEFT_ALIGNMENT);

        String hexColor = String.format("#%06x", (Theme.TEXT_SECONDARY.getRGB() & 0xFFFFFF));
        String titleHtml = "<html><span style='color:" + hexColor + "; font-family:SansSerif; font-size:10px; font-weight:bold;'>SATUAN</span> <span style='color:#FF4747;'>*</span></html>";
        JLabel lblTitle = new JLabel(titleHtml);
        lblTitle.setBorder(new EmptyBorder(0, 0, 5, 0));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel inputWrapper = new JPanel(new CardLayout());
        inputWrapper.setOpaque(false);
        inputWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        inputWrapper.setPreferredSize(new Dimension(Integer.MAX_VALUE, 42));

        cbSatuan = new JComboBox<>();
        styleComboBox(cbSatuan);
        txtSatuanBaru = createRawTextField("Ketik satuan baru...");

        inputWrapper.add(wrapInput(cbSatuan), "COMBO");
        inputWrapper.add(wrapInput(txtSatuanBaru), "TEXT");

        JPanel toggleContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        toggleContainer.setOpaque(false);
        toggleContainer.setAlignmentX(Component.LEFT_ALIGNMENT);

        String hexPrimary = String.format("#%06x", (Theme.BLUE_ACCENT.getRGB() & 0xFFFFFF));
        String hexDanger = String.format("#%06x", (Theme.RED.getRGB() & 0xFFFFFF));

        JLabel lblToggle = new JLabel("<html><u style='color:" + hexPrimary + ";'>tambahkan satuan baru</u></html>");
        lblToggle.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblToggle.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblToggle.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                isSatuanBaruMode = !isSatuanBaruMode;
                CardLayout cl = (CardLayout) (inputWrapper.getLayout());
                if (isSatuanBaruMode) {
                    cl.show(inputWrapper, "TEXT");
                    lblToggle.setText("<html><u style='color:" + hexDanger + ";'>batal, pilih dari daftar</u></html>");
                    txtSatuanBaru.requestFocus();
                } else {
                    cl.show(inputWrapper, "COMBO");
                    lblToggle.setText("<html><u style='color:" + hexPrimary + ";'>tambahkan satuan baru</u></html>");
                }
            }
        });

        toggleContainer.add(lblToggle);
        pnlSatuan.add(lblTitle);
        pnlSatuan.add(inputWrapper);
        pnlSatuan.add(toggleContainer);
        return pnlSatuan;
    }

    private void loadDropdownData() {
        cbSupplier.removeAllItems();
        cbSupplier.addItem(new ComboItem(-1, "Memuat data..."));
        cbSupplier.setEnabled(false);
        cbSatuan.removeAllItems();
        cbSatuan.addItem("Memuat data...");
        cbSatuan.setEnabled(false);

        new Thread(() -> {
            Map<Integer, String> suppliers = bahanDAO.getSupplierList();
            List<String> satuans = bahanDAO.getSatuanList();
            SwingUtilities.invokeLater(() -> {
                cbSupplier.removeAllItems();
                cbSupplier.addItem(new ComboItem(-1, "Pilih supplier..."));
                for (Map.Entry<Integer, String> entry : suppliers.entrySet()) {
                    cbSupplier.addItem(new ComboItem(entry.getKey(), entry.getValue()));
                }
                cbSupplier.setEnabled(true);
                cbSatuan.removeAllItems();
                cbSatuan.addItem("Pilih satuan...");
                for (String s : satuans) {
                    cbSatuan.addItem(s);
                }
                cbSatuan.setEnabled(true);
            });
        }).start();
    }

    private void handleSimpan() {
        try {
            String nama = txtNama.getText().trim();
            if (nama.isEmpty() || nama.startsWith("cth.")) {
                throw new Exception("Nama bahan tidak boleh kosong!");
            }

            ComboItem sup = (ComboItem) cbSupplier.getSelectedItem();
            if (sup == null || sup.getKey() == -1) {
                throw new Exception("Silakan pilih Supplier yang valid!");
            }

            String satuan = "";
            if (isSatuanBaruMode) {
                satuan = txtSatuanBaru.getText().trim();
                if (satuan.isEmpty() || satuan.equalsIgnoreCase("Ketik satuan baru...")) {
                    throw new Exception("Kolom satuan baru tidak boleh kosong!");
                }
            } else {
                satuan = cbSatuan.getSelectedItem() != null ? cbSatuan.getSelectedItem().toString() : "";
                if (satuan.isEmpty() || satuan.contains("Pilih") || satuan.contains("Memuat")) {
                    throw new Exception("Silakan pilih satuan dari daftar!");
                }
            }

            String rawQty = txtQty.getText().replaceAll("[^\\d]", "");
            String rawMin = txtMinStok.getText().replaceAll("[^\\d]", "");
            String rawHarga = txtHarga.getText().replaceAll("[^\\d]", "");

            if (rawQty.isEmpty()) {
                throw new Exception("QTY (Stok) harus diisi dengan angka!");
            }
            if (rawMin.isEmpty()) {
                throw new Exception("Minimal Stok harus diisi dengan angka!");
            }
            if (rawHarga.isEmpty()) {
                throw new Exception("Harga Beli harus diisi dengan angka!");
            }

            double qty = Double.parseDouble(rawQty);
            double min = Double.parseDouble(rawMin);
            double harga = Double.parseDouble(rawHarga);

            if (qty <= 0) {
                throw new Exception("QTY (Stok) harus lebih dari 0!");
            }
            if (min <= 0) {
                throw new Exception("Minimal Stok harus lebih dari 0!");
            }
            if (harga <= 0) {
                throw new Exception("Harga Beli harus lebih dari 0!");
            }

            if (bahanDAO.simpanAtauUpdateBahan(nama, sup.getKey(), satuan, qty, min, harga)) {
                isSaved = true;
                dispose();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Peringatan Validasi", JOptionPane.WARNING_MESSAGE);
        }
    }

    public boolean isSaved() {
        return isSaved;
    }

    class ComboItem {

        private int key;
        private String value;

        public ComboItem(int key, String value) {
            this.key = key;
            this.value = value;
        }

        public int getKey() {
            return key;
        }

        @Override
        public String toString() {
            return value;
        }
    }
}
