package patrician.save.objects;

import tools.find.Finder;

import java.io.IOException;

/**
 * Created on 28/03/15.
 *
 * @author ptasha
 */
public class RoutePoint extends ComplexItem<RoutePoint> {
    public static final int[] ROUTE = Finder.parseBytes("3 13  8  2 | 0 11  5  c  |  d  1 10  f  | 12  4  9  6  |  b  a  7  e  | 15 17 16 14");

    public int getNext() {
        IntItem data = getItem(Items.NEXT);
        return data.getData();
    }

    public int getTown() {
        IntItem data = getItem(Items.TOWN);
        return data.getData();
    }

    public boolean isFirst() {
        Flags flags = getItem(Items.FLAGS);
        return FIRST.isSet(flags.getData());
    }

    public static enum Items implements ItemsEnum {
        NEXT(new IntItem(2)),
        TOWN(new IntItem(1)),
        FLAGS(new Flags()),
        FIXED1(new Fixed(ROUTE)),
        PRICES(new Prices()),
//        FIXED2(new Fixed(Finder.parseBytes("d0  7  0  0 | 40  6  0  0 | 60  9  0  0 | f0  a  0  0"))),
        QUANTITY(new Quantity());
//        FIXED3(new Fixed(Finder.parseBytes("0  0  0  0  | 0  0  0  0  | 0  0  0  0  | 0  0  0  0")));

        private final Item item;

        Items(Item item) {
            this.item = item;
        }

        @Override
        public Item getItem() {
            return item;
        }
    }

    public static final Flag FIRST = new Flag(4);
    public static final Flag REPAIR = new Flag(1);
    public static final Flag SKIP = new Flag(8);

    public RoutePoint() {
        super(Items.values());
    }

    public static void main(String[] args) throws IOException {
        System.out.println(new RoutePoint().size());
//        printDiff("base", "base2", new RoutePoint());
        //        findFixed("base");
        print("base2", new RoutePoint());
        //        new RoutePoint().findFixed(data);
    }

    private static void print(String game) throws IOException {
        print(game, new RoutePoint());
    }

    private static void findFixed(String game) throws IOException {
        findFixed(game, new RoutePoint());
    }
}
