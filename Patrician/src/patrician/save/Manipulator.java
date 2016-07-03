package patrician.save;

import patrician.Paths;

import java.io.IOException;
import java.util.List;

/**
 * Created on 09/03/15.
 *
 * @author ptasha
 */
public class Manipulator {
    private final List<Integer> data;

    public Manipulator(List<Integer> data) {
        this.data = data;
    }

    public int get(int index) {
        return get(index, this.data);
    }

    public static int get(int index, List<Integer> data) {
        int value = 0;
        for (int i=0; i<4; i++) {
            value <<= 8;
            value |= data.get(index + 3 - i);
        }
        return value;
    }

    public void set(int index, int value) {
        for (int i=0; i<4; i++) {
            data.set(index++, value & 0xff);
            value >>= 8;
        }
    }

    public static void main(String[] args) throws IOException {
        List<Integer> data = SaveReader.getData(Paths.getGame("base"));
        Manipulator manipulator = new Manipulator(data);
        manipulator.set(825987, 825987);
        System.out.println(manipulator.get(825987));
    }
}
