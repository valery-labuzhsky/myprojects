package unicorn.utils;

import unicorn.utils.reflection.Reflection;

import java.lang.reflect.*;
import java.util.LinkedList;
import java.util.List;

/**
 * @author unicorn
 */
@SuppressWarnings("unchecked")
public class Listeners<L> {
    private final List<L> listeners = new LinkedList<L>();

    public final L fire;

    public Listeners(Class<L> type) {
        fire = createFire(type);
    }

    protected Listeners() {
        fire = createFire();
    }

    protected L createFire() {
        ParameterizedType superclass = (ParameterizedType) this.getClass().getGenericSuperclass();
        return createFire((Class<L>) superclass.getActualTypeArguments()[0]);
    }

    protected L createFire(Class<L> type) {
        return (L) Proxy.newProxyInstance(Listeners.class.getClassLoader(), new Class[]{type}, new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                for (L listener : listeners) {
                    Reflection.invoke(listener, method, args);
                }
                return null;
            }
        });
    }

    public void add(L listener) {
        listeners.add(listener);
    }
}
