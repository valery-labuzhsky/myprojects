package statref.model.mirror;

import statref.model.SClass;
import statref.model.SVariable;
import statref.model.SMethod;

import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created on 27/01/18.
 *
 * @author ptasha
 */
public class MClass extends MType<DeclaredType> implements SClass {
    private final TypeElement type;

    public MClass(DeclaredType mirror) {
        super(mirror);
        this.type = (TypeElement) mirror.asElement();
    }

    @Override
    public MPackage getPackage() {
        return new MPackage(type.getEnclosingElement());
    }

    @Override
    public String getSimpleName() {
        return type.getSimpleName().toString();
    }

    @Override
    public Collection<SMethod> getMethods() {
        ArrayList<SMethod> methods = new ArrayList<>();
        for (Element element : type.getEnclosedElements()) {
            if (element.getKind() == ElementKind.METHOD) {
                methods.add(new MMethod((ExecutableElement) element));
            }
        }
        return methods;
    }

    @Override
    public List<SVariable> getFields() {
        ArrayList<SVariable> fields = new ArrayList<>();
        for (Element element : type.getEnclosedElements()) {
            if (element.getKind() == ElementKind.FIELD) {
                fields.add(new MVariable((VariableElement) element));
            }
        }
        return fields;
    }

    @Override
    public Collection<Modifier> getModifiers() {
        return type.getModifiers();
    }
}
