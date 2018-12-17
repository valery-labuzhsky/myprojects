package statref.model.builder;

import statref.model.*;
import statref.model.expression.SExpression;

import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BMethodDeclaration implements SMethodDeclaration, BModifiers<BMethodDeclaration> {
    private final String name;
    private final List<SBaseVariableDeclaration> parameters = new ArrayList<>();
    private SType returnType = BBase.ofClass(void.class);

    private final ArrayList<SInstruction> instructions = new ArrayList<>();
    private final ArrayList<Modifier> modifiers = new ArrayList<>();

    public BMethodDeclaration(String name) {
        this.name = name;
        describe();
    }

    @Override
    public List<SBaseVariableDeclaration> getParameters() {
        return parameters;
    }

    public BMethodDeclaration param(SBaseVariableDeclaration param) {
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
    public List<SInstruction> getInstructions() {
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

    public BMethodDeclaration code(SInstruction instruction) {
        instructions.add(instruction);
        return this;
    }

    public void describe() {
    }

    protected void return_(SExpression expression) {
        this.code(new BReturn(expression));
    }

    protected BVariable parameter(SType type, String name) {
        BBaseVariableDeclaration param = BBase.declareVariable(type, name);
        this.param(param);
        return param.usage();
    }

    public BMethodDeclaration code(BMethod method) {
        return code(new BMethodInstruction(method));
    }
}
