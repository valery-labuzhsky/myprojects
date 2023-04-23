package statref.model.idea;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiTypes;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.Nullable;
import statref.model.types.SClass;
import statref.model.types.SPrimitive;
import statref.model.types.SType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ITypes {
    private static final Logger log = Logger.getInstance(ITypes.class);

    private static final FunctionRegistry<PsiType, SType> psitypes = new FunctionRegistry<>() {
        {
            register(PsiPrimitiveType.class, ITypes::convert);
            register(PsiClassType.class, ITypes::convert);
            // TODO generate it!
        }
    };

    private static final Map<PsiPrimitiveType, Class> psiToJava = new HashMap<>() {
        {
            put(PsiTypes.voidType(), void.class);
            put(PsiTypes.booleanType(), boolean.class);
            put(PsiTypes.charType(), char.class);
            put(PsiTypes.byteType(), byte.class);
            put(PsiTypes.shortType(), short.class);
            put(PsiTypes.intType(), int.class);
            put(PsiTypes.longType(), long.class);
            put(PsiTypes.floatType(), float.class);
            put(PsiTypes.doubleType(), double.class);
        }
    };
    private static final Map<Class, PsiPrimitiveType> javaToPsi = new HashMap<>() {
        {
            for (Entry<PsiPrimitiveType, Class> entry : psiToJava.entrySet()) {
                put(entry.getValue(), entry.getKey());
            }
        }
    };

    public static SPrimitive convert(PsiPrimitiveType psiType) {
        return new SPrimitive(psiToJava.get(psiType));
    }

    public static SClass convert(PsiClassType psiType) {
        // TODO wrapper
        return new SClass(psiType.getName(), Arrays.stream(psiType.getParameters()).map(psi -> ITypes.getType(psi)).collect(Collectors.toList()));
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
