package unicorn.voice.spectroviewer;

import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.color.ColorSpace;

/**
 * @author uniCorn
 * @version 03.08.2008 18:39:02
 */
public class NormalizedGrayColorSpace extends ICC_ColorSpace{
    private float minValue;
    private float maxValue;

    public NormalizedGrayColorSpace(float minValue, float maxValue) {
        super(ICC_Profile.getInstance(ColorSpace.CS_GRAY));
        this.minValue = minValue;
        this.maxValue = maxValue; 
    }

    public float getMinValue(int component) {
        return minValue;
    }

    public float getMaxValue(int component) {
        return maxValue;
    }
}
