package streamline.plugin.nodes.guts;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public interface KeyEventDispatcher extends KeyListener, EventDispatcher<KeyEvent> {
    @Override
    default void keyTyped(KeyEvent e) {
        dispatch(e);
    }

    @Override
    default void keyPressed(KeyEvent e) {
        dispatch(e);
    }

    @Override
    default void keyReleased(KeyEvent e) {
        dispatch(e);
    }
}
