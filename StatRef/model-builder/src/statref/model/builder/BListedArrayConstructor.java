package statref.model.builder;

import statref.model.expression.SListedArrayConstructor;
import statref.model.SType;
import statref.model.expression.SVariable;

import java.util.ArrayList;

public class BListedArrayConstructor extends BElement implements SListedArrayConstructor {
    private final SType type;
    private ArrayList<SVariable> items = new ArrayList<>();

    public BListedArrayConstructor(SType type) {
        this.type = type;
    }

    public void addItem(SVariable item) {
        items.add(item);
    }

    @Override
    public SType getItemType() {
        return type;
    }

    @Override
    public ArrayList<SVariable> getItems() {
        return items;
    }
}
