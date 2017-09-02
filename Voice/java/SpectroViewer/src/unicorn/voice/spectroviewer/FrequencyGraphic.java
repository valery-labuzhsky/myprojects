package unicorn.voice.spectroviewer;

import unicorn.voice.spectrogram.Spectrogram;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author uniCorn
 * @version 04.12.2008 21:20:08
 */
public class FrequencyGraphic extends JComponent {
    private SpectroController controller;

    public FrequencyGraphic(SpectroController controller) {
        this.controller = controller;
        this.controller.addRepaintListener(this);

        final Point pressed = new Point();
        this.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    SpectroController controller = FrequencyGraphic.this.controller;
                    Rectangle view = controller.getView();
                    controller.setMonitor(controller.getMonitor().x, view.y + e.getY());
                } else if (e.getButton() == MouseEvent.BUTTON3) {
                    pressed.setLocation(e.getPoint());
                }
            }
        });

        this.addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                SpectroController controller = FrequencyGraphic.this.controller;
                Rectangle view = controller.getView();
                if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) > 0) {
                    controller.setMonitor(controller.getMonitor().x, view.y + e.getY());
                } else if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) > 0) {
                    controller.setViewPoint(view.x, view.y - e.getY() + pressed.y);
                    pressed.setLocation(e.getPoint());
                }
            }
        });
    }

    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        int endFreq = getStartFreq() + getFreqCount();
        for (int freq = getStartFreq()+1; freq < endFreq; freq++) {
            g2.drawLine(getX(freq-1), getY(freq-1), getX(freq), getY(freq));
        }

        g2.setColor(Color.RED);
        int y = getY(controller.getMonitor().y);
        g2.drawLine(0, y, getSize().width, y);
    }

    private int getX(int freq) {
        Spectrogram spectro = controller.getSpectro();
        double width = getSize().getWidth();
        if (spectro == null) return (int) width;
        float level = spectro.getSpectro(getFrame(), freq);
        float max = spectro.getMaximum();
        return (int) (width - width*level/max);
    }

    private int getFrame() {
        return controller.getMonitor().x;
    }

    private int getY(int freq) {
        double height = getSize().getHeight();
        int freqs = getFreqCount();
        freq -= getStartFreq();
        return (int) (height*freq/freqs);
    }

    private int getStartFreq() {
        return controller.getView().y;
    }

    private int getFreqCount() {
        return controller.getView().height;
    }
}
