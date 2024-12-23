package lsieun.base.base;

import java.util.Objects;

public enum CaseFormat {
    LOWER_HYPHEN(CharMatcher.is('-'), "-") {
        String normalizeWord(String word) {
            return Ascii.toLowerCase(word);
        }

        String convert(CaseFormat format, String s) {
            if (format == LOWER_UNDERSCORE)
                return s.replace('-', '_');
            if (format == UPPER_UNDERSCORE)
                return Ascii.toUpperCase(s.replace('-', '_'));
            return super.convert(format, s);
        }
    },
    LOWER_UNDERSCORE(CharMatcher.is('_'), "_") {
        String normalizeWord(String word) {
            return Ascii.toLowerCase(word);
        }

        String convert(CaseFormat format, String s) {
            if (format == LOWER_HYPHEN)
                return s.replace('_', '-');
            if (format == UPPER_UNDERSCORE)
                return Ascii.toUpperCase(s);
            return super.convert(format, s);
        }
    },
    LOWER_CAMEL(CharMatcher.inRange('A', 'Z'), "") {
        String normalizeWord(String word) {
            return firstCharOnlyToUpper(word);
        }

        String normalizeFirstWord(String word) {
            return Ascii.toLowerCase(word);
        }
    },
    UPPER_CAMEL(CharMatcher.inRange('A', 'Z'), "") {
        String normalizeWord(String word) {
            return firstCharOnlyToUpper(word);
        }
    },
    UPPER_UNDERSCORE(CharMatcher.is('_'), "_") {
        String normalizeWord(String word) {
            return Ascii.toUpperCase(word);
        }

        String convert(CaseFormat format, String s) {
            if (format == LOWER_HYPHEN)
                return Ascii.toLowerCase(s.replace('_', '-'));
            if (format == LOWER_UNDERSCORE)
                return Ascii.toLowerCase(s);
            return super.convert(format, s);
        }
    };

    private final CharMatcher wordBoundary;

    private final String wordSeparator;

    CaseFormat(CharMatcher wordBoundary, String wordSeparator) {
        this.wordBoundary = wordBoundary;
        this.wordSeparator = wordSeparator;
    }

    public final String to(CaseFormat format, String str) {
        Preconditions.checkNotNull(format);
        Preconditions.checkNotNull(str);
        return (format == this) ? str : convert(format, str);
    }

    String convert(CaseFormat format, String s) {
        StringBuilder out = null;
        int i = 0;
        int j = -1;
        while ((j = this.wordBoundary.indexIn(s, ++j)) != -1) {
            if (i == 0) {
                out = new StringBuilder(s.length() + 4 * format.wordSeparator.length());
                out.append(format.normalizeFirstWord(s.substring(i, j)));
            }
            else {
                ((StringBuilder) Objects.<StringBuilder>requireNonNull(out)).append(format.normalizeWord(s.substring(i, j)));
            }
            out.append(format.wordSeparator);
            i = j + this.wordSeparator.length();
        }
        return (i == 0) ?
                format.normalizeFirstWord(s) : (
                (StringBuilder) Objects.<StringBuilder>requireNonNull(out)).append(format.normalizeWord(s.substring(i))).toString();
    }

//    public Function<String, String> converterTo(CaseFormat targetFormat) {
//        return new StringConverter(this, targetFormat);
//    }

//    private static final class StringConverter extends Function<String, String> implements Serializable {
//        private final CaseFormat sourceFormat;
//
//        private final CaseFormat targetFormat;
//
//        private static final long serialVersionUID = 0L;
//
//        StringConverter(CaseFormat sourceFormat, CaseFormat targetFormat) {
//            this.sourceFormat = Preconditions.<CaseFormat>checkNotNull(sourceFormat);
//            this.targetFormat = Preconditions.<CaseFormat>checkNotNull(targetFormat);
//        }
//
//        protected String doForward(String s) {
//            return this.sourceFormat.to(this.targetFormat, s);
//        }
//
//        protected String doBackward(String s) {
//            return this.targetFormat.to(this.sourceFormat, s);
//        }
//
//        public boolean equals(Object object) {
//            if (object instanceof StringConverter) {
//                StringConverter that = (StringConverter) object;
//                return (this.sourceFormat.equals(that.sourceFormat) && this.targetFormat.equals(that.targetFormat));
//            }
//            return false;
//        }
//
//        public int hashCode() {
//            return this.sourceFormat.hashCode() ^ this.targetFormat.hashCode();
//        }
//
//        public String toString() {
//            return this.sourceFormat + ".converterTo(" + this.targetFormat + ")";
//        }
//    }

    String normalizeFirstWord(String word) {
        return normalizeWord(word);
    }

    private static String firstCharOnlyToUpper(String word) {
        return word.isEmpty() ?
                word : (
                Ascii.toUpperCase(word.charAt(0)) + Ascii.toLowerCase(word.substring(1)));
    }

    abstract String normalizeWord(String paramString);
}