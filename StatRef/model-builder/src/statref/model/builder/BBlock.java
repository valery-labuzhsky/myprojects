package statref.model.builder;

import statref.model.SBlock;
import statref.model.SStatement;

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
