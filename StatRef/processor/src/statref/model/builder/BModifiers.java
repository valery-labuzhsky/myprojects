package statref.model.builder;

import statref.model.SModifiers;

import javax.lang.model.element.Modifier;

public interface BModifiers<T> extends SModifiers {
    default T public_() {
        return addModifier(Modifier.PUBLIC);
    }

    default T addModifier(Modifier modifier) {
        getModifiers().add(modifier);
        return (T) this;
    }

    default T static_() {
        return addModifier(Modifier.STATIC);
    }

    default T final_() {
        return addModifier(Modifier.FINAL);
    }
}
