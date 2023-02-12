package streamline.plugin.tree;

import java.util.Iterator;

/**
 * Created on 08.05.2020.
 *
 * @author unicorn
 */
public class Traversator<B> implements Iterator<B> {
    private final Treeterator<B> treeterator;
    private B next;
    private boolean hasNext;

    public Traversator(Treeterator<B> treeterator) {
        this.treeterator = treeterator;
        findNext();
    }

    private void findNext() {
        hasNext = false;
        if (!treeterator.isLeaf()) {
            next = treeterator.up();
            hasNext = true;
        } else {
            while (!treeterator.isRoot()) {
                if (treeterator.hasNext()) {
                    next = treeterator.next();
                    hasNext = true;
                } else {
                    treeterator.down();
                }
            }
        }
    }

    @Override
    public boolean hasNext() {
        return hasNext;
    }

    @Override
    public B next() {
        try {
            return next;
        } finally {
            findNext();
        }
    }
}
