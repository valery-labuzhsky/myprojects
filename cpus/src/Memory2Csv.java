import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created on 26.04.2021.
 *
 * @author unicorn
 */
public class Memory2Csv {
    public static final Pattern PRICE = Pattern.compile("([0-9 ]+) руб.");
    public static final Pattern DESCRIPTION = Pattern.compile("([0-9]+) Гб, 2 модуля DDR-4, ([0-9]+) Мб/с.*");

    public static void main(String[] args) {
        StringBuilder output = new StringBuilder();

        output.append("Number\tName\tVolume\tSpeed\tPrice\n");

        try (BufferedReader reader = new BufferedReader(new FileReader("memory.csv"))) {
            while (reader.readLine() != null) {
                String number = reader.readLine();
                if (number.isBlank()) number = reader.readLine();
                String name = reader.readLine();
                String description = reader.readLine();
                String price = reader.readLine();

                output.append(number);
                if (!number.endsWith("\t")) output.append('\t');
                output.append(name).append("\t");

                Matcher d = DESCRIPTION.matcher(description);
                if (d.matches()) {
                    for (int i = 1; i <= d.groupCount(); i++) {
                        String g = d.group(i);
//                        if (i==5 || i==6) g = g.replaceAll("\\.", ",");
                        output.append(g).append("\t");
                    }
                } else {
                    System.out.println(description);
                    return;
                }

                Matcher p = PRICE.matcher(price);
                if (p.matches()) {
                    output.append(p.group(1).replaceAll(" ", ""));
                } else {
                    System.out.println(price);
                    return;
                }

                output.append('\n');
            }

            System.out.println(output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
