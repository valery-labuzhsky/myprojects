package statref.model.expressions;

import statref.model.types.SType;

import java.util.ArrayList;

public interface SListedArrayConstructor extends SExpression {
    SType getItemType();

    ArrayList<SLocalVariable> getItems();
}
