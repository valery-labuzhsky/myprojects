package streamline.plugin.refactoring.guts;

import com.intellij.openapi.diagnostic.Logger;

import java.util.ArrayList;
import java.util.List;

public class Listeners {
    private static final Logger log = Logger.getInstance(Listeners.class);

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
            run(listener);
        }
    }

    private void run(Runnable listener) {
        try {
            listener.run();
        } catch (Exception e) {
            log.warn("Listener thrown exception", e);
        }
    }

    public void fire() {
        if (newListeners == null) {
            newListeners = new Listeners();
            for (Runnable listener : listeners) {
                run(listener);
            }
            listeners.addAll(newListeners.listeners);
            newListeners = null;
        } else {
            for (Runnable listener : listeners) {
                run(listener);
            }
            newListeners.fire();
        }
    }
}
