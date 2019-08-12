package streamline.plugin;

import org.jetbrains.annotations.NotNull;
import statref.model.builder.BMethod;
import statref.model.builder.BMethodDeclaration;
import statref.model.builder.BVariable;
import statref.model.idea.*;
import streamline.plugin.nodes.NodesRegistry;
import streamline.plugin.refactoring.compound.CompoundRefactoring;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InlineParameter extends CompoundRefactoring {
    @org.jetbrains.annotations.NotNull
    private final IParameter parameter;

    public InlineParameter(NodesRegistry registry, IParameter parameter) {
        super(registry.getRefactorings());
        this.parameter = parameter;
        IMethodDeclaration method = parameter.getParent();
        ArrayList<IMethodCall> calls = method.getCalls();
        Map<Object, List<IExpression>> expressions = new HashMap<>();
        for (IMethodCall call : calls) {
            IExpression expression = call.getExpression(parameter);
            expressions.computeIfAbsent(expression.signature(), (k) -> new ArrayList<>()).add(expression);
        }

        int i = 0;
        for (List<IExpression> value : expressions.values()) {
            BMethodDeclaration prototype = new BMethodDeclaration(method.getName() + i++) {
                @Override
                public void describe() {
                    BMethod delegate = new BMethod(null, method.getName());
                    for (IParameter param : method.getParameters()) {
                        if (param.equals(parameter)) {
                            // TODO it won't work with complex usages
                            // TODO we need using signature
                            delegate.getParams().add(value.get(0));
                        } else {
                            delegate.getParams().add(new BVariable(param));
                        }
                    }
                    if (method.isVoid()) {
                        code(delegate);
                    } else {
                        return_(delegate);
                    }
                }
            }.returnType(method.getReturnType());

            IMethodDeclaration newMethod = IFactory.convertMethodDeclaration(prototype, method.getProject());

            System.out.println(newMethod.getText());

            CreateMethod createMethod = new CreateMethod(this.registry, method, newMethod);
            add(createMethod);
        }
    }

    @NotNull
    public IParameter getParameter() {
        return parameter;
    }
}
