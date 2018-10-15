package statref.model.mirror;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;

public abstract class MClassRef extends MBase<DeclaredType> {
    protected final TypeElement type;

    public MClassRef(DeclaredType mirror) {
        super(mirror);
        this.type = (TypeElement) mirror.asElement();
    }

    public MPackage getPackage() {
        return new MPackage(type.getEnclosingElement());
    }

    public String getSimpleName() {
        return type.getSimpleName().toString();
    }
}
