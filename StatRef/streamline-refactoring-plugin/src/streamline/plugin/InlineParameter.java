package streamline.plugin;

import org.jetbrains.annotations.NotNull;
import statref.model.builder.BMethod;
import statref.model.builder.BMethodDeclaration;
import statref.model.expression.SExpression;
import statref.model.idea.*;
import streamline.plugin.nodes.NodesRegistry;
import streamline.plugin.refactoring.compound.CompoundRefactoring;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class InlineParameter extends CompoundRefactoring {
    @org.jetbrains.annotations.NotNull
    private final IParameter parameter;

    public InlineParameter(NodesRegistry registry, IParameter parameter) {
        super(registry.getRefactorings());
        this.parameter = parameter;

        IMethodDeclaration method = parameter.getParent();
        ArrayList<IMethod> calls = method.getCalls();
        Map<CodeFragment, List<IMethod>> expressions = new HashMap<>();
        for (IMethod call : calls) {
            IExpression expression = call.getExpression(parameter);
            expressions.computeIfAbsent(expression.fragment(), (e) -> new ArrayList<>()).add(call);
        }

        FragmentPlace parameterPlace = method.getPlace(parameter);

        for (Map.Entry<CodeFragment, List<IMethod>> entry : expressions.entrySet()) {
            CodeFragment fragment = entry.getKey();
            // TODO how can I use CodeFragment to improve this code?
            // TODO fragment will be bigger it will contain a method call
            // TODO add method call to a fragment
            // TODO let's do 2 independent fragments first

            // TODO replace all "method" mentions with callFragment
            BMethodDeclaration delegate = new BMethodDeclaration(method.getName()) {{
                // TODO what fragment? I have 2 of them!
                // TODO I can stack a place so will always know the fragment
                BiConsumer<CodeFragment, FragmentPlace> input = (f, p) -> f.set(p, parameter(f.getType(p), f.getName(p)));
                CodeFragment callFragment = entry.getValue().get(0).fragment();

                // TODO here I need a way to replace positions in code fragment
                // TODO how can I do it?
                // TODO I need modifiable fragment

                callFragment.forEach((f, p) -> {
                    if (p.equals(parameterPlace)) {
                        f.forEach(input);
                        f.set(p, f.get());
                    } else {
                        input.accept(f, p);
                    }
                });

                SExpression target = callFragment.get();

                if (target.getType().isPrimitive(void.class)) {
                    code((BMethod) target);
                } else {
                    return_(target);
                }
            }};


            // TODO check for signature conflicts
            // TODO yet another challenge - resolve the names
/*
            BMethodDeclaration delegate = new BMethodDeclaration(method.getName()) {{
                BMethod target = new BMethod(null, method.getName());

                for (IParameter param : method.getParameters()) {
                    if (param.equals(parameter)) {
                        target.parameter(fragment.getExpression(e -> parameter(e.getType(), fragment.getName(e))));
                    } else {
                        target.parameter(new BVariable(param.getName())); // TODO usage
                    }
                }

                if (method.isVoid()) {
                    code(target);
                } else {
                    return_(target);
                }
            }};
*/

            add(new CreateMethod(this.registry, method, delegate));

            for (IMethod call : entry.getValue()) {
                BMethod replacement = new BMethod(call.getQualifier(), delegate.getName());
                CodeFragment callFragment = call.fragment();
                BiConsumer<CodeFragment, FragmentPlace> param = (f, p) -> replacement.parameter(f.get(p));
                // TODO I don't need any expression here, it's just a way to walk through a tree
                // TODO need universal way of doing it
                // TODO how can I combine them?
                // TODO I olny need a visitor
                callFragment.forEach((f, p) -> {
                    if (p.equals(parameterPlace)) {
                        fragment.forEach(param);
                    } else {
                        param.accept(f, p);
                    }
                });

//                BMethod replacement = new BMethod(call.getQualifier(), delegate.getName());
//                List<IExpression> params = call.getParams();
//                for (int i = 0; i < params.size(); i++) {
//                    IExpression param = params.get(i);
//                    if (i == parameter.getIndex()) {
//                        for (SExpression input : param.fragment().getInputs()) {
//                            replacement.parameter(input);
//                        }
//                    } else {
//                        replacement.parameter(param);
//                    }
//                }

                add(new ReplaceElement(this.getRegistry(), call, replacement));
            }

            // TODO replace methods manipulations with fragments
        }
    }

    @NotNull
    public IParameter getParameter() {
        return parameter;
    }

}
