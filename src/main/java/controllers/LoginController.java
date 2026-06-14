package controllers;

import dao.UserDAO;
import views.LoginView;
import views.MainFrame;
import javax.swing.*;
import java.sql.SQLException;
import java.util.prefs.Preferences;

public class LoginController {

    private final LoginView view;
    private final UserDAO userDAO;
    private final Preferences prefs;
    private final String EMAIL_PLACEHOLDER = "email@pabrik.com";
    private final String PASS_PLACEHOLDER = "Masukkan kata sandi";

    public LoginController(LoginView view) {
        this.view = view;
        this.userDAO = new UserDAO();
        this.prefs = Preferences.userNodeForPackage(LoginView.class);
        initController();
    }

    private void initController() {
        loadPreferences();
        view.getBtnLogin().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                handleLogin();
            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                view.getBtnLogin().setBackground(utils.Theme.BLUE_ACCENT.darker());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                view.getBtnLogin().setBackground(utils.Theme.BLUE_ACCENT);
            }
        });
    }

    private void loadPreferences() {
        boolean rememberMe = prefs.getBoolean("rememberMe", false);
        String savedEmail = prefs.get("email", "");
        String savedPassword = prefs.get("password", "");
        view.getChkRemember().setSelected(rememberMe);

        if (rememberMe && !savedEmail.isEmpty()) {
            view.getTxtEmail().setText(savedEmail);
            view.getTxtEmail().setForeground(utils.Theme.TEXT_PRIMARY);
            view.getLblIconMail().setForeground(utils.Theme.BLUE_ACCENT);
        }

        if (rememberMe && !savedPassword.isEmpty()) {
            view.getTxtPass().setText(savedPassword);
            view.getTxtPass().setForeground(utils.Theme.TEXT_PRIMARY);
            view.getTxtPass().setEchoChar('•');
            view.getLblIconLock().setForeground(utils.Theme.BLUE_ACCENT);
        }
    }

    private void savePreferences(String email, String password) {
        if (view.getChkRemember().isSelected()) {
            prefs.putBoolean("rememberMe", true);
            prefs.put("email", email);
            prefs.put("password", password);
        } else {
            prefs.putBoolean("rememberMe", false);
            prefs.remove("email");
            prefs.remove("password");
        }
    }

    private void handleLogin() {
        String email = view.getTxtEmail().getText().trim();
        String password = String.valueOf(view.getTxtPass().getPassword());

        if (email.isEmpty() || email.equals(EMAIL_PLACEHOLDER) || password.isEmpty() || password.equals(PASS_PLACEHOLDER)) {
            JOptionPane.showMessageDialog(view, "Email dan Kata Sandi tidak boleh kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            models.User loggedInUser = userDAO.authenticateUser(email, password);

            if (loggedInUser != null) {
                String namaUser = loggedInUser.getNama();
                String roleUser = "";
                if (loggedInUser instanceof models.Owner) {
                    roleUser = "Owner";
                } else if (loggedInUser instanceof models.Admin) {
                    roleUser = "Admin";
                } else if (loggedInUser instanceof models.Staff) {
                    roleUser = "Staff";
                }

                savePreferences(email, password);
                view.dispose();
                new MainFrame(namaUser, roleUser).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(view, "Email atau Kata Sandi salah!", "Login Gagal", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(view, "Koneksi Database Gagal: \n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.err.println("Gagal Login: " + ex.getMessage());
        }
    }
}
