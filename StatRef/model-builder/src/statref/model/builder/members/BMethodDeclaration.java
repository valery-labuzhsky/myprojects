package statref.model.builder.members;

import statref.model.builder.BBase;
import statref.model.builder.BModifiers;
import statref.model.builder.expressions.BLocalVariable;
import statref.model.builder.expressions.BMethod;
import statref.model.builder.statements.BMethodStatement;
import statref.model.builder.statements.BReturn;
import statref.model.expressions.SExpression;
import statref.model.expressions.SMethod;
import statref.model.members.SMethodDeclaration;
import statref.model.members.SParameter;
import statref.model.statements.SStatement;
import statref.model.types.SType;

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
        SType type = returnType;
        if (type.isClass(void.class)) {
            type = expression.getType();
            returnType(type);
        }
        if (type.isClass(void.class)) {
            this.code(createStatement(expression));
        } else {
            this.code(new BReturn(expression));
        }
    }

    // TODO move me somewhere?
    private SStatement createStatement(SExpression expression) {
        return expression.toStatement();
//        // TODO I basically need to know whether it's convertable and a method to convert it
//        // TODO do I need expression-statement? it's not compilable most of the time
//        // TODO I would need a method of checking anyway, and there is no point in creating wrong code
//
//        if (expression instanceof SMethod) {
//            return createMethodStatement((SMethod) expression);
//        }
//        // TODO what to do with ExpressionFragment, how to convert it to statement?
//        // TODO it should be possible somehow
//        // TODO 1. I can call getBase
//        // TODO 2. ???
//        // TODO need some common place
//        // TODO I can create this method in SExpression and just implement it
//        throw new IllegalArgumentException("Cannot treat "+expression+" of "+expression.getClass().getName()+" as a statement");
    }

    private BMethodStatement createMethodStatement(SMethod method) {
        return new BMethodStatement(method);
    }

    protected BLocalVariable parameter(SType type, String name) {
        BParameter param = new BParameter(type, name);
        this.param(param);
        return param.usage();
    }

    public BMethodDeclaration code(BMethod method) {
        return code(createMethodStatement(method));
    }

}
