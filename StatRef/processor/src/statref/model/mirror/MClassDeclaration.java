package statref.model.mirror;

import statref.model.*;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeVariable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created on 27/01/18.
 *
 * @author ptasha
 */
public class MClassDeclaration extends MClassRef implements SClassDeclaration {

    public MClassDeclaration(DeclaredType mirror) {
        super(mirror);
    }

    @Override
    public List<SClassMemeber> getMembers() {
        ArrayList<SClassMemeber> methods = new ArrayList<>();
        for (Element element : type.getEnclosedElements()) {
            if (element.getKind() == ElementKind.METHOD) {
                methods.add(new MMethodDeclaration((ExecutableElement) element));
            } else if (element.getKind() == ElementKind.FIELD) {
                methods.add(new MFieldDeclaration(element));
            }
        }
        return methods;
    }

    @Override
    public SClass usage() {
        return new MClass(getTypeMirror());
    }

    @Override
    public SClass usage(List<SType> generics) {
        return new MClass(getTypeMirror(), generics);
    }

    @Override
    public SClass getExtends() {
        return new MClass((DeclaredType) type.getSuperclass());
    }

    @Override
    public List<SGenericDeclaration> getGenerics() {
        return type.getTypeParameters().stream().map(
                element -> new MGenericDeclaration((TypeVariable) element.asType())
        ).collect(Collectors.toList());
    }

    @Override
    public Collection<Modifier> getModifiers() {
        return type.getModifiers();
    }
}
