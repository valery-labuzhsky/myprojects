package streamline.plugin.refactoring.guts.flow;

import statref.model.idea.IElement;

import java.util.ArrayList;

public class ElementStack {
    private int index = -1;
    private final ArrayList<IElement> stack = new ArrayList<>();

    public IElement getTop() {
        return get(index);
    }

    public void add(IElement element) {
        stack.add(element);
        index++;
    }

    private IElement get(int index) {
        if (index < 0 || index >= stack.size()) {
            return null;
        }
        return stack.get(index);
    }

    public void up() {
        index++;
    }

    public void down() {
        index--;
    }
}
