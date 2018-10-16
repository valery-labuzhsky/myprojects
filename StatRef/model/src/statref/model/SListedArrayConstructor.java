package statref.model;

import java.util.ArrayList;

public interface SListedArrayConstructor extends SExpression {
    SType getType();

    ArrayList<SVariable> getItems();
}
