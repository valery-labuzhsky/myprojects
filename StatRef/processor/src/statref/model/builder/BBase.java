package statref.model.builder;

import statref.model.SClass;
import statref.model.SInstruction;

/**
 * Created on 04/02/18.
 *
 * @author ptasha
 */
public class BBase {
    public static BFieldUsage field(SClass clazz, String fieldName) {
        return new BFieldUsage(clazz, fieldName);
    }

    public static BReturn.Builder returnIt() {
        return new BReturn.Builder();
    }

    public static BBlock block(SInstruction instruction) {
        return new BBlock(instruction);
    }
}
