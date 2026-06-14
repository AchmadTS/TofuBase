package components;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import utils.Theme;

public class ModernScrollBarUI extends BasicScrollBarUI {

    private final int THICKNESS = 14;

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        g.setColor(Theme.BG);
        g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(100, 100, 100));

        if (scrollbar.getOrientation() == JScrollBar.VERTICAL) {
            g2.fillRoundRect(thumbBounds.x + 3, thumbBounds.y + 2, thumbBounds.width - 6, thumbBounds.height - 4, 8, 8);
        } else {
            g2.fillRoundRect(thumbBounds.x + 2, thumbBounds.y + 3, thumbBounds.width - 4, thumbBounds.height - 6, 8, 8);
        }
        g2.dispose();
    }

    @Override
    public Dimension getPreferredSize(JComponent c) {
        if (scrollbar.getOrientation() == JScrollBar.VERTICAL) {
            return new Dimension(THICKNESS, 0);
        } else {
            return new Dimension(0, THICKNESS);
        }
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return createZeroButton();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return createZeroButton();
    }

    private JButton createZeroButton() {
        JButton jbutton = new JButton();
        jbutton.setPreferredSize(new Dimension(0, 0));
        return jbutton;
    }
}
