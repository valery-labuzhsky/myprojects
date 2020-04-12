import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created on 26/03/2020.
 *
 * @author ptasha
 */
public class Html2Csv {
    public static void main(String[] args) throws IOException {
        String file = Files.readString(Path.of("cpus.html"));
        Matcher tm = Pattern.compile("<table.*?</table>", Pattern.DOTALL).matcher(file);
//        tm.find();
        tm.find();
        String table = file.substring(tm.start(), tm.end());

        Matcher hm = Pattern.compile("<thead.*?</thead>", Pattern.DOTALL).matcher(table);
        hm.find();
        String header = table.substring(hm.start(), hm.end());

        Matcher hr = Pattern.compile("<th .*?<span>(.*?)\n", Pattern.DOTALL).matcher(header);
        System.out.print("Name\t");
        while (hr.find()) {
            System.out.print(header.substring(hr.start(1), hr.end(1))+"\t");
        }
        System.out.println();

        Matcher bm = Pattern.compile("<tbody.*?</tbody>", Pattern.DOTALL).matcher(table);
        bm.find();
        String body = table.substring(bm.start(), bm.end());

        Matcher rm = Pattern.compile("<tr.*?</tr>", Pattern.DOTALL).matcher(body);
        while (rm.find()) {
            parseRow(body, rm);
        }

//        System.out.println(value);
        // table
        // thead
        // tbody
    }

    private static void parseRow(String body, Matcher rm) {
        String row = body.substring(rm.start(), rm.end());

        Matcher dm = Pattern.compile("<td.*?</td>", Pattern.DOTALL).matcher(row);
        dm.find();
        dm.find();
        String value = row.substring(dm.start(), dm.end());

        Matcher namem = Pattern.compile("</i>\\s+(.*?)<a.*?>(.*?)</a>", Pattern.DOTALL).matcher(value);
        namem.find();
        System.out.print(value.substring(namem.start(1), namem.end(1)));
        System.out.print(value.substring(namem.start(2), namem.end(2))+"\t");

        withArrows(row, dm);
        simple(row, dm);
        range(row, dm);
        simple(row, dm);
        simple(row, dm);
        simple(row, dm);
        simple(row, dm);
        price(row, dm);
        System.out.println();
    }

    private static void price(String row, Matcher dm) {
        dm.find();
        String value = row.substring(dm.start(), dm.end());
        Matcher arrm = Pattern.compile("руб (.*?)<").matcher(value);
        arrm.find();
        String arr = value.substring(arrm.start(1), arrm.end(1));
        System.out.print(arr+"\t");
    }

    private static void withArrows(String row, Matcher dm) {
        dm.find();
        String value = row.substring(dm.start(), dm.end());
        Matcher arrm = Pattern.compile("▲</div>(.+?)<div").matcher(value);
        arrm.find();
        String arr = value.substring(arrm.start(1), arrm.end(1));
        System.out.print(arr+"\t");
    }

    private static void simple(String row, Matcher dm) {
        dm.find();
        String value = row.substring(dm.start(), dm.end());
        Matcher arrm = Pattern.compile("<td><div.*?>(.*?)</div>").matcher(value);
        if (!arrm.find()) {
            System.out.println(value);
        }
        String arr = value.substring(arrm.start(1), arrm.end(1));
        System.out.print(arr+"\t");
    }

    private static void range(String row, Matcher dm) {
        dm.find();
        String value = row.substring(dm.start(), dm.end());
        Matcher arrm = Pattern.compile("<div.*?<div.*?>(.+?)<div").matcher(value);
        arrm.find();
        String arr = value.substring(arrm.start(1), arrm.end(1));
        System.out.print(arr+"\t");
    }
}
