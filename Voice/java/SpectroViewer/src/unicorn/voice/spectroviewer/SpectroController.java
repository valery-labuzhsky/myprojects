package unicorn.voice.spectroviewer;

import unicorn.voice.spectrogram.Spectrogram;

import javax.swing.*;
import java.awt.image.*;
import java.awt.*;
import java.util.LinkedList;

/**
 * @author uniCorn
 * @version 25.11.2008 22:35:11
 */
public class SpectroController {
    private BufferedImage image;

    private LinkedList<JComponent> components = new LinkedList<JComponent>();

    private Spectrogram spectro;

    public SpectroController(Spectrogram spectro) {
        setSpectro(spectro);
    }

    public SpectroController() {
        this(null);
    }

    public Spectrogram getSpectro() {
        return spectro;
    }

    public void setSpectro(Spectrogram spectro) {
        this.spectro = spectro;
        if (spectro!=null) {
            this.image = prepareImage(spectro);
        } else {
            this.image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        }
    }

    public BufferedImage getImage() {
        return image;
    }

    public void addRepaintListener(JComponent component) {
        components.add(component);
    }

    private Rectangle view = new Rectangle();

    public Rectangle getView() {
        return view;
    }

    public void setViewPoint(int x, int y) {
        int viewWidth = view.width;
        int viewHeight = view.height;

        x = x<0?0:x;
        x = x+viewWidth>image.getHeight()?image.getHeight()-viewWidth:x;

        y = y<0?0:y;
        y = y+viewHeight>image.getWidth()?image.getWidth()-viewHeight:y;

        view.x = x;
        view.y = y;

        repaint();
    }

    private void repaint() {
        for (JComponent component : components) {
            component.repaint();
        }
    }

    public void setViewSize(int width, int height) {
        view.width = width;
        view.height = height;
        if (image.getWidth() < view.y + height) {
            view.y = image.getWidth() - height;
            if (view.y<0) {
                view.y = 0;
                view.height = image.getWidth();
            }
        }
        if (image.getHeight() < view.y + width) {
            view.x = image.getHeight() - width;
            if (view.x<0) {
                view.x = 0;
                view.width = image.getHeight(); 
            }
        }
        repaint();
    }

    private Point monitor = new Point();

    public Point getMonitor() {
        return monitor;
    }

    public void setMonitor(int x, int y) {
        monitor.setLocation(x, y);
        repaint();
    }

    private BufferedImage prepareImage(Spectrogram spectro) {
        final int[] db = spectro.getImage();
        DirectColorModel cm = (DirectColorModel) ColorModel.getRGBdefault();
        DataBuffer buf = new DataBufferInt(db, db.length);

        WritableRaster r = Raster.createPackedRaster(buf,
                spectro.getFrequencies(), spectro.getFrames(),
                spectro.getFrequencies(),
                cm.getMasks(),
                null);
        return new BufferedImage(cm, r, false, null);
    }

}
