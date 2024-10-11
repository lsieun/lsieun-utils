package lsieun.utils.match.clazz;

import lsieun.utils.match.SameTypeBiPredicate;

public enum ClassMatch implements SameTypeBiPredicate<Class<?>> {
    EXACTLY {
        @Override
        public boolean test(Class<?> clazz1, Class<?> clazz2) {
            return clazz1 == clazz2;
        }
    },
    ASSIGNABLE_FROM {
        @Override
        public boolean test(Class<?> clazz1, Class<?> clazz2) {
            return clazz1.isAssignableFrom(clazz2);
        }
    };
}
