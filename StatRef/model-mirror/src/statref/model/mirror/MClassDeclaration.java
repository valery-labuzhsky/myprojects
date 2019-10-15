package statref.model.mirror;

import statref.model.*;
import statref.model.classes.SClassDeclaration;
import statref.model.classes.SPackage;
import statref.model.members.SClassMemeber;
import statref.model.types.SClass;
import statref.model.types.SType;

import javax.lang.model.element.*;
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
public class MClassDeclaration extends MBase<DeclaredType> implements SClassDeclaration {

    private final TypeElement type;

    public MClassDeclaration(DeclaredType mirror) {
        super(mirror);
        type = (TypeElement) mirror.asElement();
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
        return (SClass) MBase.get(getTypeMirror());
    }

    @Override
    public SClass usage(List<SType> generics) {
        return new SClass(getTypeMirror().toString(), generics);
    }

    @Override
    public SClass getExtends() {
        return (SClass) MBase.get(type.getSuperclass());
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

    @Override
    public SPackage getPackage() {
        return new MPackage(type.getEnclosingElement());
    }

    @Override
    public String getSimpleName() {
        return type.getSimpleName().toString();
    }
}
