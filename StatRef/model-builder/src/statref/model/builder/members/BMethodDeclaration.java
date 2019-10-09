package statref.model.builder.members;

import statref.model.members.SMethodDeclaration;
import statref.model.members.SParameter;
import statref.model.statements.SStatement;
import statref.model.types.SType;
import statref.model.builder.*;
import statref.model.builder.expressions.BMethod;
import statref.model.builder.expressions.BVariable;
import statref.model.builder.statements.BMethodStatement;
import statref.model.builder.statements.BReturn;
import statref.model.expressions.SExpression;
import statref.model.expressions.SMethod;

import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BMethodDeclaration implements SMethodDeclaration, BModifiers<BMethodDeclaration> {
    private String name;
    private final List<SParameter> parameters = new ArrayList<>();
    private SType returnType = BBase.ofClass(void.class);

    private final ArrayList<SStatement> instructions = new ArrayList<>();
    private final ArrayList<Modifier> modifiers = new ArrayList<>();

    public BMethodDeclaration(String name) {
        this.name = name;
        describe();
    }

    @Override
    public List<SParameter> getParameters() {
        return parameters;
    }

    public BMethodDeclaration param(SParameter param) {
        parameters.add(param);
        return this;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public SType getReturnType() {
        return returnType;
    }

    @Override
    public List<SStatement> getInstructions() {
        return instructions;
    }

    @Override
    public Collection<Modifier> getModifiers() {
        return modifiers;
    }

    public BMethodDeclaration returnType(SType returnType) {
        if (returnType != null) {
            this.returnType = returnType;
        }
        return this;
    }

    public BMethodDeclaration code(SStatement instruction) {
        instructions.add(instruction);
        return this;
    }

    public void describe() {
    }

    protected void return_(SExpression expression) {
        SType type = expression.getType();
        if (type.isClass(void.class)) {
            this.code(createStatement(expression));
        } else {
            returnType(type);
            this.code(new BReturn(expression));
        }
    }

    // TODO move me somewhere?
    private SStatement createStatement(SExpression expression) {
        if (expression instanceof SMethod) {
            return createMethodStatement((SMethod) expression);
        }
        throw new IllegalArgumentException("Cannot treat "+expression+" of "+expression.getClass().getName()+" as a statement");
    }

    private BMethodStatement createMethodStatement(SMethod method) {
        return new BMethodStatement(method);
    }

    protected BVariable parameter(SType type, String name) {
        BParameter param = new BParameter(type, name);
        this.param(param);
        return param.usage();
    }

    public BMethodDeclaration code(BMethod method) {
        return code(createMethodStatement(method));
    }

}
