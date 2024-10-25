package lsieun.asm.common.analysis;


import lsieun.asm.sam.match.MemberInfoMatch;
import lsieun.base.io.resource.ResourceUtils;
import lsieun.core.sam.match.text.TextMatch;
import org.junit.jupiter.api.Test;

class ClassFileAnalysisUtilsTest {

    @Test
    void testListMembers() {
        byte[] bytes = ResourceUtils.readClassBytes(StringBuilder.class);
        MemberInfoMatch memberMatch = MemberInfoMatch.byName(
                TextMatch.equals("append")
        );
        ClassFileAnalysisUtils.listMembers(bytes, memberMatch);
    }

}