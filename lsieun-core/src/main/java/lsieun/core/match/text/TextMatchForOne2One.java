package lsieun.core.match.text;

import java.util.function.BiPredicate;

public enum TextMatchForOne2One implements BiPredicate<String, String> {
    EQUALS_FULLY {
        @Override
        public boolean test(String actual, String expected) {
            return actual.equals(expected);
        }
    },

    EQUALS_FULLY_IGNORE_CASE {
        @Override
        public boolean test(String actual, String expected) {
            return actual.equalsIgnoreCase(expected);
        }
    },


    STARTS_WITH {
        @Override
        public boolean test(String actual, String expected) {
            return actual.startsWith(expected);
        }
    },


    STARTS_WITH_IGNORE_CASE {
        @Override
        public boolean test(String actual, String expected) {
            return actual.toLowerCase().startsWith(expected.toLowerCase());
        }
    },


    ENDS_WITH {
        @Override
        public boolean test(String actual, String expected) {
            return actual.endsWith(expected);
        }
    },


    ENDS_WITH_IGNORE_CASE {
        @Override
        public boolean test(String actual, String expected) {
            return actual.toLowerCase().endsWith(expected.toLowerCase());
        }
    },


    CONTAINS {
        @Override
        public boolean test(String actual, String expected) {
            return actual.contains(expected);
        }
    },


    CONTAINS_IGNORE_CASE {
        @Override
        public boolean test(String actual, String expected) {
            return actual.toLowerCase().contains(expected.toLowerCase());
        }
    },

    REGEX {
        @Override
        public boolean test(String actual, String regex) {
            return actual.matches(regex);
        }
    };
}
