package views;

import components.Sidebar;
import utils.Theme;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel contentPanel; // Panel penampung kartu-kartu halaman
    private Sidebar sidebar;

    public MainFrame(String userName, String userRole) {
        setTitle("TofuBase - Pabrik Tahu");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Theme.BG);

        // 1. Inisialisasi CardLayout dan Panel Penampungnya
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        // 2. Masukkan halaman-halaman Anda ke dalam contentPanel sebagai "Kartu"
        // Halaman ini HANYA DI-LOAD SATU KALI saat aplikasi dibuka
        contentPanel.add(new Dashboard(userName, userRole), "Dashboard");
        contentPanel.add(new BahanBaku(userName, userRole), "Bahan Baku");
        // Tambahkan halaman lain di sini nanti...

        // 3. Setup Sidebar dengan callback navigasi
        // Saat menu diklik, Sidebar akan mengirim nama menu ke fungsi switchPage()
        sidebar = new Sidebar(userName, userRole, "Dashboard", this::switchPage);

        // 4. Masukkan ke Frame Utama
        add(sidebar, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        // Tampilkan halaman default pertama kali
        switchPage("Dashboard");
    }

    // Fungsi untuk menukar kartu tanpa memuat ulang database
    private void switchPage(String pageName) {
        cardLayout.show(contentPanel, pageName);
        sidebar.setActiveMenu(pageName); // Update warna menu di sidebar
    }
}
