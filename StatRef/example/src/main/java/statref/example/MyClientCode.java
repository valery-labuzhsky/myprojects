package statref.example;

import statref.ann.StatRefs;
import statref.api.Field;

/**
 * Created on 02/01/18.
 *
 * @author ptasha
 */
@StatRefs(MyPlainClass.class)
public class MyClientCode {
    public static void main(String[] args) {
        MyPlainClass object = new MyPlainClass();
        Field<MyPlainClass, Integer> field = MyPlainClassStatRef.SR.MyPrivateField;
        field.set(object, 1);
        System.out.println(field.get(object));
    }
}
