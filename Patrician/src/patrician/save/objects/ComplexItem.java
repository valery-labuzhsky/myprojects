package patrician.save.objects;

import patrician.Paths;
import patrician.save.SaveReader;
import tools.find.Finder;

import java.io.IOException;
import java.util.*;

/**
 * Created on 28/03/15.
 *
 * @author ptasha
 */
public abstract class ComplexItem<T extends ComplexItem<T>> implements Item<T> {
    private final LinkedHashMap<Enum, Item> itemMap = new LinkedHashMap<>();
    private int offset;

    @SuppressWarnings("unchecked")
    public ComplexItem(Enum<? extends ItemsEnum>... items) {
        for (Enum<? extends ItemsEnum> key : items) {
            Item item = (Item) ((ItemsEnum) key).getItem().clone();
            this.itemMap.put(key, item);
        }
        initialize();
    }

    protected void initialize() {
    }

    @SuppressWarnings("unchecked")
    public <I extends Item> I getItem(Enum name) {
        return (I) itemMap.get(name);
    }

    @Override
    public int size() {
        int size = 0;
        for (Item item : itemMap.values()) {
            size+=item.size();
        }
        return size;
    }

    public int getOffset() {
        return offset;
    }

    @Override
    public int get(int offset) {
        int itemOffset = offset;
        for (Item item : itemMap.values()) {
            if (itemOffset<item.size()) {
                return item.get(itemOffset);
            }
            itemOffset -= item.size();
        }
        throw new IndexOutOfBoundsException("Index "+offset+" is out of bound: size is "+size());
    }

    @Override
    public int read(int offset, List<Integer> data) {
        this.offset = offset;
        for (Map.Entry<Enum, Item> entry : itemMap.entrySet()) {
            Item item = entry.getValue();
            try {
                offset+=item.read(offset, data);
            } catch (RuntimeException e) {
                throw new RuntimeException("Failed to read "+entry.getKey());
            }
        }
        return offset - this.offset;
    }

    public String toString() {
        String string = getClass().getSimpleName()+"\n";
//        for (Item item : items) {
//            string+=item;
//        }
        for (Map.Entry<Enum, Item> entry : itemMap.entrySet()) {
            string+=entry.getKey()+": "+entry.getValue();
        }
        return string;
    }

    public List<T> find(List<Integer> data) {
        Fixed fixed = null;
        int offset = 0;
        int os = 0;
        for (Item item : itemMap.values()) {
            if (item instanceof Fixed) {
                if (fixed==null || item.size()>fixed.size()) {
                    fixed = (Fixed) item;
                    offset = os;
                }
            }
            os += item.size();
        }
        if (fixed==null) {
            throw new RuntimeException("Fixed item not found");
        }
        Finder finder = new Finder(data, fixed.getSequence());
        int i;
        ArrayList<T> list = new ArrayList<>();
        while ((i=finder.next())>0) {
            T clone = this.clone();
            clone.read(i-offset, data);
//            System.out.println(clone);
            list.add(clone);
        }
        return list;
    }

    public void findFixed(List<Integer> data) {
        List<T> list = find(data);
        System.out.println(list.size());
        findFixedUsing(list);
    }

    public void findFixedUsing(List<T> list) {
        for (Map.Entry<Enum, Item> entry : itemMap.entrySet()) {
            Item item = entry.getValue();
            if (item instanceof Unknown) {
                Enum key = entry.getKey();
                System.out.println(key);
                Unknown unknown = (Unknown) ((ComplexItem)list.get(0)).itemMap.get(key);
                boolean[] differs = new boolean[unknown.size()];
                setDiffers(differs, list, unknown, key, false);
                printHalfKnown(unknown, differs);
            }
        }
    }

    public void findSimilarities(List<T> list, List<T> others) {
        for (Map.Entry<Enum, Item> entry : itemMap.entrySet()) {
            Item item = entry.getValue();
            if (item instanceof Sequence) {
                Enum key = entry.getKey();
                System.out.println(key);
                Sequence unknown = (Sequence) ((ComplexItem)list.get(0)).itemMap.get(key);
                boolean[] differs = new boolean[unknown.size()];
                setDiffers(differs, list, unknown, key, false);
                setDiffers(differs, others, unknown, key, true);
                printHalfKnown(unknown, differs);
            }
        }
    }

    private void setDiffers(boolean[] differs, List<T> list, Sequence unknown, Enum key, boolean neg) {
        for (int index = 0; index < unknown.size(); index++) {
            Iterator<T> iterator = list.iterator();
            iterator.next();
            for (; iterator.hasNext(); ) {
                Sequence next = (Sequence) ((ComplexItem) iterator.next()).itemMap.get(key);
                if ((unknown.getSequence()[index] != next.getSequence()[index]) ^ neg) {
                    differs[index] = true;
                    break;
                }
            }
        }
    }

    private void printHalfKnown(Sequence unknown, boolean[] differs) {
        for (int i = 0; i < differs.length; i++) {
            if (differs[i]) {
                System.out.print("x ");
            } else {
                System.out.print(String.format("%h ", unknown.getSequence()[i]));
            }
        }
        System.out.println();
    }

    @SuppressWarnings("unchecked")
    @Override
    public T clone() {
        try {
            return (T) getClass().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public String diff(T item) {
        String diff = getClass().getSimpleName()+"\n";
        boolean eq = true;
        for (Map.Entry<Enum, Item> entry : itemMap.entrySet()) {
            String itemDiff = entry.getValue().diff(item.getItem(entry.getKey()));
            if (itemDiff!=null) {
                diff+= entry.getKey()+":\n"  + itemDiff;
                eq = false;
            } else {
                diff += entry.getKey()+":\n"  + entry.getValue();
            }
        }
        if (eq) {
            return null;
        }
        return diff;
    }



    public static <T extends ComplexItem<T>> void print(String game, T item) throws IOException {
        List<Integer> data = SaveReader.getData(Paths.getGame(game));
        List<T> points = item.find(data);
        int i = 0;
        for (T point : points) {
            System.out.println(String.format("%h ", i++) + point);
        }
    }

    public static <T extends ComplexItem<T>> void findFixed(String game, T item) throws IOException {
        item.findFixed(SaveReader.getData(Paths.getGame(game)));
    }


    public static <T extends ComplexItem<T>> void printDiff(String game1, String game2, T item) throws IOException {
        List<T> routes1 = item.find(SaveReader.getData(Paths.getGame(game1)));
        List<T> routes2 = item.find(SaveReader.getData(Paths.getGame(game2)));
        for (int i=0; i<routes1.size(); i++) {
            T route1 = routes1.get(i);
            T route2 = routes2.get(i);
            String diff = route1.diff(route2);
            if (diff!=null) {
                System.out.println(String.format("%h", i));
                System.out.println(diff);
            } else {
//                System.out.println(String.format("%h", i));
//                System.out.println(route1);
            }
        }
    }
}
