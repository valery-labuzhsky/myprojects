package board;

import java.util.AbstractSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created on 11.04.2020.
 *
 * @author ptasha
 */
public class ComboSet<E> extends AbstractSet<E> {
    private final Set<E> head;
    private final Set<E> tail = new HashSet<>();

    public ComboSet(Set<E> head) {
        this.head = head;
    }

    public ComboSet(Set<E> head, E tail) {
        this(head);
        add(tail);
    }

    @Override
    public boolean add(E e) {
        return !head.contains(e) && tail.add(e);
    }

    @Override
    public boolean remove(Object o) {
        return tail.remove(o) || head.remove(o);
    }

    @Override
    public boolean contains(Object o) {
        return tail.contains(o) || head.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            Iterator<E> iterator = head.iterator();
            boolean h = true;

            @Override
            public boolean hasNext() {
                if (h && !iterator.hasNext()) {
                    h = false;
                    iterator = tail.iterator();
                }
                return iterator.hasNext();
            }

            @Override
            public E next() {
                return iterator.next();
            }
        };
    }

    @Override
    public int size() {
        return head.size() + tail.size();
    }
}
