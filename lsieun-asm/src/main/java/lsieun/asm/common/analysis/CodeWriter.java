package lsieun.asm.common.analysis;

import jakarta.annotation.Nonnull;

import java.io.PrintWriter;
import java.util.StringTokenizer;


public final class CodeWriter extends PrintWriter {
    private final int myIndent = 4;
    private int myIndentLevel = 0;
    private boolean myNewLineStarted = true;

    public CodeWriter(PrintWriter writer) {
        super(writer);
    }

    public void print(@Nonnull String s) {
        this.possiblyIndent(s);
        super.print(s);

        for (int i = 0; i < s.length(); ++i) {
            if (isOpenBrace(s, i)) {
                ++this.myIndentLevel;
            }

            if (isCloseBrace(s, i)) {
                --this.myIndentLevel;
            }
        }

    }

    private static boolean isCloseBrace(String s, int index) {
        char c = s.charAt(index);
        return c == ')' || c == ']' || c == '}';
    }

    private static boolean isOpenBrace(String s, int index) {
        char c = s.charAt(index);
        return c == '(' || c == '[' || c == '{';
    }

    public void println() {
        ((PrintWriter) this.out).println();
        this.myNewLineStarted = true;
    }

    private void possiblyIndent(String s) {
        if (this.myNewLineStarted) {
            int i;
            for (i = 0; i < s.length() && s.charAt(i) == ' '; ++i) {
            }

            int firstNonBlank = i < s.length() && s.charAt(i) != ' ' ? i : -1;
            if (firstNonBlank >= 0) {
                if (isCloseBrace(s, firstNonBlank)) {
                    --this.myIndentLevel;
                }

                int blanksToPrint = this.myIndent * this.myIndentLevel - firstNonBlank;

                for (int j = 0; j < blanksToPrint; ++j) {
                    this.write(" ");
                }

                if (isCloseBrace(s, firstNonBlank)) {
                    ++this.myIndentLevel;
                }
            }

            this.myNewLineStarted = false;
        }

    }

    public void println(String s) {
        StringTokenizer st = new StringTokenizer(s, "\r\n", false);

        while (st.hasMoreTokens()) {
            super.println(st.nextToken());
        }
    }
}