package streamline.plugin.refactoring.guts;

import java.util.ArrayList;
import java.util.List;

public class Listeners {
    private final List<Runnable> listeners = new ArrayList<>();
    private Listeners newListeners;

    public Listeners() {
    }

    public void listen(Runnable listener) {
        if (newListeners != null) {
            newListeners.listen(listener);
        } else {
            listeners.add(listener);
        }
    }

    public void invoke(Runnable listener) {
        if (newListeners != null) {
            newListeners.invoke(listener);
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
