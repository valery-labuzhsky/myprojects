package unicorn.voice.spectroviewer;

import unicorn.voice.spectrogram.Spectrogram;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.*;

/**
 * @author uniCorn
 * @version 17.09.2008 16:22:05
 */
public class SpectroObserver extends JComponent {
    private SpectroController controller;

    public SpectroObserver(SpectroController controller) {
        this.controller = controller;
        this.controller.addRepaintListener(this);

        initComponents();
    }

    private void initComponents() {
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                moveViewArea(e.getPoint());
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                moveViewArea(e.getPoint());
            }
        });
    }

    private void moveViewArea(Point p) {
        Rectangle view = controller.getView();
        double k = ((double) getWidth())/controller.getImage().getHeight();
        view.x = (int)(p.getX()/k - view.width/2.0);
        view.y = (int)(p.getY()/k - view.height/2.0);
        controller.setViewPoint(view.x, view.y);
    }

    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        BufferedImage image = controller.getImage();
        AffineTransform oldTransform = g2.getTransform();

        double k = ((double) getWidth())/image.getHeight();
        g2.transform(new AffineTransform(0, k, k, 0, 0, 0));
        g2.drawImage(image, 0, 0, this);
        g2.setTransform(oldTransform);

        Rectangle view = controller.getView();
        g2.setColor(Color.RED);
        g2.setStroke(new BasicStroke(3));
        g2.drawRect((int)(k*view.x), (int)(k*view.y), (int)(k*view.width), (int)(k*view.height)-1);

        Point monitor = controller.getMonitor();
        g2.setStroke(new BasicStroke(5));
        g2.drawLine((int)(monitor.x*k), (int)(monitor.y*k), (int)(monitor.x*k), (int)(monitor.y*k));
    }

    public int getHeight(int width) {
        BufferedImage image = controller.getImage();
        return width*image.getWidth()/image.getHeight();
    }
}
