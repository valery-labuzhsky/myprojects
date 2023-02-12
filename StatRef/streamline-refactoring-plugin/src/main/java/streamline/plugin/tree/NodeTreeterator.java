package streamline.plugin.tree;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * Created on 08.05.2020.
 *
 * @author unicorn
 */
public abstract class NodeTreeterator<N> implements Treeterator<N> {
    private final LinkedList<N> nodes = new LinkedList<>();
    private final LinkedList<Iterator<N>> iterators = new LinkedList<>();

    private N node;
    private Iterator<N> iterator;

    public NodeTreeterator(N node) {
        this.node = node;
    }

    @Override
    public boolean isLeaf() {
        return isLeaf(node);
    }

    protected abstract boolean isLeaf(N node);

    @Override
    public N up() {
        if (isLeaf()) {
            throw new NoSuchElementException();
        }
        nodes.push(node);
        iterators.push(iterator);
        iterator = children(node);
        node = iterator.next();
        return node;
    }

    @NotNull
    protected abstract Iterator<N> children(N node);

    @Override
    public boolean isRoot() {
        return nodes.isEmpty();
    }

    @Override
    public N down() {
        node = nodes.pop();
        iterator = iterators.pop();
        return node;
    }

    @Override
    public boolean hasNext() {
        if (iterator == null) {
            return false;
        }
        return iterator.hasNext();
    }

    @Override
    public N next() {
        if (iterator == null) {
            throw new NoSuchElementException();
        }
        node = iterator.next();
        return node;
    }
}
