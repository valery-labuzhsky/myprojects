package patrician.save.objects;

import java.util.List;

/**
 * Created on 30/03/15.
 *
 * @author ptasha
 */
public class Cannons extends Sequence<Cannons> {
    private int size;

    public Cannons() {
        super(4*6);
    }
    // 0 = Small catapult
    // 1 = Small balist
    // 2 = Large catapult
    // 3 = Large balist ?
    // 4 = Large cannon
    // 6 = 2 slots
    // 7 = Impossible
    // ff = empty


    @Override
    public int read(int offset, List<Integer> data) {
        try {
            return super.read(offset, data);
        } finally {
            for (int cannon : sequence) {
                if (cannon<7) {
                    size+=5;
                }
            }
        }
    }

    @Override
    public String toString() {
        return "Cannons\n";
    }

    public int getSize() {
        return size;
    }
}
