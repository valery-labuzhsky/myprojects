package statref.model.idea;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.PsiType;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.Nullable;
import statref.model.types.SClass;
import statref.model.types.SPrimitive;
import statref.model.types.SType;

import java.util.HashMap;
import java.util.Map;

public class ITypes {
    private static final Logger log = Logger.getInstance(ITypes.class);

    private static final FunctionRegistry<PsiType, SType> psitypes = new FunctionRegistry<PsiType, SType>() {
        {
            register(PsiPrimitiveType.class, ITypes::convert);
            // TODO generate it!
        }
    };

    private static final Map<PsiPrimitiveType, Class> psiToJava = new HashMap<PsiPrimitiveType, Class>() {
        {
            put(PsiPrimitiveType.VOID, void.class);
            put(PsiPrimitiveType.BOOLEAN, boolean.class);
            put(PsiPrimitiveType.CHAR, char.class);
            put(PsiPrimitiveType.BYTE, byte.class);
            put(PsiPrimitiveType.SHORT, short.class);
            put(PsiPrimitiveType.INT, int.class);
            put(PsiPrimitiveType.LONG, long.class);
            put(PsiPrimitiveType.FLOAT, float.class);
            put(PsiPrimitiveType.DOUBLE, double.class);
        }
    };
    private static final Map<Class, PsiPrimitiveType> javaToPsi = new HashMap<Class, PsiPrimitiveType>() {
        {
            for (Entry<PsiPrimitiveType, Class> entry : psiToJava.entrySet()) {
                put(entry.getValue(), entry.getKey());
            }
        }
    };

    public static SPrimitive convert(PsiPrimitiveType psiType) {
        return new SPrimitive(psiToJava.get(psiType));
    }

    @Nullable
    public static PsiType toPsiType(Project project, SType type) {
        PsiType psiType = null;
        if (type instanceof SPrimitive) {
            psiType = javaToPsi.get(((SPrimitive) type).getJavaClass());
        } else if (type instanceof SClass) {
            psiType = PsiType.getTypeByName(((SClass) type).getName(), project, GlobalSearchScope.allScope(project));
        }
        if (psiType == null) {
            log.error("Failed to find PsiType for " + type + " of " + type.getClass().getName());
        }
        return psiType;
    }

    public static SType getType(PsiType type) {
        return psitypes.convert(type);
    }
}
