package unicorn.voice.spectroviewer;

import unicorn.voice.spectrogram.Spectrogram;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author uniCorn
 * @version Dec 13, 2008 1:10:30 PM
 */
public class TimeGraphic extends JComponent{
    private SpectroController controller;

    public TimeGraphic(SpectroController controller) {
        this.controller = controller;
        this.controller.addRepaintListener(this);

        final Point pressed = new Point();
        this.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    SpectroController controller = TimeGraphic.this.controller;
                    Rectangle view = controller.getView();
                    controller.setMonitor(view.x + e.getX(), controller.getMonitor().y);
                } else if (e.getButton() == MouseEvent.BUTTON3) {
                    pressed.setLocation(e.getPoint());
                }
            }
        });

        this.addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                SpectroController controller = TimeGraphic.this.controller;
                Rectangle view = controller.getView();
                if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) > 0) {
                    controller.setMonitor(view.x + e.getX(), controller.getMonitor().y);
                } else if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) > 0) {
                    controller.setViewPoint(view.x - e.getX() + pressed.x, view.y);
                    pressed.setLocation(e.getPoint());
                }
            }
        });
    }

    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        int endFrame = getStartFrame() + getFrameCount();
        for (int frame = getStartFrame()+1; frame < endFrame; frame++) {
            g2.drawLine(getX(frame-1), getY(frame-1), getX(frame), getY(frame));
        }

        g2.setColor(Color.RED);
        int x = getX(controller.getMonitor().x);
        g2.drawLine(x, 0, x, getSize().height);
    }

    private int getY(int frame) {
        Spectrogram spectro = controller.getSpectro();
        double height = getSize().getHeight();
        if (spectro==null) return (int) height;
        float level = spectro.getSpectro(frame, getFreq());
        float max = spectro.getMaximum();
        return (int) (height - height*level/max);
    }

    private int getFreq() {
        return controller.getMonitor().y;
    }

    private int getX(int frame) {
        double height = getSize().getWidth();
        int frames = getFrameCount();
        frame -= getStartFrame();
        return (int) (height*frame/frames);
    }

    private int getStartFrame() {
        return controller.getView().x;
    }

    private int getFrameCount() {
        return controller.getView().width;
    }
}
