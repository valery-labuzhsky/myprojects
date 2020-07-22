package streamline.plugin.refactoring.guts.flow;

/**
 * Created on 21.07.2020.
 *
 * @author unicorn
 */
public class SequenceFlow extends ComplexFlow {
    @Override
    public boolean harvest(Visitor visitor) {
        boolean override = false;
        for (ExecutionFlow flow : flows) {
            override |= flow.harvest(visitor);
        }
        return override;
    }
}
