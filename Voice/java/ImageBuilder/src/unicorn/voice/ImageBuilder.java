package unicorn.voice;

import javax.imageio.ImageIO;
import java.io.*;
import java.awt.image.BufferedImage;

/**
 * @author uniCorn
 * @version 22.01.2008 19:27:59
 */
public class ImageBuilder {
    public static class Header {
        private int frames;
        private int frequencies;
        private float min_freq;
        private float max_freq;
        private float rate;
    }

    private static int getPixel(int r, int g, int b) {
      return r << 16 | g << 8 | b;
    }

    public static int readInt(InputStream in) throws IOException {
        int ch1 = in.read();
        int ch2 = in.read();
        int ch3 = in.read();
        int ch4 = in.read();
        if ((ch1 | ch2 | ch3 | ch4) < 0)
            throw new EOFException();
        return ((ch4 << 24) + (ch3 << 16) + (ch2 << 8) + (ch1 << 0));
    }

    public static float readFloat(InputStream in) throws IOException {
        return Float.intBitsToFloat(readInt(in));
    }

    public static void main(String[] args) throws IOException {
        String fileName = args[0];
        DataInputStream input = new DataInputStream(new FileInputStream(new File(fileName)));
        Header header = new Header();
        header.frames = readInt(input);
        header.frequencies = readInt(input);
        header.min_freq = readFloat(input);
        header.max_freq = readFloat(input);
        header.rate = readFloat(input);

        float max = readFloat(input);

        BufferedImage bi = new BufferedImage(header.frames, header.frequencies, BufferedImage.TYPE_INT_RGB);

        for (long i = 0; i < header.frames; i++) {
          for (int y = 0; y < header.frequencies; y++) {

            float r = readFloat(input)/max;
            int cr = (int) (r * 255);

            int pixel = getPixel(cr, cr, cr);
            bi.setRGB((int) i, y, pixel);
          }
        }

        ImageIO.write(bi, "jpg", new File(fileName+".jpg"));

        input.close();
    }
}
