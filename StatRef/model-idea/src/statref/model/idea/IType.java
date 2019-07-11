package statref.model.idea;

import com.intellij.psi.PsiType;
import statref.model.SType;

public abstract class IType<T extends PsiType> implements SType {
    private final T psiType;

    public IType(T psiType) {
        this.psiType = psiType;
    }

    public T getPsiType() {
        return psiType;
    }

    @Override
    public SType getGenericType() {
        return this;
    }
}
