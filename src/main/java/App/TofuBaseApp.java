package App;

import views.Dashboard;
import javax.swing.SwingUtilities;

public class TofuBaseApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Dashboard().setVisible(true);
        });
    }
}
