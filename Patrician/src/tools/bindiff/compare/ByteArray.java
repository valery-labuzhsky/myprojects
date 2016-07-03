package tools.bindiff.compare;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by ptasha on 14/02/15.
 */
public class ByteArray {
    private final byte[] array;

    public ByteArray(byte[] array) {
        this.array = array;
    }

    public ByteArray(String file) throws IOException {
        FileInputStream input = new FileInputStream(file);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            int b;
            while ((b = input.read()) >= 0) {
                output.write(b);
            }
        } finally {
            input.close();
        }
        this.array = output.toByteArray();
    }

    public int size() {
        return array.length;
    }

    public byte get(int i) {
        return array[i];
    }

    public boolean exists(int i) {
        return i >=0 && i<size();
    }
}
