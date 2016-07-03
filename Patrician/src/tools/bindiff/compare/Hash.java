package tools.bindiff.compare;

import tools.bindiff.utils.CollectionHashMap;
import tools.bindiff.utils.TreeSetFactory;

import java.util.*;

/**
 * Created on 14/02/15.
 *
 * @author ptasha
 */
public class Hash {
    private final int n;
    private final Hash parent;
    private final int size;
    private final ByteArray array;
    private final HashMap<ByteKey, HashSet<Integer>> hash = new HashMap<>();
    private final HashSet<ByteKey> singles = new HashSet<>();
    private final HashMap<Integer, HashSet<Match>> future;

    public Hash(ByteArray array, int n) {
        this.n = n;
        this.parent = null;
        this.array = array;
        for (int i = 0; i < array.size(); i++) {
            ByteKey key = new ByteKey(array.get(i));
            putIndex(key, i);
        }
        future = new HashMap<>();
        size = 1;
    }

    private void putIndex(ByteKey key, int i) {
        HashSet<Integer> indexes = hash.get(key);
        if (indexes == null) {
            indexes = new HashSet<>();
            hash.put(key, indexes);
        }
        if (!indexes.add(i)) {
        }
    }

    public Hash(Hash parent) {
        this.n = parent.n;
        this.size = parent.size + 1;
        this.parent = parent;
        this.array = parent.array;
        for (Map.Entry<ByteKey, HashSet<Integer>> entry : parent.hash.entrySet()) {
            ByteKey key = entry.getKey();
            for (Integer index : entry.getValue()) {
                int i = index + key.size();
                if (i < array.size()) {
                    byte b = array.get(i);
                    putIndex(new ByteKey(key, b), index);
                }
            }
        }
        this.future = parent.future;
//        parent.processFutures();
    }

    private void processFutures() {
        HashSet<Match> futures = future.remove(getSequenceSize());
        if (futures != null) {
            for (Match match : futures) {
                int index = match.getLine(n).getIndex1();
                int size = getSequenceSize();
                putIndex(createKey(index, size), index);
            }
        }
    }

    private ByteKey createKey(int index, int size) {
        ByteKey key = null;
        for (int i = 0; i < size; i++) {
            key = new ByteKey(key, array.get(index + i));
        }
        return key;
    }

    public void intersect(Hash hash) {
        this.intersect(hash.hash, true);
        hash.intersect(this.hash, false);
        extendSingles(hash);
    }

    private void extendSingles(Hash hash) {
        for (Iterator<ByteKey> iterator = singles.iterator(); iterator.hasNext(); ) {
            ByteKey single = iterator.next();
            iterator.remove();
            HashSet<Integer> indexes1 = this.hash.remove(single);
            HashSet<Integer> indexes2 = hash.hash.remove(single);
            for (Integer index1 : indexes1) {
                for (Integer index2 : indexes2) {
                    Match m = Match.extend(getSequenceSize(), new int[]{index1, index2}, new ByteArray[]{array, hash.array});
                    addFuture(m);
//                    hash.addFuture(m);
                }
            }
        }
    }

    private void addFuture(Match match) {
        HashSet<Match> matches = future.get(match.size());
        if (matches == null) {
            matches = new HashSet<>();
            future.put(match.size(), matches);
        }
        matches.add(match);
    }

    private void intersect(HashMap<ByteKey, HashSet<Integer>> hash, boolean createSingles) {
        for (Iterator<Map.Entry<ByteKey, HashSet<Integer>>> iterator = this.hash.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<ByteKey, HashSet<Integer>> entry = iterator.next();
            if (!hash.containsKey(entry.getKey())) {
                iterator.remove();
//            } else if (createSingles && (entry.getValue().size() * hash.get(entry.getKey()).size()) == 1) {
            } else if (createSingles && (entry.getValue().size() * hash.get(entry.getKey()).size()) < getSequenceSize()) {
                singles.add(entry.getKey());
            }
        }
    }

    public boolean isEmpty() {
        return hash.isEmpty();
    }

    public Hash getParent() {
        return parent;
    }

    public Collection<? extends AbstractMatch> match(Hash hash) {
        TreeSet<Match> order = new TreeSet<>();
        for (Iterator<Map.Entry<Integer, HashSet<Match>>> iterator = future.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<Integer, HashSet<Match>> entry = iterator.next();
            if (entry.getKey() > getSequenceSize()) {
                iterator.remove();
                for (Match match : entry.getValue()) {
                    order.add(match);
                }
            }
        }
        /*for (HashSet<Match> matches : future.values()) {
            for (Match match : matches) {
                order.add(match);
            }
        }
        future.clear();*/

        TreeSet<AbstractMatch> matches1 = new TreeSet<>(new Match.LineComparator(0));
        TreeSet<AbstractMatch> matches2 = new TreeSet<>(new Match.LineComparator(1));

        processMatches(order, matches1, matches2);

        extractMatches(hash, matches1, matches2);

//        return matches1;
        TreeSet<Match> diff = getDiff(matches1, matches2);
        return diff;
    }

    private void processMatches(TreeSet<Match> order, TreeSet<AbstractMatch> matches1, TreeSet<AbstractMatch> matches2) {
        HashSet<Match> futures = future.remove(getSequenceSize());
        if (futures!=null) {
            order.addAll(futures);
        }
        for (Match match : order) {
            Match sub = subtract(match, matches1, matches2);
            if (sub!=match) {
                if (sub!=null) {
                    addFuture(sub);
                }
            } else {
                matches1.add(match);
                matches2.add(match);
            }
//            if (!intersects(match, matches1) && !intersects(match, matches2)) {
//            }
        }
    }

    private Match subtract(Match match, TreeSet<AbstractMatch> matches1, TreeSet<AbstractMatch> matches2) {
        Match sub = subtract(match, matches1, 0);
        if (sub!=match) {
            return sub;
        }
        sub = subtract(match, matches2, 1);
        if (sub!=match) {
            return sub;
        }
        return sub;
    }

    private Match subtract(Match match, TreeSet<AbstractMatch> matches, int n) {
        Match sub = match.subtract(matches.floor(match), n);
        if (sub!=match) {
            return sub;
        }
        sub = match.subtract(matches.ceiling(match), n);
        if (sub!=match) {
            return sub;
        }
        return sub;
    }

    private TreeSet<Match> getDiff(TreeSet<AbstractMatch> matches1, TreeSet<AbstractMatch> matches2) {
        TreeSet<Match> diff = new TreeSet<>(new Match.LineComparator(0));
        if (matches1.size()<2) {
            return diff;
        }
        Iterator<AbstractMatch> iterator = matches1.iterator();
        AbstractMatch lower = iterator.next();
        while (iterator.hasNext()) {
            AbstractMatch upper = lower;
            lower = iterator.next();
            if (upper.getDiff() == lower.getDiff() && lower==matches2.higher(upper)) {
                diff.add(diff(upper, lower));
            }
        }
        return diff;
    }

    private Match diff(AbstractMatch upper, AbstractMatch lower) {
        int index1 = upper.getIndex2(0) + 1;
        int index2 = lower.getIndex(0) - 1;
        int size = index2 - index1 + 1;
        int matchIndex = upper.getIndex2(1) + 1;
        return new Match(size, index1, matchIndex);
    }

    private void extractMatches(Hash hash, CollectionHashMap<Integer, Match, MatchSet> matches) {
        for (Map.Entry<ByteKey, HashSet<Integer>> entry : this.hash.entrySet()) {
            HashSet<Integer> indexes1 = entry.getValue();
            HashSet<Integer> indexes2 = hash.hash.get(entry.getKey());
            for (Integer index1 : indexes1) {
                for (Integer index2 : indexes2) {
                    Match match = new Match(getSequenceSize(), index1, index2);
                    matches.add(match.getDiff(), match);
                }
            }
        }
        if (parent.size > 1) {
            parent.extractMatches(hash.parent, matches);
        }
    }

    private void extractMatches(Hash hash, TreeSet<AbstractMatch> matches1, TreeSet<AbstractMatch> matches2) {
        TreeSet<Match> matches = new TreeSet<>();
        for (Map.Entry<ByteKey, HashSet<Integer>> entry : this.hash.entrySet()) {
            HashSet<Integer> indexes1 = entry.getValue();
            filter(indexes1, matches1, 0);
            HashSet<Integer> indexes2 = hash.hash.get(entry.getKey());
            filter(indexes2, matches2, 1);
            for (Integer index1 : indexes1) {
                for (Integer index2 : indexes2) {
                    matches.add(new Match(getSequenceSize(), index1, index2));
                }
            }
        }

        processMatches(matches, matches1, matches2);

        System.out.println("Matches "+getSequenceSize()+" extracted");
        if (parent.size > 1) {
            parent.extractMatches(hash.parent, matches1, matches2);
        }
    }

    private void filter(HashSet<Integer> indexes, TreeSet<AbstractMatch> matches, int n) {
        int[] array = new int[2];
        for (Iterator<Integer> iterator = indexes.iterator(); iterator.hasNext(); ) {
            Integer index = iterator.next();
            array[n] = index;
            Match match = new Match(getSequenceSize(), array);
            if (intersects(match, matches)) {
                iterator.remove();
            }
        }
    }

    private boolean intersects(AbstractMatch match, TreeSet<AbstractMatch> matches) {
        return match.intersects(matches.floor(match)) || match.intersects(matches.ceiling(match));
    }

    public int getSequenceSize() {
        return size;
    }

    public int getHashSize() {
        return hash.size();
    }

    @Override
    public String toString() {
        return "" + getSequenceSize() + "->" + getHashSize();
    }

    public int get(int index) {
        byte b = array.get(index);
        if (b<0) {
            return 128 + b;
        }
        return b;
    }
}
