package statref.model.types;

import java.util.HashMap;

public class SPrimitive extends SClass {
    private static final HashMap<Class, Class> wrappers = new HashMap<>();

    static {
        wrappers.put(int.class, Integer.class);
        wrappers.put(boolean.class, Boolean.class);
        wrappers.put(double.class, Double.class);
        wrappers.put(short.class, Short.class);
        wrappers.put(float.class, Float.class);
        wrappers.put(long.class, Long.class);
        wrappers.put(char.class, Character.class);
        wrappers.put(byte.class, Byte.class);
        wrappers.put(void.class, Void.class);
    }

    private final Class<?> primitive;

    public SPrimitive(Class<?> primitive) {
        super(primitive.getSimpleName());
        this.primitive = primitive;
    }

    @Override
    public Class<?> getJavaClass() {
        return primitive;
    }

    @Override
    public SType getGenericType() {
        return new SClass(getWrapperClass());
    }

    private Class getWrapperClass() {
        return wrappers.get(primitive);
    }

}
