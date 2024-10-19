package lsieun.base.jdk;

import org.junit.jupiter.api.Test;

class JdkUtilsTest {
    @Test
    void testGetJVMVersion() {
        int jvmVersion = JdkUtils.JVM_VERSION;
        System.out.println(jvmVersion);
    }

    @Test
    void testGetJvmName() {
        String jvmName = JdkUtils.getJvmName();
        System.out.println(jvmName);
    }
}