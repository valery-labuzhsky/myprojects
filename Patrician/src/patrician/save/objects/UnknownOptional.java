package patrician.save.objects;

import java.util.List;

/**
 * Created on 04/04/15.
 *
 * @author ptasha
 */
public class UnknownOptional implements Item<UnknownOptional> {
    private int header;
    private final Unknown unknown0;
    private final Unknown unknown1;

    public UnknownOptional(Unknown unknown0, Unknown unknown1) {
        this.unknown0 = unknown0;
        this.unknown1 = unknown1;
    }

    @Override
    public int read(int offset, List<Integer> data) {
        header = data.get(offset++);
//        System.out.println("HEADER: "+header);
        if (header>1) {
            throw new RuntimeException("Unknown header "+header);
        }
        return getVariant().read(offset, data)+1;
    }

    @Override
    public int get(int offset) {
        return getVariant().get(offset-1);
    }

    @Override
    public String diff(UnknownOptional item) {
        return getVariant().diff(item.getVariant());
    }

    private Unknown getVariant() {
        if (header==0) {
            return unknown0;
        } else {
            return unknown1;
        }
    }

    @Override
    public UnknownOptional clone() {
        UnknownOptional clone = new UnknownOptional(unknown0.clone(), unknown1.clone());
        clone.header = header;
        return clone;
    }

    @Override
    public int size() {
        if (header==0) {
            return unknown0.size() + 1;
        } else {
            return unknown1.size() + 1;
        }
    }

    @Override
    public String toString() {
        return header + ": "+getVariant().toString();
    }
}
