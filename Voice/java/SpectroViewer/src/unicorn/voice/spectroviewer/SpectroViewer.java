package unicorn.voice.spectroviewer;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;

/**
 * @author uniCorn
 * @version 17.09.2008 16:31:23
 */
public class SpectroViewer extends JComponent {
    private SpectroController controller;

    public SpectroViewer(SpectroController controller) {
        this.controller = controller;
        this.controller.addRepaintListener(this);

        final Point pressed = new Point();
        this.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    SpectroController controller = SpectroViewer.this.controller;
                    Rectangle view = controller.getView();
                    controller.setMonitor(view.x + e.getX(), view.y + e.getY());
                } else if (e.getButton() == MouseEvent.BUTTON3) {
                    pressed.setLocation(e.getPoint());
                }
            }
        });

        this.addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                SpectroController controller = SpectroViewer.this.controller;
                Rectangle view = controller.getView();
                if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) > 0) {
                    controller.setMonitor(view.x + e.getX(), view.y + e.getY());
                } else if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) > 0) {
                    controller.setViewPoint(
                            view.x - e.getX() + pressed.x, view.y - e.getY() + pressed.y);
                    pressed.setLocation(e.getPoint());
                }
            }
        });
    }

    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        AffineTransform oldTransform = g2.getTransform();

        Rectangle view = controller.getView();
        g2.transform(new AffineTransform(0, 1, 1, 0, -view.x, -view.y));
        g2.drawImage(controller.getImage(), 0, 0, this);
        g2.setTransform(oldTransform);

        Point monitor = controller.getMonitor();
        g2.setColor(Color.RED);
        g2.drawLine(0, monitor.y - view.y, getWidth(), monitor.y - view.y);
        g2.drawLine(monitor.x - view.x, 0, monitor.x - view.x, getHeight());
    }

    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        controller.setViewSize(width, height);
    }
}
