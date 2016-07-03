package tools.bindiff.compare;

import tools.bindiff.simple.Strings;

/**
 * Created by ptasha on 14/02/15.
 */
public class ByteKey {
    private final ByteKey parent;
    private final byte b;
    private final int hash;
    private final int size;

    public ByteKey(byte b) {
        this(null, b);
    }

    public static ByteKey create(int... bytes) {
        ByteKey key = null;
        for (int i=0; i<bytes.length; i++) {
            key = new ByteKey(key, (byte)bytes[i]);
        }
        return key;
    }

    public ByteKey(ByteKey parent, byte b) {
        this.parent = parent;
        this.b = b;
        this.hash = getHashCode();
        if (parent==null) {
            size = 1;
        } else {
            size = parent.size + 1;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ByteKey byteKey = (ByteKey) o;

        if (b != byteKey.b) return false;
        if (parent != null ? !parent.equals(byteKey.parent) : byteKey.parent != null) return false;

        return true;
    }

    public int getHashCode() {
        int result = parent != null ? parent.hash : 0;
        result = 31 * result + (int) b;
        return result;
    }

    @Override
    public int hashCode() {
        return hash;
    }

    public int size() {
        return size;
    }

    public String toString() {
        return (parent==null?"":parent.toString()) + String.format("%2x", b);
    }
}
