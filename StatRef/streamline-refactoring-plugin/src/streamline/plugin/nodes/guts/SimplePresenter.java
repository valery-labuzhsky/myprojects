package streamline.plugin.nodes.guts;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.psi.PsiElement;
import com.intellij.ui.SimpleTextAttributes;
import statref.model.idea.IElement;

import java.util.ArrayList;
import java.util.Objects;

public class SimplePresenter implements Presenter {
    private final ArrayList<Block> blocks = new ArrayList<>();
    private SimpleTextAttributes attributes = SimpleTextAttributes.REGULAR_ATTRIBUTES;
    ;
    private ElementBlock lastElementBlock;

    public SimplePresenter inline(PsiElement element) {
        lastElementBlock.child(new ElementBlock(element, attributes));
        regular();
        return this;
    }

    public SimplePresenter add(IElement element) {
        return add(element.getElement());
    }

    public SimplePresenter add(PsiElement element) {
        if (element != null) {
            lastElementBlock = new ElementBlock(element, attributes);
            blocks.add(lastElementBlock);
        }
        regular();
        return this;
    }

    public Presenter add(Object object) {
        blocks.add(new ObjectBlock(object, attributes));
        regular();
        return this;
    }

    public SimplePresenter add(String text) {
        blocks.add(new TextBlock(text, attributes));
        regular();
        return this;
    }

    public SimplePresenter regular() {
        attributes = SimpleTextAttributes.REGULAR_ATTRIBUTES;
        return this;
    }

    public SimplePresenter bold() {
        attributes = SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES;
        return this;
    }

    public SimplePresenter italic() {
        attributes = SimpleTextAttributes.REGULAR_ITALIC_ATTRIBUTES;
        return this;
    }

    private void addText(PresentationData presentation, String text, SimpleTextAttributes attributes) {
        presentation.addText(text, update(attributes));
    }

    @Override
    public void update(PresentationData presentation) {
        presentation.clearText();
        for (Block block : blocks) {
            block.present(presentation);
        }
    }

    protected SimpleTextAttributes update(SimpleTextAttributes attributes) {
        return attributes;
    }

    private class ElementBlock extends SimplePresenter.Block {
        private final PsiElement element;
        private final ArrayList<SimplePresenter.ElementBlock> children = new ArrayList<>();

        public ElementBlock(PsiElement element, SimpleTextAttributes attributes) {
            super(attributes);
            this.element = element;
        }

        public boolean contains(SimplePresenter.ElementBlock child) {
            return Objects.equals(element.getContainingFile(), child.element.getContainingFile()) && element.getTextRange().contains(child.element.getTextRange());
        }

        public void child(SimplePresenter.ElementBlock child) {
            for (SimplePresenter.ElementBlock block : children) {
                if (block.contains(child)) {
                    block.child(child);
                    return;
                }
            }
            this.children.add(child);
        }

        @Override
        public void present(PresentationData presentation) {
            String text = element.getText();
            int parentOffset = element.getTextRange().getStartOffset();

            for (SimplePresenter.ElementBlock child : children) {
                if (child.element.isValid()) {
                    int childOffset = child.element.getTextRange().getStartOffset() - parentOffset;
                    addText(presentation, text.substring(0, childOffset));

                    child.present(presentation);
                    childOffset += child.element.getTextLength();

                    text = text.substring(childOffset);
                    parentOffset += childOffset;
                }
            }

            addText(presentation, text);
        }
    }

    private class TextBlock extends SimplePresenter.Block {
        private final String text;

        public TextBlock(String text, SimpleTextAttributes attributes) {
            super(attributes);
            this.text = text;
        }


        @Override
        public void present(PresentationData presentation) {
            addText(presentation, this.text);
        }

    }

    public abstract class Block {
        protected final SimpleTextAttributes attributes;

        public Block(SimpleTextAttributes attributes) {
            this.attributes = attributes;
        }

        public abstract void present(PresentationData presentation);

        public void addText(PresentationData presentation, String text) {
            SimplePresenter.this.addText(presentation, text, this.attributes);
        }
    }

    private class ObjectBlock extends Block {
        private final Object object;

        public ObjectBlock(Object object, SimpleTextAttributes attributes) {
            super(attributes);
            this.object = object;
        }

        @Override
        public void present(PresentationData presentation) {
            addText(presentation, object.toString());
        }
    }
}
