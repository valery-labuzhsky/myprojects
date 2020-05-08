package streamline.plugin.tree;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Created on 08.05.2020.
 *
 * @author unicorn
 */
public class Monkey<T> implements Iterable<T> {
    private final Treeterator<T> treeterator;

    public Monkey(Treeterator<T> treeterator) {
        this.treeterator = treeterator;
    }

    public Stream<T> climb() {
        return StreamSupport.stream(spliterator(), false);
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return new Traversator<T>(treeterator());
    }

    public Treeterator<T> treeterator() {
        return treeterator;
    }

    @SafeVarargs
    public final Monkey<T> branches(Class<? extends T>... branches) {
        Predicate<T> predicate = t -> false;
        for (Class<? extends T> branch : branches) {
            predicate = predicate.or(branch::isInstance);
        }
        Predicate<T> isBranch = predicate;
        return new Monkey<>(new Treeterator.Delegate<T>(treeterator) {
            @Override
            public boolean isLeaf() {
                return !isBranch.test(current) || super.isLeaf();
            }
        });
    }

    public Monkey<T> leaves(Class<? extends T> leaf) {
        return new Monkey<>(new Treeterator.Delegate<T>(treeterator) {
            @Override
            public boolean isLeaf() {
                return leaf.isInstance(current) || super.isLeaf();
            }
        });
    }

    public <B extends T> Stream<B> climb(Class<B> leaf) {
        return leaves(leaf).climb().filter(b -> !leaf.isInstance(b)).map(b -> (B) b);
    }
}
