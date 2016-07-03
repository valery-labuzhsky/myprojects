package patrician.save.objects;

import patrician.Paths;
import patrician.save.SaveReader;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created on 28/03/15.
 *
 * @author ptasha
 */
public class Quantity extends GoodsItem {
    @Override
    protected Integer getValue(Map.Entry<Goods, Integer> entry) {
        return super.getValue(entry)/entry.getKey().getUnitSize();
    }

    @Override
    public String toString() {
        return "Quantity\n"+super.toString();
    }


    public static void main(String[] args) throws IOException {
        List<Integer> data = SaveReader.getData(Paths.getGame("base"));
        Quantity quantity = new Quantity();
        quantity.read(956752, data);
        System.out.println(quantity);
    }

    public int getSize() {
        int size = 0;
        for (Integer load : goods.values()) {
            size+=load;
        }
        return size;
    }
}
