package tools.bindiff.compare;

import java.util.Collection;
import java.util.TreeSet;

/**
 * Created by ptasha on 17/02/15.
 */
public class MatchList extends AbstractMatch {
    private final int size;
    private final TreeSet<Integer> list1 = new TreeSet<>();
    private final TreeSet<Integer> list2 = new TreeSet<>();

    public MatchList(int size, Collection<Integer> indexes1, Collection<Integer> indexes2) {
        this.size = size;
        list1.addAll(indexes1);
        list2.addAll(indexes2);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public int getIndex(int n) {
        return getList(n).first();
    }

    private TreeSet<Integer> getList(int n) {
        return n==0?list1:list2;
    }

    public boolean isEmpty() {
        return list1.isEmpty() || list2.isEmpty();
    }

    public void removeItem(int n) {
        getList(n).pollFirst();
    }

    public Match extractMatch() {
        return new Match(size, list1.pollFirst(), list2.pollFirst());
    }
}
