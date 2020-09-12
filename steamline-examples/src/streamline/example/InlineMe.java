package streamline.example;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Supplier;

public class InlineMe {
    public void simpleAfter() {
        ArrayList<String> enemies = new ArrayList<>();
        for (String enemy: enemies) {

        }

        int inline = 8;

        int target = inline;
    }

    public void setBefore() {
        int inline = 8;

        inline = 7;

        int target = inline;

        inline = 9;
    }


    public void setAfter() {
        int inline = 8;

        inline = 7;

        int target = 7;

        inline = 9;
    }

    public void ifBefore() {
        int inline = 8;

        if (1 == 1) {
            inline = 7;
        } else {
            inline = 8;
        }

        int target = inline;

        inline = 9;
    }

    public void ifAfter() {
        int inline = 8; // TODO inlining it causes inlining int target = inline; for some reason

        if (1 == 1)
            inline = 7;

        inline = 6;

        int target = inline;

        inline = 9;
    }

    public void cylce() {
        
        int inline = 8;

        while (inline > 0) {
            int target = inline; // TODO it will also remove this not minding of other values set, we must replace them with declaration
            inline = 9;

            int targetLocal = target; // TODO target get replaced with inline which then overriden, we must at least worn
            target = 10;
        }

        int afterTarget = inline;
    }

    public void escape() {
        int a = 10;
        int b = 20;
        int s = a + b;
        int m = s * 2;
    }

    public void test(boolean value) {

    }

    public void testUsage() {
        boolean c = true;
        test(c);
        test(true);
        test(false);
    }

    public static void main(String[] args) {
        String tst = "tst";
        System.out.println(tst);
    }
}
