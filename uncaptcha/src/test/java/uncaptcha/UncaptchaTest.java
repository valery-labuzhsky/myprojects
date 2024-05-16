package uncaptcha;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.LinkedList;
import java.util.ListIterator;

public class UncaptchaTest {
    @Test
    public void test() throws IOException {
        check("900437");
        check("072772");
        check("642944");
        check("645614");
        check("371582");
        check("104746");
        check("146254");
        check("166850");
        check("882116");
        check("907850");
        check("583747");
        check("267722");
        check("250787");
        check("341373");
        check("977334");
        check("149881");
        check("283852");
//        check("483821");
        check("564549");
        check("849104");
        check("386917");
        check("810864");
//        check("458958");
        check("691551");
    }

    @Test
    public void iterator() {
        LinkedList<String> list = new LinkedList<>();
        list.add("2");
        ListIterator<String> iterator = list.listIterator();
        iterator.next();
        iterator.previous();
        iterator.add("1");
        iterator.next();
        System.out.println(list);
        System.out.println(iterator.hasNext());
        System.out.println(iterator.next());
    }

    private static void check(String number) throws IOException {
        Assertions.assertEquals(number, Uncaptcha.detect("images/" + number + ".png"));
    }
}
