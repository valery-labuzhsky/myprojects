package unicorn.voice.spectrogram;

import java.io.DataInput;
import java.io.IOException;

/**
 * @author uniCorn
 * @version 01.02.2008 19:15:57
 */
public class Header {
    private int frames;
    private int frequencies;
    private float minFrequency;
    private float maxFrequency;
    private float rate;

    public Header(int frames, int frequencies, float minFrequency, float maxFrequency, float rate) {
        this.frames = frames;
        this.frequencies = frequencies;
        this.minFrequency = minFrequency;
        this.maxFrequency = maxFrequency;
        this.rate = rate;
    }
    
    public Header(DataInput input) throws IOException {
        frames = input.readInt();
        frequencies = input.readInt();
        minFrequency = input.readFloat();
        maxFrequency = input.readFloat();
        rate = input.readFloat();
    }

    public int getFrames() {
        return frames;
    }

    public int getFrequencies() {
        return frequencies;
    }

    public float getMinFrequency() {
        return minFrequency;
    }

    public float getMaxFrequency() {
        return maxFrequency;
    }

    public float getRate() {
        return rate;
    }
}
