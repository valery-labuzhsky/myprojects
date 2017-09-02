package unicorn.voice.wavparser;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

/**
 * @author uniCorn
 * @version 16.01.2008 14:45:40
 */
public class WavParser {
    public static void main(String[] args) throws IOException, UnsupportedAudioFileException {
        AudioInputStream voice = AudioSystem.getAudioInputStream(new File(""));

        
    }
}
