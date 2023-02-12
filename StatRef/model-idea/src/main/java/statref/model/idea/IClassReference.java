package statref.model.idea;

import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import statref.model.builder.BBase;
import statref.model.classes.SClassReference;
import statref.model.types.SClass;
import statref.model.types.SType;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created on 25.07.2020.
 *
 * @author unicorn
 */
// TODO It should implement SClass when it becomes an interface
public class IClassReference extends IElement implements SClassReference {
    public IClassReference(PsiJavaCodeReferenceElement element) {
        super(element);
    }

    @Override
    public PsiJavaCodeReferenceElement getElement() {
        return (PsiJavaCodeReferenceElement) super.getElement();
    }

    @Override
    public boolean isDiamond() {
        PsiReferenceParameterList parameters = getElement().getParameterList();
        if (parameters == null) return false;
        PsiTypeElement[] elements = parameters.getTypeParameterElements();
        return elements.length == 1 && elements[0].getType() instanceof PsiDiamondType;
    }

    @Override
    public List<SType> resolveParameters() {
        PsiReferenceParameterList list = getElement().getParameterList();
        return Stream.of(list.getTypeArguments()).map(psi -> ITypes.getType(psi)).collect(Collectors.toList());
    }

    @Override
    @NotNull
    public List<SType> getParameters() {
        PsiReferenceParameterList list = getElement().getParameterList();
        if (list == null || isDiamond()) {
            return Collections.emptyList();
        }
        return Stream.of(list.getTypeParameterElements()).map(psi -> ITypes.getType(psi.getType())).collect(Collectors.toList());
    }

    @Override
    public void setParameters(List<SType> parameters) {
        // TODO should I make SClass - builder now?
        //  let's do it next time?

        // TODO start getting rid of writers!!!
        String newText = BBase.write(new SClass(getElement().getReferenceName(), parameters));

        // TODO should I involve any factory here?
        PsiJavaCodeReferenceElement reference = JavaPsiFacade.getElementFactory(getProject()).createReferenceFromText(newText, null);

        getElement().getParameterList().replace(reference.getParameterList());

    }

    @Override
    public String getSimpleName() {
        return getElement().getReferenceName();
    }
}
