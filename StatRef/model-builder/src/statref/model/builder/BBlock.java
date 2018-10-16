package statref.model.builder;

import statref.model.SBlock;
import statref.model.SInstruction;

import java.util.Arrays;
import java.util.List;

/**
 * Created on 04/02/18.
 *
 * @author ptasha
 */
public class BBlock implements SBlock {
    private final List<SInstruction> instructions;

    public BBlock(SInstruction... instructions) {
        this.instructions = Arrays.asList(instructions);
    }

    @Override
    public List<SInstruction> getInstructions() {
        return instructions;
    }
}
