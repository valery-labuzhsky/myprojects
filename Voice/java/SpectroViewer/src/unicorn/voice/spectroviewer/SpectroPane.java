package unicorn.voice.spectroviewer;

import unicorn.voice.spectrogram.Spectrogram;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author uniCorn
 * @version 03.08.2008 16:18:05
 */
public class SpectroPane extends JPanel {
    private SpectroController controller;
    private SpectroObserver observer;

    public SpectroPane() {
        super(new BorderLayout());
        controller = new SpectroController();
        observer = new SpectroObserver(controller);
        final SpectroViewer viewer = new SpectroViewer(controller);

        final JSplitPane verticalLeftSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true,
                new JPanel(), new FrequencyGraphic(controller));
        final JSplitPane verticalRightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true,
                new TimeGraphic(controller), viewer);
        verticalLeftSplitPane.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY,
                new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent evt) {
                        if (evt.getNewValue() != evt.getOldValue()) {
                            verticalRightSplitPane.setDividerLocation(verticalLeftSplitPane.getDividerLocation());
                        }
                    }
                });
        verticalRightSplitPane.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY,
                new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent evt) {
                        if (evt.getNewValue() != evt.getOldValue()) {
                            verticalLeftSplitPane.setDividerLocation(verticalRightSplitPane.getDividerLocation());
                        }
                    }
                });
        JSplitPane horizontalSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true,
                verticalLeftSplitPane, verticalRightSplitPane);

        horizontalSplitPane.setResizeWeight(0.2);
        horizontalSplitPane.setDividerLocation(0.2);

        verticalRightSplitPane.setResizeWeight(0.2);
        verticalRightSplitPane.setDividerLocation(0.2);

        this.add(observer, BorderLayout.NORTH);
        this.add(horizontalSplitPane);

        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                Point monitor = controller.getMonitor();
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    controller.setMonitor(monitor.x, monitor.y + 1);
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    controller.setMonitor(monitor.x, monitor.y - 1);
                } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    controller.setMonitor(monitor.x - 1, monitor.y);
                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    controller.setMonitor(monitor.x + 1, monitor.y);
                }
            }
        });
    }

    public void setBounds(int x, int y, int width, int height) {
        int h = observer.getHeight(width);
        observer.setPreferredSize(new Dimension(width, h));
        super.setBounds(x, y, width, height);
    }

    public void setSpectro(Spectrogram spectro) {
        controller.setSpectro(spectro);

        setBounds(getBounds());
        this.updateUI();
    }
}
