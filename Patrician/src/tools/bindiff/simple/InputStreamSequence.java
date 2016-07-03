package tools.bindiff.simple;

import tools.bindiff.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

/**
 * Created on 08/03/15.
 *
 * @author ptasha
 */
public class InputStreamSequence implements Sequence {
    private final InputStream input;
    private int next;

    public InputStreamSequence(InputStream input) {
        this.input = input;
    }

    @Override
    public boolean hasNext() {
        if (next!=-1) {
            readNext();
        }
        return next!=-1;
    }

    private void readNext() {
        try {
            next = input.read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Integer next() {
        return next;
    }

    @Override
    public void remove() {
    }

    @Override
    public void skip(int size) {
        try {
            input.skip(size);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        try {
            input.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
