package App;

import javax.swing.SwingUtilities;
import views.LoginView;

public class TofuBaseApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginView().setVisible(true);
        });
    }
}
