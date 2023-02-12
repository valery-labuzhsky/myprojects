package streamline.plugin;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashSet;

public class StackPrinter {
    private final static HashSet<String> stacks = new HashSet<>();

    public static void printStack() {
        ByteArrayOutputStream array = new ByteArrayOutputStream();
        new Exception().printStackTrace(new PrintStream(array));
        String stack = array.toString();
        if (stacks.add(stack)) {
            System.out.println(stack);
        }
    }
}
