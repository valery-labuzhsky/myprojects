package streamline.plugin.refactoring;

import java.util.ArrayList;
import java.util.List;

public class Listeners {
    private final List<Runnable> listeners = new ArrayList<>();
    private Listeners newListeners;

    public Listeners() {
    }

    public void add(Runnable listener) {
        if (newListeners != null) {
            newListeners.add(listener);
        } else {
            listeners.add(listener);
            listener.run();
        }
    }

    public void fire() {
        if (newListeners == null) {
            newListeners = new Listeners();
            for (Runnable listener : listeners) {
                listener.run();
            }
            listeners.addAll(newListeners.listeners);
            newListeners = null;
        } else {
            for (Runnable listener : listeners) {
                listener.run();
            }
            newListeners.fire();
        }
    }
}
