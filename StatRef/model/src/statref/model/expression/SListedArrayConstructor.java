package statref.model.expression;

import statref.model.SType;

import java.util.ArrayList;

public interface SListedArrayConstructor extends SExpression {
    SType getItemType();

    ArrayList<SVariable> getItems();
}
