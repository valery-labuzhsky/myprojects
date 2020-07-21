package streamline.plugin.refactoring.guts.flow;

/**
 * Created on 21.07.2020.
 *
 * @author unicorn
 */
public class SequenceBlock extends ComplexBlock {
    @Override
    public boolean harvest(Visitor visitor, Cycler cycler) {
        boolean override = false;
        for (Block block : blocks) {
            override |= block.harvest(visitor, cycler);
        }
        return override;
    }
}
