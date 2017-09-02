package unicorn.utils.reflection;

import unicorn.utils.collection.MapBuilder;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author unicorn
 */
@SuppressWarnings({"unchecked"})
public class Reflection {
    public static <T> T valueOf(final Class<T> clazz, final String string) {
        return doAction(new Action<T>() {
            @Override
            protected T doAction() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
                return (T) clazz.getMethod("valueOf", String.class).invoke(null, string);
            }
        });

    }

    public static <T> T create(final Class<T> clazz, final Object... parameters) {
        return doAction(new Action<T>() {
            @Override
            protected T doAction() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
                return getConstructor(clazz, parameters).newInstance(parameters);
            }
        });
    }

    public static <O> Object invoke(final O object, final Method method, final Object[] args) {
        return doAction(new Action<O>() {
            @Override
            protected O doAction() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
                return (O) method.invoke(object, args);
            }
        });
    }

    public static <T> Constructor<T> getConstructor(Class<T> clazz, Object... parameters) throws NoSuchMethodException {
        Constructor<?>[] constructors = clazz.getConstructors();
        List<Constructor<?>> found = new ArrayList<Constructor<?>>();
        for (Constructor<?> constructor : constructors) {
            if (match(constructor, parameters)) {
                found.add(constructor);
            }
        }
        switch (found.size()) {
            case 0:
                throw new NoSuchMethodException("Constructor of " + clazz.getName() + " with parameters " + Arrays.toString(parameters) + " not found");
            case 1:
                return (Constructor<T>) found.get(0);
            default:
                throw new NoSuchMethodException("Constructor of " + clazz.getName() + " with parameters " + Arrays.toString(parameters) + " is obvious: " + found);
        }
    }

    private static boolean match(Constructor<?> constructor, Object[] parameters) {
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        if (parameterTypes.length != parameters.length) return false;
        for (int i = 0; i < parameters.length; i++) {
            if (!match(parameters[i], parameterTypes[i])) return false;
        }
        return true;
    }

    private static boolean match(Object parameter, Class<?> parameterType) {
        if (parameter == null) return !parameterType.isPrimitive();
        if (parameterType.isPrimitive()) {
            parameterType = wrap(parameterType);
        }
        return parameterType.isAssignableFrom(parameter.getClass());
    }

    private static final Map<Class<?>, Class<?>> PRIMITIVES_TO_WRAPPERS
            = new MapBuilder<Class<?>, Class<?>>(new HashMap<Class<?>, Class<?>>())
            .put(boolean.class, Boolean.class)
            .put(byte.class, Byte.class)
            .put(char.class, Character.class)
            .put(double.class, Double.class)
            .put(float.class, Float.class)
            .put(int.class, Integer.class)
            .put(long.class, Long.class)
            .put(short.class, Short.class)
            .put(void.class, Void.class)
            .getMap();

    private static Class<?> wrap(Class<?> parameterType) {
        return PRIMITIVES_TO_WRAPPERS.get(parameterType);
    }

    public static <T> T doAction(Action<T> action) {
        try {
            return action.doAction();
        } catch (IllegalAccessException e) {
            throw new ReflectionException(e);
        } catch (InvocationTargetException e) {
            throw new ReflectionException(e);
        } catch (NoSuchMethodException e) {
            throw new ReflectionException(e);
        } catch (InstantiationException e) {
            throw new ReflectionException(e);
        }
    }
}
