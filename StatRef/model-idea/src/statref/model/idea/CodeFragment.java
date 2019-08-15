package statref.model.idea;

import statref.model.builder.BMethod;
import statref.model.builder.BParameter;
import statref.model.expression.SExpression;

import java.util.List;

// TODO move me to another package, module?
public abstract class CodeFragment {
    public SExpression getExpression() {
        return null; // TODO remove me I'm wrong
    }

    public CodeFragment input(IExpression expression, CodeFragment fragment) {
        return null; // TODO what will it do?
        // TODO I need return fragment with mixed parameters of two of them and containing both
        // TODO it's kind of easy with instances, I'll just have expression for template and expressions for parameters to replace with
        // TODO I'll also need expressions for internal code fragments
        // TODO I may also have several expressions for template
        // TODO let it be that way for a time being?
        // TODO let's have more methods to deal with
    }

    public List<BParameter> inputs() {
        return null;
    }

    public BParameter thisInput() {
        return null;
    }

    public BMethod createBody() {
        return null;
    }
}
