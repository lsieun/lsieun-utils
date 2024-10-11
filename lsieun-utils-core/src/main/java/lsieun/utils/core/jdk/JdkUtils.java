package lsieun.utils.core.jdk;

import lsieun.utils.core.text.StringUtils;

public class JdkUtils {
    public static final int JVM_VERSION;

    static {
        // JVM版本
        JVM_VERSION = getJvmVersion();
    }

    public static String getJvmName() {
        return System.getProperty("java.vm.name");
    }

    private static int getJvmVersion() {
        int jvmVersion = -1;

        try {
            String javaSpecVer = System.getProperty("java.specification.version");
            if (StringUtils.isNotBlank(javaSpecVer)) {
                if (javaSpecVer.startsWith("1.")) {
                    javaSpecVer = javaSpecVer.substring(2);
                }
                if (javaSpecVer.indexOf('.') == -1) {
                    jvmVersion = Integer.parseInt(javaSpecVer);
                }
            }
        } catch (Throwable ignore) {
            // 默认JDK8
            jvmVersion = 8;
        }

        return jvmVersion;
    }
}
