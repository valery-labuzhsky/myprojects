package statref.model.idea;

import com.intellij.lang.jvm.types.JvmPrimitiveTypeKind;
import com.intellij.psi.PsiPrimitiveType;
import statref.model.SClass;

public class IPrimitive extends IType<PsiPrimitiveType> implements SClass {
    public IPrimitive(PsiPrimitiveType psiType) {
        super(psiType);
    }

    @Override
    public String getName() {
        return getPsiType().getName();
    }

    @Override
    public Class<?> getJavaClass() {
        JvmPrimitiveTypeKind kind = getPsiType().getKind();
        if (kind==JvmPrimitiveTypeKind.VOID) {
            return void.class;
        }
        // TODO continue
        return null;
    }
}
