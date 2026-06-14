package views;

import dao.BahanBakuDAO;
import components.RoundedPanel;
import components.ModernScrollBarUI;
import utils.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;
import models.BahanBakuModel;

public class ModalTambahBahan extends JDialog {

    private static final int MODAL_WIDTH = 580;
    private static final int MODAL_HEIGHT = 680;
    private static final int DEBOUNCE_DELAY_MS = 500;
    private static final String TITLE = "Tambah Bahan Baku";
    private static final String SUBTITLE = "Tambahkan data bahan baku baru ke inventaris";
    private static final String PLACEHOLDER_NAMA = "cth. Kedelai, Garam, Cuka...";
    private static final String PLACEHOLDER_SATUAN = "Ketik satuan baru...";
    private static final String MSG_LOADING = "Memuat data...";
    private static final String MSG_SELECT_SUPPLIER = "Pilih supplier...";
    private static final String MSG_SELECT_SATUAN = "Pilih satuan...";
    private final BahanBakuDAO bahanDAO = new BahanBakuDAO();
    private boolean isSaved = false;
    private boolean isSatuanBaruMode = false;
    private Point initialClick;
    private JTextField txtNama, txtSatuanBaru, txtQty, txtMinStok, txtHarga;
    private JComboBox<ComboItem> cbSupplier;
    private JComboBox<String> cbSatuan;
    private JPanel satuanInputWrapper;
    private JLabel lblToggleSatuan;

    public ModalTambahBahan(Frame parent) {
        super(parent, TITLE, true);
        setupDialogProperties(parent);
        setupGlobalUI();
        buildMainLayout();
        setupEscapeKey();
        loadDropdownData();
    }

    private void setupDialogProperties(Frame parent) {
        setSize(MODAL_WIDTH, MODAL_HEIGHT);
        setLocationRelativeTo(parent);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));
    }

    private void setupGlobalUI() {
        UIManager.put("ComboBox.disabledBackground", Theme.CARD);
        UIManager.put("ComboBox.disabledForeground", Theme.TEXT_SECONDARY);
    }

    private void buildMainLayout() {
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
        header.setBorder(new EmptyBorder(20, 30, 15, 30));

        enableWindowDrag(header);

        JPanel leftHeader = new JPanel(new BorderLayout());
        leftHeader.setOpaque(false);
        leftHeader.add(createHeaderIcon(), BorderLayout.WEST);
        leftHeader.add(createHeaderTitleText(), BorderLayout.CENTER);

        header.add(leftHeader, BorderLayout.WEST);
        header.add(createCloseButton(), BorderLayout.EAST);
        return header;
    }

    private JPanel buildForm() {
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setOpaque(false);
        form.setBorder(new EmptyBorder(25, 35, 10, 35));
        form.setAlignmentX(Component.LEFT_ALIGNMENT);

        form.add(buildBasicInfoSection());
        form.add(Box.createVerticalStrut(30));
        form.add(createThinLine());
        form.add(Box.createVerticalStrut(20));
        form.add(buildStockAndPriceSection());

        return form;
    }

    private JPanel buildBasicInfoSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setOpaque(false);
        section.setAlignmentX(Component.LEFT_ALIGNMENT);

        // NAMA BAHAN
        txtNama = createRawTextField(PLACEHOLDER_NAMA);
        setupNamaDebouncer();
        section.add(createFormGroup("NAMA BAHAN", wrapInput(txtNama), null));
        section.add(Box.createVerticalStrut(20));

        // SUPPLIER & SATUAN
        JPanel row = new JPanel(new GridLayout(1, 2, 20, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        cbSupplier = new JComboBox<>();
        styleComboBox(cbSupplier);
        row.add(createFormGroup("SUPPLIER", wrapInput(cbSupplier), null));
        row.add(buildSatuanGroup());

        section.add(row);
        return section;
    }

    private JPanel buildSatuanGroup() {
        JPanel pnlSatuan = new JPanel();
        pnlSatuan.setLayout(new BoxLayout(pnlSatuan, BoxLayout.Y_AXIS));
        pnlSatuan.setOpaque(false);
        pnlSatuan.setAlignmentX(Component.LEFT_ALIGNMENT);

        pnlSatuan.add(createRequiredLabel("SATUAN"));

        satuanInputWrapper = new JPanel(new CardLayout());
        satuanInputWrapper.setOpaque(false);
        satuanInputWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        satuanInputWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        cbSatuan = new JComboBox<>();
        styleComboBox(cbSatuan);
        txtSatuanBaru = createRawTextField(PLACEHOLDER_SATUAN);

        satuanInputWrapper.add(wrapInput(cbSatuan), "COMBO");
        satuanInputWrapper.add(wrapInput(txtSatuanBaru), "TEXT");

        JPanel toggleContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        toggleContainer.setOpaque(false);
        toggleContainer.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblToggleSatuan = new JLabel();
        lblToggleSatuan.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblToggleSatuan.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblToggleSatuan.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                toggleSatuanMode();
            }
        });

        updateToggleSatuanLabel();
        toggleContainer.add(lblToggleSatuan);
        pnlSatuan.add(satuanInputWrapper);
        pnlSatuan.add(toggleContainer);
        return pnlSatuan;
    }

    private JPanel buildStockAndPriceSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setOpaque(false);
        section.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSection = new JLabel("📋 DATA STOK & HARGA");
        lblSection.setForeground(Theme.TEXT_SECONDARY);
        lblSection.setFont(new Font("SansSerif", Font.BOLD, 12));
        section.add(lblSection);
        section.add(Box.createVerticalStrut(15));

        // QTY & MIN STOK
        JPanel row = new JPanel(new GridLayout(1, 2, 20, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtQty = createRawTextField("0");
        setupNumberFormatListener(txtQty, "");
        row.add(createFormGroup("QTY (STOK DITAMBAHKAN)", wrapInput(txtQty), "Jumlah ditambahkan ke stok saat ini"));

        txtMinStok = createRawTextField("0");
        setupNumberFormatListener(txtMinStok, "");
        row.add(createFormGroup("MIN. STOK", wrapInput(txtMinStok), "Batas bawah pemicu peringatan"));

        section.add(row);
        section.add(Box.createVerticalStrut(15));

        // HARGA BELI
        txtHarga = createRawTextField("Rp 0");
        setupNumberFormatListener(txtHarga, "Rp ");
        section.add(createFormGroup("HARGA BELI", wrapInput(txtHarga), "Harga per satuan dari supplier"));

        return section;
    }

    private JPanel buildFooter() {
        JPanel footerWrapper = new JPanel(new BorderLayout());
        footerWrapper.setOpaque(false);
        footerWrapper.add(createThinLine(), BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(20, 30, 25, 30));

        buttonPanel.add(createGhostButton("Batal", this::dispose));
        buttonPanel.add(createPrimaryButton("💾 Simpan Bahan Baku", this::handleSimpan));

        footerWrapper.add(buttonPanel, BorderLayout.CENTER);
        return footerWrapper;
    }

    private void loadDropdownData() {
        cbSupplier.removeAllItems();
        cbSupplier.addItem(new ComboItem(-1, MSG_LOADING));
        cbSupplier.setEnabled(false);

        cbSatuan.removeAllItems();
        cbSatuan.addItem(MSG_LOADING);
        cbSatuan.setEnabled(false);

        new SwingWorker<DropdownDataDto, Void>() {
            @Override
            protected DropdownDataDto doInBackground() {
                return new DropdownDataDto(bahanDAO.getSupplierList(), bahanDAO.getSatuanList());
            }

            @Override
            protected void done() {
                try {
                    DropdownDataDto data = get();

                    cbSupplier.removeAllItems();
                    cbSupplier.addItem(new ComboItem(-1, MSG_SELECT_SUPPLIER));
                    for (Map.Entry<Integer, String> entry : data.suppliers.entrySet()) {
                        cbSupplier.addItem(new ComboItem(entry.getKey(), entry.getValue()));
                    }
                    cbSupplier.setEnabled(true);

                    cbSatuan.removeAllItems();
                    cbSatuan.addItem(MSG_SELECT_SATUAN);
                    for (String s : data.satuans) {
                        cbSatuan.addItem(s);
                    }
                    cbSatuan.setEnabled(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    private void checkBahanExist(String nama) {
        if (nama.isEmpty() || nama.equals(PLACEHOLDER_NAMA)) {
            resetAutofill();
            return;
        }

        new SwingWorker<BahanBakuModel, Void>() { // <-- Return Model
            @Override
            protected BahanBakuModel doInBackground() {
                return bahanDAO.cekDetailBahan(nama);
            }

            @Override
            protected void done() {
                try {
                    BahanBakuModel detail = get();
                    if (detail != null) {
                        applyAutofill(detail); // <-- Kirim Model
                    } else {
                        resetAutofill();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    private void applyAutofill(BahanBakuModel detail) { // <-- Terima Model
        if (isSatuanBaruMode) {
            toggleSatuanMode();
        }

        cbSatuan.setSelectedItem(detail.getSatuan()); // <-- Pakai Getter
        cbSatuan.setEnabled(false);

        java.text.NumberFormat formatter = java.text.NumberFormat.getInstance(new java.util.Locale("id", "ID"));
        txtMinStok.setText(formatter.format(detail.getMinStok())); // <-- Pakai Getter
        txtMinStok.setEnabled(false);

        if (lblToggleSatuan != null) {
            lblToggleSatuan.setVisible(false);
        }
    }

    private void resetAutofill() {
        cbSatuan.setEnabled(true);
        txtMinStok.setEnabled(true);

        if (lblToggleSatuan != null) {
            lblToggleSatuan.setVisible(true);
            updateToggleSatuanLabel();
        }
        revalidate();
        repaint();
    }

    private void handleSimpan() {
        try {
            validateInput();
            saveToDatabase();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Peringatan Validasi", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan sistem: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void validateInput() {
        String nama = txtNama.getText().trim();
        if (nama.isEmpty() || nama.equals(PLACEHOLDER_NAMA)) {
            throw new IllegalArgumentException("Nama bahan tidak boleh kosong!");
        }

        ComboItem sup = (ComboItem) cbSupplier.getSelectedItem();
        if (sup == null || sup.getKey() == -1) {
            throw new IllegalArgumentException("Silakan pilih Supplier yang valid!");
        }

        if (isSatuanBaruMode) {
            String satuan = txtSatuanBaru.getText().trim();
            if (satuan.isEmpty() || satuan.equalsIgnoreCase(PLACEHOLDER_SATUAN)) {
                throw new IllegalArgumentException("Kolom satuan baru tidak boleh kosong!");
            }
        } else {
            String satuan = cbSatuan.getSelectedItem() != null ? cbSatuan.getSelectedItem().toString() : "";
            if (satuan.isEmpty() || satuan.equals(MSG_SELECT_SATUAN) || satuan.equals(MSG_LOADING)) {
                throw new IllegalArgumentException("Silakan pilih satuan dari daftar!");
            }
        }

        validateNumericInput(txtQty.getText(), "QTY (Stok)");
        validateNumericInput(txtMinStok.getText(), "Minimal Stok");
        validateNumericInput(txtHarga.getText(), "Harga Beli");
    }

    private void validateNumericInput(String text, String fieldName) {
        String rawNumber = text.replaceAll("[^\\d]", "");
        if (rawNumber.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " harus diisi dengan angka!");
        }
        if (Double.parseDouble(rawNumber) <= 0) {
            throw new IllegalArgumentException(fieldName + " harus lebih dari 0!");
        }
    }

    private void saveToDatabase() {
        String nama = txtNama.getText().trim();
        ComboItem sup = (ComboItem) cbSupplier.getSelectedItem();
        String satuan = isSatuanBaruMode ? txtSatuanBaru.getText().trim() : cbSatuan.getSelectedItem().toString();

        double qty = Double.parseDouble(txtQty.getText().replaceAll("[^\\d]", ""));
        double min = Double.parseDouble(txtMinStok.getText().replaceAll("[^\\d]", ""));
        double harga = Double.parseDouble(txtHarga.getText().replaceAll("[^\\d]", ""));

        // <-- BUNGKUS DATA KE DALAM MODEL -->
        BahanBakuModel bahanBaru = new BahanBakuModel();
        bahanBaru.setNama(nama);
        bahanBaru.setIdSupplier(sup.getKey());
        bahanBaru.setSatuan(satuan);
        bahanBaru.setStok(qty);
        bahanBaru.setMinStok(min);
        bahanBaru.setHargaBeli(harga);

        if (bahanDAO.insertBahanBaru(bahanBaru)) { // <-- Kirim Model ke DAO
            isSaved = true;
            dispose();
        } else {
            throw new RuntimeException("Gagal menyisipkan data transaksi ke database.");
        }
    }

    private void setupNamaDebouncer() {
        Timer checkTimer = new Timer(DEBOUNCE_DELAY_MS, e -> checkBahanExist(txtNama.getText().trim()));
        checkTimer.setRepeats(false);
        txtNama.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                checkTimer.restart();
            }

            public void removeUpdate(DocumentEvent e) {
                checkTimer.restart();
            }

            public void changedUpdate(DocumentEvent e) {
                checkTimer.restart();
            }
        });
    }

    private void toggleSatuanMode() {
        isSatuanBaruMode = !isSatuanBaruMode;
        CardLayout cl = (CardLayout) (satuanInputWrapper.getLayout());
        cl.show(satuanInputWrapper, isSatuanBaruMode ? "TEXT" : "COMBO");
        if (isSatuanBaruMode) {
            txtSatuanBaru.requestFocus();
        }
        updateToggleSatuanLabel();
    }

    private void updateToggleSatuanLabel() {
        String hexPrimary = String.format("#%06x", (Theme.BLUE_ACCENT.getRGB() & 0xFFFFFF));
        String hexDanger = String.format("#%06x", (Theme.RED.getRGB() & 0xFFFFFF));

        if (isSatuanBaruMode) {
            lblToggleSatuan.setText("<html><u style='color:" + hexDanger + ";'>batal, pilih dari daftar</u></html>");
        } else {
            lblToggleSatuan.setText("<html><u style='color:" + hexPrimary + ";'>tambahkan satuan baru</u></html>");
        }
    }

    private void setupNumberFormatListener(JTextField txt, String prefix) {
        txt.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                int key = e.getKeyCode();
                if (key == java.awt.event.KeyEvent.VK_LEFT || key == java.awt.event.KeyEvent.VK_RIGHT
                        || key == java.awt.event.KeyEvent.VK_UP || key == java.awt.event.KeyEvent.VK_DOWN) {
                    return;
                }

                String rawText = txt.getText().replaceAll("[^\\d]", "");
                if (!rawText.isEmpty()) {
                    try {
                        long number = Long.parseLong(rawText);
                        java.text.NumberFormat formatter = java.text.NumberFormat.getInstance(new java.util.Locale("id", "ID"));
                        txt.setText(prefix + formatter.format(number));
                    } catch (NumberFormatException ignored) {
                    }
                } else if (key != java.awt.event.KeyEvent.VK_BACK_SPACE) {
                    txt.setText("");
                }
            }
        });
    }

    private JPanel createFormGroup(String title, JComponent input, String desc) {
        JPanel pnl = new JPanel();
        pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
        pnl.setOpaque(false);
        pnl.setAlignmentX(Component.LEFT_ALIGNMENT);

        pnl.add(createRequiredLabel(title));

        input.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        input.setPreferredSize(new Dimension(Integer.MAX_VALUE, 42));
        input.setAlignmentX(Component.LEFT_ALIGNMENT);
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

    private JLabel createRequiredLabel(String title) {
        String hexColor = String.format("#%06x", (Theme.TEXT_SECONDARY.getRGB() & 0xFFFFFF));
        String titleHtml = "<html><span style='color:" + hexColor + "; font-family:SansSerif; font-size:10px; font-weight:bold;'>" + title + "</span> <span style='color:#FF4747;'>*</span></html>";
        JLabel lblTitle = new JLabel(titleHtml);
        lblTitle.setBorder(new EmptyBorder(0, 0, 5, 0));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lblTitle;
    }

    private RoundedPanel wrapInput(JComponent inputComp) {
        RoundedPanel wrapper = new RoundedPanel(12, Theme.CARD);
        wrapper.setLayout(new BorderLayout());
        wrapper.setBorder(BorderFactory.createLineBorder(Theme.BORDER, 1));
        wrapper.add(inputComp, BorderLayout.CENTER);
        return wrapper;
    }

    private JTextField createRawTextField(String placeholder) {
        JTextField txt = new JTextField(placeholder);
        txt.setOpaque(false);
        txt.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        txt.setForeground(Theme.TEXT_SECONDARY);
        txt.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txt.setCaretColor(Theme.TEXT_PRIMARY);

        txt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (txt.getText().equals(placeholder)) {
                    txt.setText("");
                    txt.setForeground(Theme.TEXT_PRIMARY);
                }
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                if (txt.getText().trim().isEmpty()) {
                    txt.setText(placeholder);
                    txt.setForeground(Theme.TEXT_SECONDARY);
                }
            }
        });
        return txt;
    }

    private JPanel createThinLine() {
        JPanel line = new JPanel();
        line.setBackground(Theme.BORDER);
        line.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        line.setPreferredSize(new Dimension(Integer.MAX_VALUE, 1));
        line.setAlignmentX(Component.LEFT_ALIGNMENT);
        return line;
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
        iconPanel.setPreferredSize(new Dimension(40, 40));
        iconPanel.setLayout(new BorderLayout());
        JLabel lblPlus = new JLabel("+", SwingConstants.CENTER);
        lblPlus.setFont(new Font("SansSerif", Font.PLAIN, 22));
        lblPlus.setForeground(Color.WHITE);
        iconPanel.add(lblPlus, BorderLayout.CENTER);
        return iconPanel;
    }

    private JPanel createHeaderTitleText() {
        JPanel titleTextPanel = new JPanel(new GridLayout(2, 1, 0, 0));
        titleTextPanel.setOpaque(false);
        titleTextPanel.setBorder(new EmptyBorder(0, 15, 0, 0));

        JLabel title = new JLabel(TITLE);
        title.setFont(new Font("SansSerif", Font.BOLD, 17));
        title.setForeground(Theme.TEXT_PRIMARY);

        JLabel subtitle = new JLabel(SUBTITLE);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 11));
        subtitle.setForeground(Theme.TEXT_SECONDARY);

        titleTextPanel.add(title);
        titleTextPanel.add(subtitle);
        return titleTextPanel;
    }

    private RoundedPanel createCloseButton() {
        RoundedPanel btnClose = new RoundedPanel(10, Theme.CARD);
        btnClose.setPreferredSize(new Dimension(35, 35));
        btnClose.setLayout(new BorderLayout());
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel lblClose = new JLabel("X", SwingConstants.CENTER);
        lblClose.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblClose.setForeground(Theme.TEXT_SECONDARY);
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
        return btnClose;
    }

    private RoundedPanel createPrimaryButton(String text, Runnable action) {
        RoundedPanel btn = new RoundedPanel(12, Theme.BLUE_ACCENT);
        btn.setPreferredSize(new Dimension(180, 40));
        btn.setLayout(new BorderLayout());
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.add(lbl, BorderLayout.CENTER);

        btn.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                action.run();
            }

            public void mouseEntered(MouseEvent e) {
                btn.setBackground(Theme.BLUE_ACCENT.brighter());
                btn.repaint();
            }

            public void mouseExited(MouseEvent e) {
                btn.setBackground(Theme.BLUE_ACCENT);
                btn.repaint();
            }
        });
        return btn;
    }

    private RoundedPanel createGhostButton(String text, Runnable action) {
        RoundedPanel btn = new RoundedPanel(12, Theme.CARD);
        btn.setPreferredSize(new Dimension(90, 40));
        btn.setLayout(new BorderLayout());
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setForeground(Theme.TEXT_PRIMARY);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 13));
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
                label.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
                return label;
            }
        });

        cb.setUI(new BasicComboBoxUI() {
            @Override
            protected ComboPopup createPopup() {
                BasicComboPopup popup = new BasicComboPopup(comboBox) {
                    @Override
                    protected JScrollPane createScroller() {
                        JScrollPane scroller = new JScrollPane(list, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                        scroller.getVerticalScrollBar().setUI(new ModernScrollBarUI());
                        scroller.getVerticalScrollBar().setUnitIncrement(16);
                        scroller.setBorder(BorderFactory.createLineBorder(Theme.BORDER, 1));
                        return scroller;
                    }
                };
                popup.setBorder(BorderFactory.createEmptyBorder());
                return popup;
            }

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
                currentValuePane.paintComponent(g, c, comboBox, bounds.x, bounds.y, bounds.width, bounds.height, c instanceof JPanel);
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

    public boolean isSaved() {
        return isSaved;
    }

    public static class ComboItem {

        private final int key;
        private final String value;

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

    private static class DropdownDataDto {

        final Map<Integer, String> suppliers;
        final List<String> satuans;

        DropdownDataDto(Map<Integer, String> suppliers, List<String> satuans) {
            this.suppliers = suppliers;
            this.satuans = satuans;
        }
    }
}
