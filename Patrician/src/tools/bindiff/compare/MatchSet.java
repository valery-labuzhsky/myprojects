package tools.bindiff.compare;

import tools.bindiff.utils.CollectionFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * Created on 21/02/15.
 *
 * @author ptasha
 */
public class MatchSet extends TreeSet<Match> {
    public MatchSet() {
        super(new Match.LineComparator(0));
    }

    @Override
    public boolean add(Match match) {
        if (intersects(match)) {
            return false;
        }
        return super.add(match);
    }

    @Override
    public boolean addAll(Collection<? extends Match> c) {
        boolean changed = false;
        for (Match match : c) {
            changed |= add(match);
        }
        return changed;
    }

    private boolean intersects(Match match) {
        return match.intersects(floor(match)) || match.intersects(ceiling(match));
    }

    public static final CollectionFactory<Match,MatchSet> FACTORY = new CollectionFactory<Match,MatchSet>() {
        @Override
        public MatchSet create() {
            return new MatchSet();
        }
    };

    public void merge() {
        if (this.size()<2) {
            return;
        }
        ArrayList<Match> remove = new ArrayList<>();
        ArrayList<Match> add = new ArrayList<>();
        Iterator<Match> iterator = this.iterator();
        Match lower = iterator.next();
        while (iterator.hasNext()) {
            Match upper = lower;
            lower = iterator.next();
            if (lower.getIndex(0) - upper.getIndex2(0) < 12) {
//                System.out.println(upper.getIndex2(0));
//                System.out.println(lower.getIndex(0));
                remove.add(upper);
                remove.add(lower);
                add.add(upper.merge(lower));
            }
        }
        removeAll(remove);
        super.addAll(add);
    }

    public static void main(String[] args) {
        MatchSet matches = new MatchSet();
        matches.add(new Match(89, 0, 0));
        matches.add(new Match(19951, 90, 90));
        matches.merge();
        System.out.println(matches);
    }
}
