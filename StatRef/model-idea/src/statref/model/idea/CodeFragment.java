package statref.model.idea;

import com.intellij.openapi.util.text.StringUtil;
import statref.model.expression.SExpression;

import java.util.List;
import java.util.function.Function;

// TODO move me to another package, module?
public abstract class CodeFragment {
    // TODO a fragment is not necessary an expression
    public abstract SExpression getExpression(Function<SExpression, SExpression> function);

    public String getName(SExpression e) {
        return StringUtil.decapitalize(e.getType().toString());
    }

    public abstract List<SExpression> getInputs();
}
