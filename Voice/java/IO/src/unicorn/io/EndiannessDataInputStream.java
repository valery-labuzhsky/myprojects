package unicorn.io;

import java.io.*;

/**
 * @author uniCorn
 * @version 01.02.2008 19:26:16
 */
public class EndiannessDataInputStream extends InputStream implements DataInput {
    private DataInputStream input;
    private Endiannes endiannes;

    public EndiannessDataInputStream(InputStream input) {
        this(input, Endiannes.BIG_ENDIAN);
    }

    public EndiannessDataInputStream(InputStream input, Endiannes endiannes) {
        this.input = new DataInputStream(input);
        this.endiannes = endiannes;
    }

    public int read(byte[] b) throws IOException {
        return input.read(b);
    }

    public int read(byte[] b, int off, int len) throws IOException {
        return input.read(b, off, len);
    }

    public void readFully(byte[] b) throws IOException {
        input.readFully(b);
    }

    public void readFully(byte[] b, int off, int len) throws IOException {
        input.readFully(b, off, len);
    }

    public int skipBytes(int n) throws IOException {
        return input.skipBytes(n);
    }

    public boolean readBoolean() throws IOException {
        return input.readBoolean();
    }

    public byte readByte() throws IOException {
        return input.readByte();
    }

    public int readUnsignedByte() throws IOException {
        return input.readUnsignedByte();
    }

    public short readShort() throws IOException {
        return input.readShort();
    }

    public int readUnsignedShort() throws IOException {
        return input.readUnsignedShort();
    }

    public char readChar() throws IOException {
        return input.readChar();
    }

    public int readInt() throws IOException {
        int ch1 = input.read();
        int ch2 = input.read();
        int ch3 = input.read();
        int ch4 = input.read();
        if ((ch1 | ch2 | ch3 | ch4) < 0)
            throw new EOFException();
        if (endiannes == Endiannes.LITTLE_ENDIAN){
            return ((ch4 << 24) + (ch3 << 16) + (ch2 << 8) + (ch1 << 0));
        } else {
            return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
        }
    }

    public long readLong() throws IOException {
        return input.readLong();
    }

    public float readFloat() throws IOException {
        return Float.intBitsToFloat(readInt());
    }

    public double readDouble() throws IOException {
        return input.readDouble();
    }

    @Deprecated
    public String readLine() throws IOException {
        return input.readLine();
    }

    public String readUTF() throws IOException {
        return input.readUTF();
    }

    public String readUTF(DataInput in) throws IOException {
        return input.readUTF(in);
    }

    public int read() throws IOException {
        return input.read();
    }

    public long skip(long n) throws IOException {
        return input.skip(n);
    }

    public int available() throws IOException {
        return input.available();
    }

    public void close() throws IOException {
        input.close();
    }

    public void mark(int readlimit) {
        input.mark(readlimit);
    }

    public void reset() throws IOException {
        input.reset();
    }

    public boolean markSupported() {
        return input.markSupported();
    }
}
