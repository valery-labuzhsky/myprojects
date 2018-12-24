package statref.model.idea;

import com.intellij.psi.PsiCodeBlock;
import statref.model.SContext;
import statref.model.SElement;

public class IBlock extends IElement<PsiCodeBlock> implements SContext {
    public IBlock(PsiCodeBlock block) {
        super(block);
    }

    @Override
    public SContext getContext() {
        return super.getContext();
    }
}
