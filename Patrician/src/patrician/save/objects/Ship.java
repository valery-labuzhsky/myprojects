package patrician.save.objects;

import patrician.Paths;
import patrician.save.SaveReader;
import tools.find.Finder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created on 29/03/15.
 *
 * @author ptasha
 */
public class Ship extends ComplexItem<Ship> {
    public static int[] SHIPS = Finder.parseBytes("1 ff 7c  0  | ff ff ff ff  | ff ff ff ff  | ff ff ff ff  |  1 ff 7d  0  | ff ff ff ff  | ff ff ff ff  | ff ff ff ff  |  1 ff 7e  0  | ff ff ff ff ff ff ff ff  | ff ff ff ff  |  1 ff 7f  0  | ff ff ff ff  | ff ff ff ff  | ff ff ff ff  |  1 ff 80  0  | ff ff ff ff  | ff ff ff ff  | ff ff ff ff | 1 ff");

    public static enum Items implements ItemsEnum {
        OWNER(new IntItem(1)), // 24
        TYPE(new IntItem(1)), // 0 = Snaikka, 1 = Crayer, 2 = Cog, 3 = Hulk
        TYPE_SIZE(new HalfKnown("x x 0 0 x")),
        SIZE(new ShipSize()),
        SIZE_CONVOY(new HalfKnown("0 x x x 0 x x")),
        CONVOY(new IntItem(2)), // FF FF = no
        CONVOY_FLAG(new HalfKnown("x x x x x x x 0 x x x x x x x x x x x x x x x x x x x x x 0 0 0 x 0")), //36
        OPTIONAL_FLAG(new IntItem(1)),
        FLAG_SAILORS(new HalfKnown("x x x ff | ff 0 0 x | x")),
        SAILORS(new IntItem(1)),
        SAILORS_OPTIONAL(new HalfKnown("0 x x x x x 7 x x x x x x x 7 ff ff ff ff")),
        OPTIONAL(new ShipOptional()),
        CARGO(new Quantity()),
        PRICES(new Prices()),
        GOODS_COST(new IntItem(4)),
        UNKNOWN1(new HalfKnown("x x x x 0 0 0 0 x x x x x 0")),
        ROUTE(new IntItem(2)), // FF FF = none
        ON_ROUTE(new IntItem(1)), // 0 = No, 1 = Private, 16 = Public
        UNKNOWN112(new HalfKnown("x x")),
        TOWN2(new IntItem(1)),
        UNKNOWN12(new HalfKnown("x x")),
        CANNONS(new Cannons()),
        CUTLASS(new IntItem(2)),
        UNKNOWN2(new HalfKnown("x 0 0 x 0 0 0 ff x x")),
        NAME(new Name()), // 15
        UNKNOWN23(new HalfKnown("x 0  0  0  0  0  0  0  0  0  0  0  0  0  0  0  0")); // 16

        private final Item item;

        Items(Item item) {
            this.item = item;
        }

        @Override
        public Item getItem() {
            return item;
        }
    }

    public Ship() {
        super(Items.values());
    }

    @Override
    protected void initialize() {
        super.initialize();
        ((ShipOptional)getItem(Items.OPTIONAL)).ship = this;
    }

    public String getName() {
        return ((Name)getItem(Items.NAME)).getName();
    }

    public boolean isMine() {
        return getInt(Items.OWNER) == 0x24;
    }

    public boolean isOnRoute() {
        return getInt(Items.ON_ROUTE)==1;
    }

    public Integer getRoute() {
        int route = getInt(Items.ROUTE);
        if (route==0xffff) {
            return null;
        }
        return route;
    }

    public int getSize() {
        return getInt(Items.SIZE);
    }

    public int getCargoSpace() {
        return getSize() - getServiceSpace();
    }

    public int getAvailableSpace() {
        return getCargoSpace() - getCargo().getSize();
    }

    public boolean isEmpty() {
        return getCargo().getSize()==0;
    }

    public boolean isFull() {
        return getAvailableSpace() < 10;
    }

    public Quantity getCargo() {
        return getItem(Items.CARGO);
    }

    public int getServiceSpace() {
        return getCannons().getSize() + getCutlassSpace() + getSailorsSpace();
    }

    public Cannons getCannons() {
        return getItem(Items.CANNONS);
    }

    public int getCutlassSpace() {
        return (getCutlass() + 19)/20;
    }

    public int getCutlass() {
        return getInt(Items.CUTLASS);
    }

    public int getSailorsSpace() {
        int sailorsSpace = getSailors() - getType().getSailorsSpace();
        if (sailorsSpace<0) {
            return 0;
        }
        return sailorsSpace*2;
    }

    public int getSailors() {
        return getInt(Items.SAILORS);
    }

    public ShipType getType() {
        return ShipType.values()[getInt(Items.TYPE)];
    }

    private int getInt(Items item) {
        return ((IntItem) getItem(item)).getData();
    }

/*    @Override
    public String toString() {
        return getName();
    }*/

    public static void main(String[] args) throws IOException {
        System.out.println(new Ship().size());
//        printDiff("base", "base2", new RoutePoint());
//        findFixed("base", new Ship());
//        print("base", new Ship());

        /*List<Integer> base = SaveReader.getData(Paths.getGame("base"));
        List<Integer> base2 = SaveReader.getData(Paths.getGame("base3"));
        int head = 372 -31;
        int sh = 0;
//        rawCompare(base2, 961129 - head - 372* sh, base, 961470 - head - 372* sh - 372);
        rawCompare(base2, 960793 - 372* sh, base, 961756 - 372* sh);*/
        /*Ship ship = new Ship();
        ship.read(961470 - head - 372 * sh, base);
        System.out.println(ship.getName());
        rawCompare(base2, 959636 - head, base, 959610 - head);*/

//        960724 959668

//        printAll(Items.ROUTE, SaveReader.getData(Paths.getGame("base3")));

        /*List<Ship> ships = new ArrayList<>();
        ships.add(findShip(SaveReader.getData(Paths.getGame("base2")), "Veter"));
//        List<Ship> ships = getShips(SaveReader.getData(Paths.getGame("shipsquest")));
        System.out.println(ships.size());
        new Ship().findFixedUsing(ships);*/

        String name = "Veter";
        String name2 = "Veter";
        Ship ship1 = findShip(SaveReader.getData(Paths.getGame("base")), name);
        Ship ship2 = findShip(SaveReader.getData(Paths.getGame("base2")), name2);
//        System.out.println(ship1);
//        System.out.println(ship2);
        System.out.println(ship1.diff(ship2));


        /*HashSet<String> names = new HashSet<>();
        names.add("Cleapatra");
//        names.add("Constance");
        names.add("Fret");
//        names.add("Frieda");
//        names.add("Gabriel");
        names.add("Haithabu");
        names.add("Hyphen");
        names.add("Kotya");
//        names.add("Maria Magdalena");
        names.add("Ptasha");
//        names.add("Rode Hahn");
        names.add("Stoneheart");
//        names.add("Van Brakel");
        names.add("Veter");
        names.add("Voshod");
        names.add("Vostok");

//        names.add("St. Jakobus");
        ArrayList<Ship> ships = new ArrayList<>();
        ArrayList<Ship> others = new ArrayList<>();
        ShipIterator iterator = new ShipIterator(SaveReader.getData(Paths.getGame("base2")));
        Ship ship;
        HashSet<Integer> all = new HashSet<>();
        while ((ship = iterator.next())!=null) {
//            int x = ((Unknown) ship.getItem(Items.UNKNOWN0)).getSequence()[34]; //18, 34
//            all.add(x);
            if (ship.isMine()) {
//                System.out.println(ship.getName());
//                int x = ((Unknown) ship.getItem(Items.UNKNOWN000)).getSequence()[6];
//                System.out.println(x);
//                if (x==1) {
                if (names.remove(ship.getName())) {
                    ships.add(ship);
                } else {
                    others.add(ship);
                }
            }
        }
//        System.out.println(all);
//        System.out.println(ships);
        if (!names.isEmpty()) {
            System.out.println("Not found: "+names);
        }

        new Ship().findSimilarities(ships, others);*/

//        System.out.println(SHIPS.length);
//        System.out.println(Finder.parseBytes("1  2  b  0 |  0 a0 8c  0 |  0 a0 9f  1 |  0 ff ff").length);

//        961113
//        1005114


        // Cleapatra 159 110 162
    }

    private static void printAll(Items items, List<Integer> data) {
        HashSet<String> set = new HashSet<>();
        List<Ship> ships = getShips(data);
        for (Ship ship : ships) {
            set.add(ship.getItem(items).toString());
        }
        System.out.println(set);
    }

    private static Ship findShip(List<Integer> data, String name) {
        ShipIterator iterator = new ShipIterator(data);
        Ship ship;
        while ((ship = iterator.next())!=null) {
            if (ship.getName().equals(name)) {
                return ship;
            }
        }
        return null;
    }

    private static List<Ship> getShips(List<Integer> data) {
        ArrayList<Ship> ships = new ArrayList<>();
        ShipIterator iterator = new ShipIterator(data);
        Ship ship;
        while ((ship = iterator.next())!=null) {
            ships.add(ship);
        }
        System.out.println(ships.size());
        return ships;
    }

    private static void findFixed(String game) throws IOException {
        findFixed(game, new RoutePoint());
    }

    public static class ShipIterator {
        private final int beginning;
        private final List<Integer> data;
        private int i;
        private int offset;

        public ShipIterator(List<Integer> data) {
            this.data = data;
            this.beginning =  new Finder(data, SHIPS).next() + SHIPS.length;
            this.offset = beginning;
        }

        public Ship next() {
            Ship ship = null;
//            HalfKnown header = new HalfKnown("x x x x x");
            try {
                while (ship==null) {
//                header.read(offset, data);
                    i++;
                    if (data.get(offset + 1) != 0) {
//                    System.out.println("Last header: "+header);
                        return null;
                    }
                    if (data.get(offset) == 0xff) {
                        offset += 5;
//                    System.out.print(String.format("%h: EMPTY: %s", i - 1, header));
                        continue;
                    }
                    ship = new Ship();
                    offset += 4;
                    offset += ship.read(offset, data);
//                System.out.println(String.format("%h: ", i-1)+ship.getName());
                }
                return ship;
            } catch (Exception e) {
//                System.out.println(header);
                System.out.println(offset);
                e.printStackTrace(System.out);
                return null;
            }
        }
    }

    private static void rawCompare(List<Integer> data1, int pos1, List<Integer> data2, int pos2) {
        for (Items item : Items.values()) {
            System.out.println(item.name());
            int size = item.getItem().size();
            Unknown unknown1 = new Unknown(size);
            unknown1.read(pos1, data1);
            Unknown unknown2 = new Unknown(size);
            unknown2.read(pos2, data2);
            String diff = unknown1.diff(unknown2);
            if (diff!=null) {
                System.out.println(diff);
            } else {
                System.out.println(unknown1);
            }
            pos1+=size;
            pos2+=size;
        }
    }

    private static class ShipOptional extends HalfKnown {
        private Ship ship;

        public ShipOptional() {
            super("6 0 fc 5 c5 1 1c 6 5e 2 25 6 62 2 1e 6 6b 2 1f 6 73 2 fe 5 e1 4");
        }

        @Override
        public int size() {
            if (isFlagSet()) {
                return super.size();
            } else {
                return 0;
            }
        }

        @Override
        public int read(int offset, List<Integer> data) {
            if (isFlagSet()) {
                return super.read(offset, data);
            } else {
                return 0;
            }
        }

        private boolean isFlagSet() {
            if (ship==null) {
                return false;
            }
            return ((IntItem)ship.getItem(Items.OPTIONAL_FLAG)).getData()==1;
        }
    }
}