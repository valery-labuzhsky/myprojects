package streamline.plugin.nodes;

import java.awt.*;

public interface EventDispatcher<E extends AWTEvent> {
    void dispatch(E e);
}
