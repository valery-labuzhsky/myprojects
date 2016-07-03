package patrician.save.objects;

import java.util.List;

/**
 * Created on 28/03/15.
 *
 * @author ptasha
 */
public abstract class Sequence<T extends Sequence<T>> implements Item<T> {
    protected int[] sequence;

    public Sequence(int size) {
        this(new int[size]);
    }

    public Sequence(int[] sequence) {
        this.sequence = sequence;
    }

    public int[] getSequence() {
        return sequence;
    }

    @Override
    public int read(int offset, List<Integer> data) {
        for (int i=0; i<sequence.length; i++) {
            sequence[i]=data.get(offset+i);
        }
        return size();
    }

    @Override
    public int get(int offset) {
        return sequence[offset];
    }

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
            string += String.format("%2h", b);
            written++;
        }
        return string + "\n";
    }

    @Override
    public int size() {
        return sequence.length;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T clone() {
        try {
            T clone = (T) super.clone();
            clone.sequence = sequence.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public String diff(T item) {
        String diff1 = "";
        String diff2 = "";
        String str1 = "";
        String str2 = "";
        boolean eq = true;
        for (int i=0; i<sequence.length; i++) {
            if (i>0 && i%4==0) {
                diff1 += "| ";
                diff2 += "| ";
            }
            diff1 += String.format("%2h ", sequence[i]);
            str1 += (char)sequence[i];
            str2 += (char)item.sequence[i];
            if (sequence[i]==item.sequence[i]) {
                diff2 += String.format("== ");
            } else {
                diff2 += String.format("%2h ", item.sequence[i]);
                eq = false;
            }
        }
        if (eq) {
            return null;
        }
        return diff1+" ("+prepare(str1)+")"+"\n"+diff2+" ("+prepare(str2)+")"+"\n";
    }

    public static String prepare(String str) {
        return str.replaceAll("\\s", " ");
    }
}
