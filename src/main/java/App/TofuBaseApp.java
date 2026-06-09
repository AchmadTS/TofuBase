package App;

import views.Login;
import javax.swing.SwingUtilities;

public class TofuBaseApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Login().setVisible(true);
        });
    }
}
