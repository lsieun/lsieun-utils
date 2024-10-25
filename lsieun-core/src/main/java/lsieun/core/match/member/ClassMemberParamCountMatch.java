package lsieun.core.match.member;

import lsieun.core.match.ToSameTypeBiPredicate;

import java.lang.reflect.Executable;
import java.util.Objects;

class ClassMemberParamCountMatch extends ToSameTypeBiPredicate<Executable, Integer> {
    public static final ClassMemberParamCountMatch INSTANCE = new ClassMemberParamCountMatch();

    private ClassMemberParamCountMatch() {
        super(Executable::getParameterCount, Objects::equals);
    }


}
