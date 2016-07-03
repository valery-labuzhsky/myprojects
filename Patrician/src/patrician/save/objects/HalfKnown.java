package patrician.save.objects;

import java.util.List;

/**
 * Created on 04/04/15.
 *
 * @author ptasha
 */
public class HalfKnown extends Sequence<HalfKnown> {
    public HalfKnown(String template) {
        this(parseTemplate(template));
    }

    private static int[] parseTemplate(String template) {
        String[] bytes = template.split("[ |]+");
        int[] needle = new int[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i].equals("x")) {
                needle[i] = -1;
            } else {
                needle[i] = Integer.parseInt(bytes[i], 16);
            }
        }
        return needle;
    }

    public HalfKnown(int[] template) {
        super(template);
    }

    @Override
    public int size() {
        return sequence.length;
    }

    @Override
    public int read(int offset, List<Integer> data) {
        for (int i = 0; i < sequence.length; i++) {
            int b = sequence[i];
            int next = data.get(offset++);
            if (b==-1) {
                sequence[i] = next;
            } else if (b!=next) {
                sequence[i] = -2;
                throw new RuntimeException(String.format("Unexpected byte %h at %d: %s", next, offset + i, toString()));
            }
        }
        return size();
    }

    @Override
    public String toString() {
        String string = "";
        int written = 0;
        for (int b : sequence) {
            if (written>0) {
                string+=" ";
                if (written%4==0) {
                    string+="| ";
                }
            }
            if (b==-2) {
                string += "?";
            } else if (b==-1) {
                string += "x";
            } else {
                string += String.format("%2h", b);
            }
            written++;
        }
        return string + "\n";
    }
}
