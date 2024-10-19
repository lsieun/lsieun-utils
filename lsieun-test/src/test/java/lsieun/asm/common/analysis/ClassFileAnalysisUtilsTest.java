package lsieun.asm.common.analysis;


import lsieun.asm.match.MemberInfoMatch;
import lsieun.base.io.resource.ResourceUtils;
import lsieun.core.match.text.TextMatch;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class ClassFileAnalysisUtilsTest {

    @Test
    void testListMembers() {
        byte[] bytes = ResourceUtils.readClassBytes(StringBuilder.class);
        MemberInfoMatch memberMatch = MemberInfoMatch.byName(
                TextMatch.equals("append")
        );
        ClassFileAnalysisUtils.listMembers(bytes, memberMatch);
    }

    @Test
    void analysis() {
    }
}