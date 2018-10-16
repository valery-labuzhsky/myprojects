package statref.model.builder;

import statref.model.SListedArrayConstructor;
import statref.model.SType;
import statref.model.SVariable;

import java.util.ArrayList;

public class BListedArrayConstructor implements SListedArrayConstructor {
    private final SType type;
    private ArrayList<SVariable> items = new ArrayList<>();

    public BListedArrayConstructor(SType type) {
        this.type = type;
    }

    public void addItem(SVariable item) {
        items.add(item);
    }

    @Override
    public SType getType() {
        return type;
    }

    @Override
    public ArrayList<SVariable> getItems() {
        return items;
    }
}
