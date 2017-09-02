package unicorn.utils.reflection;

import java.lang.reflect.InvocationTargetException;

/**
 * @author unicorn
 */
public abstract class Action<T> {
    protected abstract T doAction() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException;
}
