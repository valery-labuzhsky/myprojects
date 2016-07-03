package patrician.save;

/**
 * Created on 01/03/15.
 *
 * @author ptasha
 */
public class Bits {
    private int b;
    private int i;

    public void setByte(int b) {
        this.b = b;
        this.i = 0;
    }

    public int next() {
        i++;
        int bit = b & 1;
        b >>= 1;
        return bit;
    }

    public boolean hasNext() {
        return i < 8;
    }

    public static String toBites(int b) {
        String bb = "";
        for (int i=1; i<256; i<<=1) {
            bb+=(b&i)>0?'1':'0';
        }
        bb+=' ';
        return bb;
    }
}
