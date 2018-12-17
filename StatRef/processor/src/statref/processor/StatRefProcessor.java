package statref.processor;

import statref.ann.StatRefs;
import statref.model.mirror.MClassDeclaration;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Set;

/**
 * Created on 02/01/18.
 *
 * @author ptasha
 */
@SupportedAnnotationTypes({
        "statref.ann.StatRefs"
})
public class StatRefProcessor extends AbstractProcessor {

    private static StatRefProcessor SRP;

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        SRP = this;

        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(StatRefs.class);
        for (Element element : elements) {
            StatRefs annotation = element.getAnnotation(StatRefs.class);
            try {
                try {
                    Class<?>[] classes = annotation.value();
                    if (classes.length == 0) {
                        // TODO create for element
                    } else {
                        for (Class<?> clazz : classes) {
                            note(clazz.getName());
                            // TODO create for class
                        }
                    }
                } catch (MirroredTypesException e) {
                    for (TypeMirror mirror : e.getTypeMirrors()) {
                        generate(mirror);
                    }
                }
            } catch (RuntimeException e) {
                ByteArrayOutputStream stack = new ByteArrayOutputStream();
                e.printStackTrace(new PrintStream(stack));
                getMessager().printMessage(Diagnostic.Kind.ERROR, stack.toString(), element);
            }
        }

        return false;
    }

    private void generate(TypeMirror mirror) {
        Filer filer = processingEnv.getFiler();

        SRClass gc = new SRClass(new MClassDeclaration((DeclaredType) mirror));

        try {
            gc.generate(filer);
            note("Generated " + gc.getFullName());
        } catch (IOException e) {
            getMessager().printMessage(Diagnostic.Kind.ERROR, "Failed to generate " + gc.getFullName() + ": " + e.getMessage());
        }
    }

    public static void note(Element element) {
        note(element + " " + element.getKind());
    }

    public static void note(String message) {
        SRP.getMessager().printMessage(Diagnostic.Kind.NOTE, message);
    }

    private void note(Object msg) {
        if (msg == null) {
            note("null");
        } else {
            note("" + msg);
        }
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    private Messager getMessager() {
        return processingEnv.getMessager();
    }
}
