package lsieun.base.text.escape;

import lsieun.base.base.Preconditions;

public abstract class CharEscaper extends Escaper {
    private static final int DEST_PAD_MULTIPLIER = 2;

    public String escape(String string) {
        Preconditions.checkNotNull(string);
        int length = string.length();
        for (int index = 0; index < length; index++) {
            if (escape(string.charAt(index)) != null)
                return escapeSlow(string, index);
        }
        return string;
    }


    protected abstract char[] escape(char paramChar);

    protected final String escapeSlow(String s, int index) {
        int slen = s.length();
        char[] dest = Platform.charBufferFromThreadLocal();
        int destSize = dest.length;
        int destIndex = 0;
        int lastEscape = 0;
        for (; index < slen; index++) {
            char[] r = escape(s.charAt(index));
            if (r != null) {
                int rlen = r.length;
                int charsSkipped = index - lastEscape;
                int sizeNeeded = destIndex + charsSkipped + rlen;
                if (destSize < sizeNeeded) {
                    destSize = sizeNeeded + 2 * (slen - index);
                    dest = growBuffer(dest, destIndex, destSize);
                }
                if (charsSkipped > 0) {
                    s.getChars(lastEscape, index, dest, destIndex);
                    destIndex += charsSkipped;
                }
                if (rlen > 0) {
                    System.arraycopy(r, 0, dest, destIndex, rlen);
                    destIndex += rlen;
                }
                lastEscape = index + 1;
            }
        }
        int charsLeft = slen - lastEscape;
        if (charsLeft > 0) {
            int sizeNeeded = destIndex + charsLeft;
            if (destSize < sizeNeeded)
                dest = growBuffer(dest, destIndex, sizeNeeded);
            s.getChars(lastEscape, slen, dest, destIndex);
            destIndex = sizeNeeded;
        }
        return new String(dest, 0, destIndex);
    }

    private static char[] growBuffer(char[] dest, int index, int size) {
        if (size < 0)
            throw new AssertionError("Cannot increase internal buffer any further");
        char[] copy = new char[size];
        if (index > 0)
            System.arraycopy(dest, 0, copy, 0, index);
        return copy;
    }
}
