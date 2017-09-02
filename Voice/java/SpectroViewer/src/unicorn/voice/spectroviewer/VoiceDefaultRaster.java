package unicorn.voice.spectroviewer;

import java.awt.image.WritableRaster;
import java.awt.image.SampleModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.*;

/**
 * @author uniCorn
 * @version 03.08.2008 18:46:44
 */
public class VoiceDefaultRaster extends WritableRaster{
    static int scale = 20;
    
    public VoiceDefaultRaster(DataBuffer dataBuffer, int width, int height) {
        super(new ComponentSampleModel(dataBuffer.getDataType(), width/scale, height/scale, height*scale, 1*scale, new int[]{0}), dataBuffer, new Point(0, 0));
    }
}
