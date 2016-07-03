package tools.bindiff.findchange;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created on 22/02/15.
 *
 * @author ptasha
 */
public class ByteChanger {
    private final String filename;
    private final String result;
    private final HashMap<Integer, Integer> changes = new HashMap<>();

    public ByteChanger(String filename, String result) {
        this.filename = filename;
        this.result = result;
    }

    public void change(int index, int value) {
        changes.put(index, value);
    }

    public void change() throws IOException {
        FileInputStream input = new FileInputStream(filename);
        FileOutputStream output = new FileOutputStream(result);
        int b;
        int i= 0;
        while ((b=input.read())!=-1) {
            Integer value = changes.get(i);
            if (value==null) {
                output.write(b);
            } else {
                output.write(value);
            }
            i++;
        }
        output.close();
        input.close();
    }
}
