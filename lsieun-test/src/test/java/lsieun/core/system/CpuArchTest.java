package lsieun.core.system;

import org.junit.jupiter.api.Test;

class CpuArchTest {
    @Test
    void testCurrent() {
        CpuArch current = CpuArch.CURRENT;
        System.out.println(current);
    }
}
