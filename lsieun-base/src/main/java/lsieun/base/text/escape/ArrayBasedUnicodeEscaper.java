package lsieun.base.text.escape;


import lsieun.base.base.Preconditions;

import org.jetbrains.annotations.Nullable;

import java.util.Map;

public abstract class ArrayBasedUnicodeEscaper extends UnicodeEscaper {
    private final char[][] replacements;
    private final int replacementsLength;
    private final int safeMin;
    private final int safeMax;
    private final char safeMinChar;
    private final char safeMaxChar;

    protected ArrayBasedUnicodeEscaper(Map<Character, String> replacementMap, int safeMin, int safeMax, @Nullable String unsafeReplacement) {
        this(ArrayBasedEscaperMap.create(replacementMap), safeMin, safeMax, unsafeReplacement);
    }

    protected ArrayBasedUnicodeEscaper(ArrayBasedEscaperMap escaperMap, int safeMin, int safeMax, @Nullable String unsafeReplacement) {
        Preconditions.checkNotNull(escaperMap);
        this.replacements = escaperMap.getReplacementArray();
        this.replacementsLength = this.replacements.length;
        if (safeMax < safeMin) {
            safeMax = -1;
            safeMin = Integer.MAX_VALUE;
        }

        this.safeMin = safeMin;
        this.safeMax = safeMax;
        if (safeMin >= 55296) {
            this.safeMinChar = '\uffff';
            this.safeMaxChar = 0;
        } else {
            this.safeMinChar = (char)safeMin;
            this.safeMaxChar = (char)Math.min(safeMax, 55295);
        }

    }

    public final String escape(String s) {
        Preconditions.checkNotNull(s);

        for(int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            if (c < this.replacementsLength && this.replacements[c] != null || c > this.safeMaxChar || c < this.safeMinChar) {
                return this.escapeSlow(s, i);
            }
        }

        return s;
    }

    
    protected final char[] escape(int cp) {
        if (cp < this.replacementsLength) {
            char[] chars = this.replacements[cp];
            if (chars != null) {
                return chars;
            }
        }

        return cp >= this.safeMin && cp <= this.safeMax ? null : this.escapeUnsafe(cp);
    }

    protected final int nextEscapeIndex(CharSequence csq, int index, int end) {
        while(true) {
            if (index < end) {
                char c = csq.charAt(index);
                if ((c >= this.replacementsLength || this.replacements[c] == null) && c <= this.safeMaxChar && c >= this.safeMinChar) {
                    ++index;
                    continue;
                }
            }

            return index;
        }
    }

    
    protected abstract char[] escapeUnsafe(int cp);
}