package statref.model.idea;

import com.intellij.psi.PsiCodeBlock;
import statref.model.SElement;

public class IBlock extends IElement<PsiCodeBlock> implements SElement {
    public IBlock(PsiCodeBlock block) {
        super(block);
    }

}
