package unicorn.voice.spectrogram;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author uniCorn
 * @version 01.02.2008 19:17:02
 */
public class Spectrogram {
    static {
        System.loadLibrary("unicorn_voice_spectrogram_Spectrogram");
    }

    private static final int FREQUENCIES = 1024;
    private static final int MIN_FREQ = 300;
    private static final int MAX_FREQ = 3400;

    private Header header;
    private float maximum;
    private float[] data;
    private int[] image;

    public Spectrogram(String fileName) throws IOException {
        this(new File(fileName));
    }

    public Spectrogram(File sound) throws IOException {
        this(sound, FREQUENCIES, MIN_FREQ, MAX_FREQ);
    }

    public Spectrogram(File sound, int frequencies, float minFreq, float maxFreq) throws IOException {
        String filename = sound.getCanonicalPath();
        byte[] buffer = filename.getBytes("cp1251");
        byte[] accents = new byte[buffer.length+1];
        System.arraycopy(buffer, 0, accents, 0, buffer.length);
        accents[accents.length-1] = 0;
        read(accents, frequencies, minFreq, maxFreq);
    }

    native private void read(byte[] accentsSoundFilename, int frequencies, float minFreq, float maxFreq);

    /*    public Spectrogram(InputStream spectroStream) throws IOException {
    DataInput input = new EndiannessDataInputStream(spectroStream, Endiannes.LITTLE_ENDIAN);
    header = new Header(input);
    maximum = input.readFloat();
    int maxpow = (int)(Math.log(header.getFrequencies()) / Math.log(2))+1;
    data = new float[maxpow][][];
    data[0] = new float[header.getFrames()][];
    for (int frame = 0; frame < header.getFrames(); frame++) {
    data[0][frame] = new float[header.getFrequencies()];
    for (int freq = 0; freq < header.getFrequencies(); freq++) {
    data[0][frame][freq] = input.readFloat();
    }
    }
    for (int pow=1; pow<maxpow; pow++) {
    int maxframe = data[pow - 1].length / 2;
    data[pow] = new float[maxframe][];
    for (int frame=0; frame<maxframe; frame++){
    int maxfreq = data[pow - 1][0].length / 2;
    data[pow][frame] = new float[maxfreq];
    for (int freq=0; freq<maxfreq; freq++) {
    float sp = 0;
    for (int i=0;i<2;i++){
    for (int j=0;j<2;j++){
    sp = Math.max(sp, data[pow-1][frame*2+i][freq*2+j]);
    }
    }
    data[pow][frame][freq] = sp;
    }
    }
    }
    }*/
    public Header getHeader() {
        return header;
    }

    public float getMaximum() {
        return 255;
//        return maximum;
    }

    public float[] getData() {
        return data;
    }

    public int[] getImage() {
        return image;
    }
    
    public float getSpectro(int frame, int freq) {
        return image[frame*header.getFrequencies() + freq] & 0xFF;
//        return data[frame*header.getFrequencies() + freq];
    }

    public int getFrames() {
        return header.getFrames();
    }

    public int getFrequencies() {
        return header.getFrequencies();
    }

    public float getMinFrequency() {
        return header.getMinFrequency();
    }

    public float getMaxFrequency() {
        return header.getMaxFrequency();
    }

    public float getRate() {
        return header.getRate();
    }

    public static void main(final String[] args) {
        JFrame frame = new JFrame();

        JTable table = new JTable(4, 4);
        table.setPreferredScrollableViewportSize(new Dimension(100, 100));
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(200, 200));
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        frame.add(new JScrollPane(panel), BorderLayout.CENTER);

        frame.pack();
        frame.setVisible(true);
    }

}
