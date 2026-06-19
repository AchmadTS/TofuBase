package views;

import components.Sidebar;
import utils.Theme;
import javax.swing.*;
import java.awt.*;
import models.User;
import models.Admin;
import models.Owner;
import views.PenjualanPanel;
import views.ProdukPanel;
import views.PelangganPanel;
import views.PemasukanPanel;
import views.PengeluaranPanel;
import views.InventarisPanel;
import views.ProduksiPanel;
import views.LaporanKeuanganPanel;
import views.KelolaUserPanel;

public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel contentPanel;
    private Sidebar sidebar;
    private User currentUser;

    public MainFrame(User user) {
        this.currentUser = user;
        setTitle("TofuBase - Pabrik Tahu");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Theme.BG);
        String userName = currentUser.getNama();
        final String userRole;

        if (currentUser instanceof Owner) {
            userRole = "Owner";
        } else if (currentUser instanceof Admin) {
            userRole = "Admin";
        } else {
            userRole = "Staff";
        }

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.add(new Dashboard(userName, userRole), "Dashboard");
        contentPanel.add(new BahanBaku(userName, userRole), "Bahan Baku");
        contentPanel.add(new ProdukPanel(userName, userRole), "Produk");
        contentPanel.add(new PelangganPanel(userName, userRole), "Pelanggan");
        contentPanel.add(new PenjualanPanel(userName, userRole), "Penjualan");
        contentPanel.add(new PemasukanPanel(userName, userRole), "Pemasukan");
        contentPanel.add(new PengeluaranPanel(userName, userRole), "Pengeluaran");
        // contentPanel.add(new StokDistribusiPanel(userName, userRole), "Stok & Distribusi");
        contentPanel.add(new InventarisPanel(userName, userRole), "Inventaris");
        contentPanel.add(new ProduksiPanel(userName, userRole), "Produksi");
        contentPanel.add(new LaporanKeuanganPanel(userName, userRole), "Laporan Keuangan");
        contentPanel.add(new KelolaUserPanel(userName, userRole), "Kelola User");
        contentPanel.add(new SupplierPanel(userName, userRole), "Supplier");
        sidebar = new Sidebar(userName, userRole, "Dashboard", menuName -> {
            if (currentUser.verifikasiAksesMenu(menuName)) {
                switchPage(menuName);
            } else {
                JOptionPane.showMessageDialog(this, "Akses Ditolak! Anda (" + userRole + ") tidak memiliki izin untuk fitur " + menuName + ".", "Peringatan Keamanan", JOptionPane.ERROR_MESSAGE);
            }

        });

        add(sidebar, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
        switchPage("Dashboard");
    }

    private void switchPage(String pageName) {
        cardLayout.show(contentPanel, pageName);
        sidebar.setActiveMenu(pageName);
    }
}
