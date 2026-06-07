package com.tubes.tofubase.App;

import com.tubes.tofubase.Dashboard.Dashboard;
import javax.swing.SwingUtilities;

public class TofuBaseApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Dashboard().setVisible(true);
        });
    }
}
