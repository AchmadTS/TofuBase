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

public class ModalEditBahan extends JDialog {

    private static final int MODAL_WIDTH = 580;
    private static final int MODAL_HEIGHT = 680;
    private static final String TITLE = "Edit Data Bahan Baku";
    private static final String SUBTITLE = "Perbarui histori data transaksi bahan baku";
    private static final String ALERT_SATUAN = "Jika satuan diubah, maka akan jadi data yang berbeda di Data Master (data akumulasi akan mencatat sebagai data yang berbeda).";
    private static final String ALERT_MIN_STOK = "MIN yang akan tampil di Data Master adalah MIN STOK yang terbaru.";
    private final BahanBakuDAO bahanDAO = new BahanBakuDAO();
    private final String targetIdBahan;
    private boolean isSaved = false;
    private boolean isInitializing = true;
    private boolean isSatuanBaruMode = false;
    private Point initialClick;
    private String originalSatuan = "";
    private String originalMinStok = "";
    private boolean alertSatuanShown = false;
    private boolean alertMinStokShown = false;
    private JTextField txtNama, txtSatuanBaru, txtQty, txtMinStok, txtHarga;
    private JComboBox<ComboItem> cbSupplier;
    private JComboBox<String> cbSatuan;
    private JPanel satuanInputWrapper;
    private JLabel lblToggleSatuan;

    public ModalEditBahan(Frame parent, String idBahan) {
        super(parent, TITLE, true);
        this.targetIdBahan = idBahan;
        setupDialogProperties(parent);
        setupGlobalUI();
        buildMainLayout();
        setupEscapeKey();
        loadDataForEdit();
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
        mainWrapper.setBorder(BorderFactory.createLineBorder(Theme.BORDER, 1));
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
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"),
                "closeModal");
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

        RoundedPanel iconPanel = new RoundedPanel(12, Theme.WARNING);
        iconPanel.setPreferredSize(new Dimension(40, 40));
        iconPanel.setLayout(new BorderLayout());
        JLabel lblIcon = new JLabel("✏", SwingConstants.CENTER);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        lblIcon.setForeground(Color.WHITE);
        iconPanel.add(lblIcon, BorderLayout.CENTER);

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

        JPanel leftHeader = new JPanel(new BorderLayout());
        leftHeader.setOpaque(false);
        leftHeader.add(iconPanel, BorderLayout.WEST);
        leftHeader.add(titleTextPanel, BorderLayout.CENTER);

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

        // NAMA BAHAN
        txtNama = createRawTextField("Memuat data...");
        setupNamaDebouncer();
        form.add(createFormGroup("NAMA BAHAN", wrapInput(txtNama), null));
        form.add(Box.createVerticalStrut(20));

        // SUPPLIER & SATUAN
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

        // STOK & HARGA
        JLabel lblSection = new JLabel("📋 DATA STOK & HARGA");
        lblSection.setForeground(Theme.TEXT_SECONDARY);
        lblSection.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblSection.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(lblSection);
        form.add(Box.createVerticalStrut(15));

        JPanel row3 = new JPanel(new GridLayout(1, 2, 20, 0));
        row3.setOpaque(false);
        row3.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtQty = createRawTextField("Memuat...");
        setupNumberFormatListener(txtQty, "");

        txtMinStok = createRawTextField("Memuat...");
        setupNumberFormatListener(txtMinStok, "");
        setupMinStokAlertListener();

        row3.add(createFormGroup("QTY (STOK DIEDIT)", wrapInput(txtQty), "Jumlah diedit ke stok saat ini"));
        row3.add(createFormGroup("MIN. STOK", wrapInput(txtMinStok), "Batas bawah pemicu peringatan"));

        form.add(row3);
        form.add(Box.createVerticalStrut(15));

        txtHarga = createRawTextField("Memuat...");
        setupNumberFormatListener(txtHarga, "Rp ");
        form.add(createFormGroup("HARGA BELI", wrapInput(txtHarga), "Harga per satuan dari supplier"));

        return form;
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
        cbSatuan.addActionListener(e -> triggerSatuanAlert());

        txtSatuanBaru = createRawTextField("Ketik satuan baru...");
        txtSatuanBaru.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                triggerSatuanAlert();
            }

            public void removeUpdate(DocumentEvent e) {
                triggerSatuanAlert();
            }

            public void changedUpdate(DocumentEvent e) {
                triggerSatuanAlert();
            }
        });

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
                triggerSatuanAlert();
            }
        });
        updateToggleSatuanLabel();

        toggleContainer.add(lblToggleSatuan);
        pnlSatuan.add(satuanInputWrapper);
        pnlSatuan.add(toggleContainer);
        return pnlSatuan;
    }

    private JPanel buildFooter() {
        JPanel footerWrapper = new JPanel(new BorderLayout());
        footerWrapper.setOpaque(false);
        footerWrapper.add(createThinLine(), BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(20, 30, 25, 30));

        buttonPanel.add(createGhostButton("Batal", this::dispose));
        buttonPanel.add(createPrimaryButton("💾 Simpan Perubahan", this::handleSimpan));

        footerWrapper.add(buttonPanel, BorderLayout.CENTER);
        return footerWrapper;
    }

    private void triggerSatuanAlert() {
        if (isInitializing || alertSatuanShown) {
            return;
        }

        String currentSatuan = isSatuanBaruMode ? txtSatuanBaru.getText().trim()
                : (cbSatuan.getSelectedItem() != null ? cbSatuan.getSelectedItem().toString() : "");
        if (!currentSatuan.equals(originalSatuan) && !currentSatuan.equals("Pilih satuan...")
                && !currentSatuan.equals("Ketik satuan baru...")) {
            int choice = JOptionPane.showConfirmDialog(this,
                    "Mengubah satuan akan membuat data baru di Data Master. Lanjutkan?", "Konfirmasi Perubahan Satuan",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
            if (choice == JOptionPane.OK_OPTION) {
                alertSatuanShown = true;
            } else {
                isInitializing = true;
                cbSatuan.setSelectedItem(originalSatuan);
                isInitializing = false;
            }
        }
    }

    private void setupMinStokAlertListener() {
        txtMinStok.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                checkMinStok();
            }

            public void removeUpdate(DocumentEvent e) {
                checkMinStok();
            }

            public void changedUpdate(DocumentEvent e) {
                checkMinStok();
            }

            private void checkMinStok() {
                if (isInitializing || alertMinStokShown) {
                    return;
                }

                SwingUtilities.invokeLater(() -> {
                    if (isInitializing || alertMinStokShown) {
                        return;
                    }

                    String current = txtMinStok.getText().trim();
                    if (!current.equals(originalMinStok) && !current.isEmpty() && !current.equals("Memuat...")) {
                        int choice = JOptionPane.showConfirmDialog(ModalEditBahan.this,
                                "MIN STOK yang disimpan akan menjadi batas baru di Data Master. Lanjutkan?",
                                "Konfirmasi Min Stok", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
                        if (choice == JOptionPane.OK_OPTION) {
                            alertMinStokShown = true;
                        } else {
                            isInitializing = true;
                            txtMinStok.setText(originalMinStok);
                            isInitializing = false;
                        }
                    }
                });
            }
        });
    }

    private void loadDataForEdit() {
        cbSupplier.removeAllItems();
        cbSupplier.addItem(new ComboItem(-1, "Memuat data..."));
        cbSupplier.setEnabled(false);

        cbSatuan.removeAllItems();
        cbSatuan.addItem("Memuat data...");
        cbSatuan.setEnabled(false);

        new SwingWorker<Void, Void>() {
            Map<Integer, String> suppliers;
            List<String> satuans;
            BahanBakuModel data;

            @Override
            protected Void doInBackground() {
                suppliers = bahanDAO.getSupplierList();
                satuans = bahanDAO.getSatuanList();
                data = bahanDAO.getTransaksiById(targetIdBahan);
                return null;
            }

            @Override
            protected void done() {
                isInitializing = true;

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

                if (data != null) {
                    txtNama.setText(data.getNama());
                    txtNama.setForeground(Theme.TEXT_PRIMARY);

                    int idSup = data.getIdSupplier();
                    for (int i = 0; i < cbSupplier.getItemCount(); i++) {
                        if (cbSupplier.getItemAt(i).getKey() == idSup) {
                            cbSupplier.setSelectedIndex(i);
                            break;
                        }
                    }

                    originalSatuan = data.getSatuan();
                    cbSatuan.setSelectedItem(originalSatuan);

                    java.text.NumberFormat nf = java.text.NumberFormat.getInstance(java.util.Locale.of("id", "ID"));

                    txtQty.setText(nf.format(data.getStok()));
                    txtQty.setForeground(Theme.TEXT_PRIMARY);

                    originalMinStok = nf.format(data.getMinStok());
                    txtMinStok.setText(originalMinStok);
                    txtMinStok.setForeground(Theme.TEXT_PRIMARY);

                    txtHarga.setText("Rp " + nf.format(data.getHargaBeli()));
                    txtHarga.setForeground(Theme.TEXT_PRIMARY);
                }

                isInitializing = false;
            }
        }.execute();
    }

    private void checkBahanExist(String nama) {
        if (nama.isEmpty() || nama.startsWith("cth.")) {
            resetAutofill();
            return;
        }

        new SwingWorker<BahanBakuModel, Void>() {
            @Override
            protected BahanBakuModel doInBackground() {
                return bahanDAO.cekDetailBahan(nama);
            }

            @Override
            protected void done() {
                try {
                    BahanBakuModel detail = get();
                    if (detail != null) {
                        isInitializing = true;
                        String satuan = detail.getSatuan();
                        double minStok = detail.getMinStok();
                        if (isSatuanBaruMode) {
                            toggleSatuanMode();
                        }

                        cbSatuan.setSelectedItem(satuan);
                        cbSatuan.setEnabled(false);

                        java.text.NumberFormat formatter = java.text.NumberFormat
                                .getInstance(java.util.Locale.of("id", "ID"));
                        txtMinStok.setText(formatter.format(minStok));
                        txtMinStok.setEnabled(false);

                        if (lblToggleSatuan != null) {
                            lblToggleSatuan.setVisible(false);
                        }
                        isInitializing = false;
                    } else {
                        resetAutofill();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    private void handleSimpan() {
        try {
            validateInput();
            saveToDatabase();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Peringatan Validasi", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan sistem: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void validateInput() {
        String nama = txtNama.getText().trim();
        if (nama.isEmpty() || nama.startsWith("cth.") || nama.equals("Memuat data...")) {
            throw new IllegalArgumentException("Nama bahan tidak boleh kosong!");
        }

        ComboItem sup = (ComboItem) cbSupplier.getSelectedItem();
        if (sup == null || sup.getKey() == -1) {
            throw new IllegalArgumentException("Silakan pilih Supplier yang valid!");
        }

        if (isSatuanBaruMode) {
            String satuan = txtSatuanBaru.getText().trim();
            if (satuan.isEmpty() || satuan.equalsIgnoreCase("Ketik satuan baru...")) {
                throw new IllegalArgumentException("Kolom satuan baru tidak boleh kosong!");
            }
        } else {
            String satuan = cbSatuan.getSelectedItem() != null ? cbSatuan.getSelectedItem().toString() : "";
            if (satuan.isEmpty() || satuan.contains("Pilih") || satuan.contains("Memuat")) {
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

        BahanBakuModel bahanEdit = new BahanBakuModel();
        bahanEdit.setIdBahan(Integer.parseInt(targetIdBahan));
        bahanEdit.setNama(nama);
        bahanEdit.setIdSupplier(sup.getKey());
        bahanEdit.setSatuan(satuan);
        bahanEdit.setStok(qty);
        bahanEdit.setMinStok(min);
        bahanEdit.setHargaBeli(harga);

        if (bahanDAO.updateTransaksiBahan(bahanEdit)) {
            isSaved = true;
            dispose();
        } else {
            throw new RuntimeException("Gagal menyimpan perubahan ke database!");
        }
    }

    private void setupNamaDebouncer() {
        Timer checkTimer = new Timer(500, e -> {
            if (!isInitializing) {
                checkBahanExist(txtNama.getText().trim());
            }
        });
        checkTimer.setRepeats(false);
        txtNama.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                if (!isInitializing) {
                    checkTimer.restart();
                }
            }

            public void removeUpdate(DocumentEvent e) {
                if (!isInitializing) {
                    checkTimer.restart();
                }
            }

            public void changedUpdate(DocumentEvent e) {
                if (!isInitializing) {
                    checkTimer.restart();
                }
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
        String hexPrimary = String.format("#%06x", (Theme.WARNING.getRGB() & 0xFFFFFF));
        String hexDanger = String.format("#%06x", (Theme.RED.getRGB() & 0xFFFFFF));
        if (isSatuanBaruMode) {
            lblToggleSatuan.setText("<html><u style='color:" + hexDanger + ";'>batal, pilih dari daftar</u></html>");
        } else {
            lblToggleSatuan.setText("<html><u style='color:" + hexPrimary + ";'>tambahkan satuan baru</u></html>");
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
                        java.text.NumberFormat formatter = java.text.NumberFormat
                                .getInstance(java.util.Locale.of("id", "ID"));
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
        String titleHtml = "<html><span style='color:" + hexColor
                + "; font-family:SansSerif; font-size:10px; font-weight:bold;'>" + title
                + "</span> <span style='color:#FF4747;'>*</span></html>";
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
                if (txt.getText().equals(placeholder) || txt.getText().contains("Memuat")) {
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
        RoundedPanel btn = new RoundedPanel(12, Theme.WARNING);
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
                btn.setBackground(Theme.WARNING.brighter());
                btn.repaint();
            }

            public void mouseExited(MouseEvent e) {
                btn.setBackground(Theme.WARNING);
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
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected,
                        cellHasFocus);
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
                        JScrollPane scroller = new JScrollPane(list, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
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
                @SuppressWarnings("unchecked")
                ListCellRenderer<? super Object> renderer = (ListCellRenderer<? super Object>) comboBox.getRenderer();
                Component c = renderer.getListCellRendererComponent(listBox, comboBox.getSelectedItem(), -1, false,
                        false);
                c.setFont(comboBox.getFont());
                c.setForeground(comboBox.isEnabled() ? Theme.TEXT_PRIMARY : Theme.TEXT_SECONDARY);
                c.setBackground(Theme.CARD);
                currentValuePane.paintComponent(g, c, comboBox, bounds.x, bounds.y, bounds.width, bounds.height, false);
            }

            @Override
            protected JButton createArrowButton() {
                JButton btn = new JButton() {
                    public void paint(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(comboBox.isEnabled() ? Theme.TEXT_SECONDARY : new Color(138, 146, 166, 100));
                        g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                        int w = getWidth(), h = getHeight();
                        int[] x = { w / 2 - 4, w / 2, w / 2 + 4 }, y = { h / 2 - 2, h / 2 + 3, h / 2 - 2 };
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
