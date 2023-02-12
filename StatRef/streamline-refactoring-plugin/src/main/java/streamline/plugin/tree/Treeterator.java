package streamline.plugin.tree;

/**
 * Created on 08.05.2020.
 *
 * @author unicorn
 */
public interface Treeterator<B> {
    boolean isLeaf();

    B up();

    boolean isRoot();

    B down();

    boolean hasNext();

    B next();

    class Delegate<B> implements Treeterator<B> {
        private final Treeterator<B> delegate;
        protected B current;

        public Delegate(Treeterator<B> delegate) {
            this.delegate = delegate;
        }

        @Override
        public boolean isLeaf() {
            return delegate.isLeaf();
        }

        @Override
        public B up() {
            return current = delegate.up();
        }

        @Override
        public boolean isRoot() {
            return delegate.isRoot();
        }

        @Override
        public B down() {
            return current = delegate.down();
        }

        @Override
        public boolean hasNext() {
            return delegate.hasNext();
        }

        @Override
        public B next() {
            return current = delegate.next();
        }
    }
}
