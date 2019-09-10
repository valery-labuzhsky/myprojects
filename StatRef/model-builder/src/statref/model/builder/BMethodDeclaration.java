package statref.model.builder;

import statref.model.SMethodDeclaration;
import statref.model.SParameter;
import statref.model.SStatement;
import statref.model.SType;
import statref.model.expression.SExpression;

import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BMethodDeclaration implements SMethodDeclaration, BModifiers<BMethodDeclaration> {
    private final String name;
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
        this.returnType = returnType;
        return this;
    }

    public BMethodDeclaration code(SStatement instruction) {
        instructions.add(instruction);
        return this;
    }

    public void describe() {
    }

    protected void return_(SExpression expression) {
        returnType(expression.getType());
        this.code(new BReturn(expression));
    }

    protected BVariable parameter(SType type, String name) {
        BParameter param = new BParameter(type, name);
        this.param(param);
        return param.usage();
    }

    public BMethodDeclaration code(BMethod method) {
        return code(new BMethodInstruction(method));
    }

}
