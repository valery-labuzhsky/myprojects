package patrician.save;

import patrician.Paths;
import patrician.save.objects.Goods;
import patrician.save.objects.RoutePoint;
import patrician.save.objects.TownManager;
import tools.find.Finder;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created on 10/03/15.
 *
 * @author ptasha
 */
public class PriceManager {


    public static void main(String[] args) throws IOException {
        String input = "save";
        String output = "save_mod";

        changePrice(input, output,
//                new Change(Goods.WINE, 10, Direction.ALL),
//                new Change(Goods.SALT, -1, Direction.BUY),
                new Change(Goods.SALT, -1, Direction.BUY)
                );
    }

    public static void changePrice(String input, String output, Change... changes) throws IOException {
        List<Integer> data = SaveReader.getData(Paths.getGame(input));
        Manipulator manipulator = new Manipulator(data);
        HashMap<Goods, Price> prices = new HashMap<>();
        System.out.println("Towns");
        changePrice(manipulator, data, TownManager.TOWN, 2, 10, prices, changes);
        System.out.println("Routes");
        changePrice(manipulator, data, RoutePoint.ROUTE, 0, 27, prices, changes);
        for (Map.Entry<Goods, Price> entry : prices.entrySet()) {
            System.out.println(entry.getKey()+": "+(-entry.getValue().buy)+"-"+entry.getValue().sell);
        }
        FileOutputStream outputStream = new FileOutputStream(Paths.getGame(output));
        new Compressor(data).write(outputStream);
        outputStream.close();
    }

    private static void changePrice(Manipulator manipulator, List<Integer> data, int[] sequence, int offset, int checkSum, HashMap<Goods, Price> prices, Change... changes) {
        Finder finder = new Finder(data, sequence);
        int pointer;
        int towns = 0;
        while ((pointer=finder.next())>=0) {
            towns++;
            for (Change change : changes) {
                Price checkPrice = prices.get(change.goods);
                if (checkPrice==null) {
                    checkPrice = new Price();
                    prices.put(change.goods, checkPrice);
                }
                int priceIndex = pointer + sequence.length + offset + change.goods.ordinal() * 4;
                int price = manipulator.get(priceIndex);
                if (price > 0 && change.direction.isSell()) {
                    price += change.count;
                    if (checkPrice.sell==0) {
                        checkPrice.sell = price;
                    } else if (checkPrice.sell!=price) {
                        System.out.println("Price differs for "+change.goods+": expected "+checkPrice.sell+", actual "+price);
                    }
                } else if (price < 0 && change.direction.isBuy()) {
                    price -= change.count;
                    if (checkPrice.buy==0) {
                        checkPrice.buy = price;
                    } else if (checkPrice.buy!=price) {
                        System.out.println("Price differs for "+change.goods+": expected "+checkPrice.buy+", actual "+price);
                    }
                }
                manipulator.set(priceIndex, price);
            }
        }
        if (towns != checkSum) {
            throw new RuntimeException("Town count check failed: "+towns+", expected "+checkSum);
        }
    }

    public static class Change {
        private final Goods goods;
        private final int count;
        private final Direction direction;

        public Change(Goods goods, int count) {
            this(goods, count, Direction.ALL);
        }

        public Change(Goods goods, int count, Direction direction) {
            this.goods = goods;
            this.count = count;
            this.direction = direction;
        }
    }

    public static class Price {
        private int buy;
        private int sell;
    }
}
