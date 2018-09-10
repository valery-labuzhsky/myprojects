package statref.writer;

import java.io.IOException;

/**
 * Created on 28/01/18.
 *
 * @author ptasha
 */
public abstract class WBase<S> {
    public WBase() {
    }

    public static WClass classWriter() {
        return new WClass();
    }

    public static WModifiers modifiersWriter() {
        return new WModifiers();
    }

    public static WVariable variableWriter() {
        return new WVariable();
    }

    public static WMethod methodWriter() {
        return new WMethod();
    }

    public static WTypeUsage typeUsageWriter() {
        return new WTypeUsage();
    }

    public static WExpression expressionWriter() {
        return new WExpression();
    }

    public static WInstruction instructionWriter() {
        return new WInstruction();
    }

    public static WFieldUsage fieldUsage() {
        return new WFieldUsage();
    }

    public static WReturn returnWriter() {
        return new WReturn();
    }

    public static WBlock blockWriter() {
        return new WBlock();
    }

    public abstract void write(CodeWriter writer, S element) throws IOException;
}
