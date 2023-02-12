package streamline.plugin.nodes.guts;

import java.awt.*;

public interface EventDispatcher<E extends AWTEvent> {
    void dispatch(E e);
}
