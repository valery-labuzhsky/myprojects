package statref.example;

import lombok.Data;

/**
 * Created on 01/01/18.
 *
 * @author ptasha
 */
@Data
public class MyPlainClass<C> {
    private int myPrivateField;
    protected int myProtectedField;
    public int myPublicField;
    private C genericField;
}
