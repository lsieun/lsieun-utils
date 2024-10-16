package lsieun.utils.asm.common;


import lsieun.utils.asm.match.MemberInfoMatch;
import lsieun.utils.core.io.resource.ResourceUtils;
import lsieun.utils.match.text.TextMatch;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class ClassFileAnalysisUtilsTest {

    @Test
    void testListMembers() {
        byte[] bytes = ResourceUtils.readClassBytes(Arrays.class);
        MemberInfoMatch memberMatch = MemberInfoMatch.byName(
                TextMatch.equals("toString")
        );
        ClassFileAnalysisUtils.listMembers(bytes, memberMatch);
    }

    @Test
    void analysis() {
    }
}