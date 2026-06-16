package views;

import components.RoundedPanel;
import dao.SupplierDAO;
import models.Supplier;
import utils.Theme;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ModalEditSupplier extends JDialog {

    private static final int MODAL_WIDTH = 580;
    private static final int MODAL_HEIGHT = 650;
    private final SupplierDAO supplierDAO = new SupplierDAO();
    private boolean isSaved = false;
    private JTextField txtNama, txtTelp, txtEmail;
    private JTextArea txtAlamat;
    private Point initialClick;
    private final String targetIdSupplier;

    public ModalEditSupplier(Frame parent, String idSupplier) {
        super(parent, "Edit Supplier", true);
        this.targetIdSupplier = idSupplier;
        setupDialogProperties(parent);
        buildMainLayout();
        setupEscapeKey();
        loadData();
    }

    private void setupDialogProperties(Frame parent) {
        setSize(MODAL_WIDTH, MODAL_HEIGHT);
        setLocationRelativeTo(parent);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));
    }

    private void loadData() {
        // Ambil data dari DAO
        Supplier s = supplierDAO.getSupplierById(Integer.parseInt(targetIdSupplier));
        if (s != null) {
            txtNama.setText(s.getNama());
            txtNama.setForeground(Theme.TEXT_PRIMARY);
            txtTelp.setText(s.getNoTelp());
            txtTelp.setForeground(Theme.TEXT_PRIMARY);
            txtEmail.setText(s.getEmail());
            txtEmail.setForeground(Theme.TEXT_PRIMARY);
            txtAlamat.setText(s.getAlamat());
            txtAlamat.setForeground(Theme.TEXT_PRIMARY);
        }
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

    private JPanel buildForm() {
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setOpaque(false);
        form.setBorder(new EmptyBorder(25, 35, 10, 35));
        form.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtNama = createRawTextField("Contoh: PT. Sumber Tahu Jaya");
        form.add(createFormGroup("NAMA SUPPLIER", wrapInput(txtNama, 42), null));
        form.add(Box.createVerticalStrut(20));

        JPanel rowContact = new JPanel(new GridLayout(1, 2, 20, 0));
        rowContact.setOpaque(false);
        rowContact.setAlignmentX(Component.LEFT_ALIGNMENT);
        rowContact.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        txtTelp = createRawTextField("0812xxxxxxxx");
        txtEmail = createRawTextField("email@perusahaan.com");

        rowContact.add(createFormGroup("NO. TELEPON", wrapInput(txtTelp, 42), null));
        rowContact.add(createFormGroup("EMAIL", wrapInput(txtEmail, 42), null));
        form.add(rowContact);
        form.add(Box.createVerticalStrut(20));

        txtAlamat = createRawTextArea("Masukkan alamat lengkap...");
        form.add(createFormGroup("ALAMAT", wrapInputTextArea(txtAlamat), null));

        return form;
    }

    private void handleSimpan() {
        try {
            if (txtNama.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nama supplier harus diisi!");
                return;
            }

            validateInputs();
            Supplier s = new Supplier(Integer.parseInt(targetIdSupplier), txtNama.getText(), txtAlamat.getText(), txtTelp.getText(), txtEmail.getText());

            if (supplierDAO.updateSupplier(s)) {
                isSaved = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal memperbarui database.");
            }
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Kesalahan Input", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan: " + e.getMessage());
        }
    }

    private void validateInputs() throws IllegalArgumentException {
        String telp = txtTelp.getText().trim();
        String email = txtEmail.getText().trim();

        if (telp.isEmpty() || !telp.matches("\\d+")) {
            throw new IllegalArgumentException("No. Telepon harus diisi dengan angka!");
        }
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        if (email.isEmpty() || !email.matches(emailRegex)) {
            throw new IllegalArgumentException("Format email tidak valid!");
        }
    }

    // --- UI HELPER METHODS ---
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(20, 30, 15, 30));
        enableWindowDrag(header);

        JPanel leftHeader = new JPanel(new BorderLayout());
        leftHeader.setOpaque(false);
        leftHeader.add(createHeaderIcon(), BorderLayout.WEST);

        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setOpaque(false);
        titlePanel.setBorder(new EmptyBorder(0, 15, 0, 0));
        titlePanel.add(new JLabel("Edit Supplier") {
            {
                setFont(new Font("SansSerif", Font.BOLD, 17));
                setForeground(Theme.TEXT_PRIMARY);
            }
        });
        titlePanel.add(new JLabel("Perbarui data pemasok") {
            {
                setFont(new Font("SansSerif", Font.PLAIN, 11));
                setForeground(Theme.TEXT_SECONDARY);
            }
        });

        leftHeader.add(titlePanel, BorderLayout.CENTER);
        header.add(leftHeader, BorderLayout.WEST);
        header.add(createCloseButton(), BorderLayout.EAST);
        return header;
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

    // Gunakan Theme.WARNING sesuai permintaan
    private RoundedPanel createPrimaryButton(String text, Runnable action) {
        RoundedPanel btn = new RoundedPanel(12, Theme.WARNING);
        btn.setPreferredSize(new Dimension(180, 40));
        btn.setLayout(new BorderLayout());
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        JLabel lbl = new JLabel(text, SwingConstants.CENTER) {
            {
                setForeground(Color.WHITE);
                setFont(new Font("SansSerif", Font.BOLD, 13));
            }
        };
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

    private RoundedPanel createHeaderIcon() {
        RoundedPanel icon = new RoundedPanel(12, Theme.WARNING);
        icon.setPreferredSize(new Dimension(40, 40));
        icon.setLayout(new BorderLayout());
        icon.add(new JLabel("✏", SwingConstants.CENTER) {
            {
                setForeground(Color.WHITE);
                setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
            }
        }, BorderLayout.CENTER);
        return icon;
    }

    private JPanel createFormGroup(String title, JComponent input, String desc) {
        JPanel pnl = new JPanel();
        pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
        pnl.setOpaque(false);
        pnl.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lblTitle = new JLabel("<html><span style='color:#8A92A6; font-family:SansSerif; font-size:10px; font-weight:bold;'>" + title + "</span></html>");
        lblTitle.setBorder(new EmptyBorder(0, 0, 5, 0));
        pnl.add(lblTitle);
        input.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnl.add(input);
        return pnl;
    }

    private RoundedPanel wrapInput(JComponent inputComp, int height) {
        RoundedPanel wrapper = new RoundedPanel(12, Theme.CARD);
        wrapper.setLayout(new BorderLayout());
        wrapper.setBorder(BorderFactory.createLineBorder(Theme.BORDER, 1));
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
        wrapper.setPreferredSize(new Dimension(Integer.MAX_VALUE, height));
        wrapper.add(inputComp, BorderLayout.CENTER);
        return wrapper;
    }

    private RoundedPanel wrapInputTextArea(JTextArea textArea) {
        RoundedPanel wrapper = new RoundedPanel(12, Theme.CARD);
        wrapper.setLayout(new BorderLayout());
        wrapper.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER, 1),
                new EmptyBorder(10, 10, 10, 10)
        ));
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        wrapper.add(new JScrollPane(textArea) {
            {
                setOpaque(false);
                getViewport().setOpaque(false);
                setBorder(null);
            }
        }, BorderLayout.CENTER);
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

    private JTextArea createRawTextArea(String placeholder) {
        JTextArea txt = new JTextArea(placeholder);
        txt.setOpaque(false);
        txt.setForeground(Theme.TEXT_SECONDARY);
        txt.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txt.setLineWrap(true);
        txt.setWrapStyleWord(true);
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
        line.setPreferredSize(new Dimension(Integer.MAX_VALUE, 1));
        line.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return line;
    }

    private RoundedPanel createGhostButton(String text, Runnable action) {
        RoundedPanel btn = new RoundedPanel(12, Theme.CARD);
        btn.setPreferredSize(new Dimension(80, 40));
        btn.setLayout(new BorderLayout());
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel lbl = new JLabel(text, SwingConstants.CENTER) {
            {
                setForeground(Theme.TEXT_PRIMARY);
                setFont(new Font("SansSerif", Font.BOLD, 13));
            }
        };
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

    private RoundedPanel createCloseButton() {
        RoundedPanel btnClose = new RoundedPanel(10, Theme.CARD);
        btnClose.setPreferredSize(new Dimension(35, 35));
        btnClose.setLayout(new BorderLayout());
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.add(new JLabel("X", SwingConstants.CENTER) {
            {
                setForeground(Theme.TEXT_SECONDARY);
            }
        }, BorderLayout.CENTER);
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

    private void setupEscapeKey() {
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "close");
        getRootPane().getActionMap().put("close", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
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

    public boolean isSaved() {
        return isSaved;
    }
}
