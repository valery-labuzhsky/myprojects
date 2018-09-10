package statref.example;

import statref.api.Field;

import java.lang.reflect.Type;

/**
 * Created on 02/01/18.
 *
 * @author ptasha
 */
public class MyPlainClassStatRefSource<C extends Number> {
    private static final MyPlainClassStatRefSource SR = new MyPlainClassStatRefSource();

    public static <C extends Number> MyPlainClassStatRefSource<C> sr() {
        return SR;
    }

    protected Field<MyPlainClass, Integer> myProtectedField = new Field<MyPlainClass, Integer>() {
        @Override
        public Class<MyPlainClass> getObjectType() { return MyPlainClass.class; }

        @Override
        public Type getValueType() { return int.class; }

        @Override
        public Integer get(MyPlainClass object) { return object.myProtectedField; }

        @Override
        public void set(MyPlainClass object, Integer value) { object.setMyPrivateField(value); }
    };

    public Field<MyPlainClass, ?>[] fields() {
        return new Field[]{myProtectedField};
    }

    private Field<MyPlainClass<C>, C> GenericField = new Field<MyPlainClass<C>, C>() {
        public Class<MyPlainClass<C>> getObjectType() { return (Class)MyPlainClass.class; }
        public Type getValueType() { return MyPlainClass.class.getTypeParameters()[0]; }
        public C get(MyPlainClass<C> object) { return object.getGenericField(); }
        public void set(MyPlainClass<C> object, C value) { object.setGenericField(value); }
    };
}
