package statref.model.builder.statements;

import statref.model.statements.SBlock;
import statref.model.statements.SStatement;

import java.util.Arrays;
import java.util.List;

/**
 * Created on 04/02/18.
 *
 * @author ptasha
 */
public class BBlock implements SBlock {
    private final List<SStatement> instructions;

    public BBlock(SStatement... instructions) {
        this.instructions = Arrays.asList(instructions);
    }

    @Override
    public List<SStatement> getInstructions() {
        return instructions;
    }
}
