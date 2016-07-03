package patrician.save.objects;

import patrician.save.Manipulator;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created on 28/03/15.
 *
 * @author ptasha
 */
public class GoodsItem extends Sequence<GoodsItem> {
    protected final TreeMap<Goods, Integer> goods;

    public GoodsItem() {
        this(new TreeMap<Goods, Integer>(new Comparator<Goods>() {
            @Override
            public int compare(Goods o1, Goods o2) {
                return o1.name().compareTo(o2.name());
            }
        }));
    }

    protected GoodsItem(TreeMap<Goods, Integer> goods) {
        super(Goods.values().length*4);
        this.goods = goods;
    }

    @Override
    public int read(int offset, List<Integer> data) {
        for (Goods goods : Goods.values()) {
            this.goods.put(goods, Manipulator.get(offset + goods.ordinal() * 4, data));
        }
        return super.read(offset, data);
    }

    public String toString() {
        String string = "";
        for (Map.Entry<Goods, Integer> entry : goods.entrySet()) {
            string+=String.format("%10s: %11d\n", entry.getKey(), getValue(entry));
        }
        return string;
    }

    protected Integer getValue(Map.Entry<Goods, Integer> entry) {
//        float f = ByteBuffer.wrap(ByteBuffer.allocate(4).putInt(entry.getValue()).array()).getFloat();
//        return (int)f;
        return entry.getValue();
    }

    @SuppressWarnings("unchecked")
    @Override
    public GoodsItem clone() {
        try {
            return getClass().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
