package statref.model.idea;

import com.intellij.psi.PsiType;
import statref.model.SType;

public abstract class IType implements SType {
    private final PsiType psiType;

    public IType(PsiType psiType) {
        this.psiType = psiType;
    }

    public PsiType getPsiType() {
        return psiType;
    }

    @Override
    public SType getGenericType() {
        return this;
    }
}
