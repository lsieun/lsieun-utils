package lsieun.core.system;

import com.sun.jna.Pointer;
import com.sun.jna.platform.mac.SystemB;
import com.sun.jna.platform.unix.LibCAPI;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.ptr.IntByReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 来自 IntelliJ IDEA 的 util-8.jar 中 com.intellij.util.system.CpuArch
 */
public enum CpuArch {
    X86(32),
    X86_64(64),
    ARM32(32),
    ARM64(64),
    OTHER(0),
    UNKNOWN(0);

    public final int width;
    public static final CpuArch CURRENT = fromString(System.getProperty("os.arch"));
    private static @Nullable Boolean ourEmulated;

    CpuArch(int width) {
        if (width == 0) {
            try {
                width = Integer.parseInt(System.getProperty("sun.arch.data.model", "32"));
            } catch (NumberFormatException var5) {
            }
        }

        this.width = width;
    }

    public static @NotNull CpuArch fromString(@Nullable String arch) {
        if (!"x86_64".equals(arch) && !"amd64".equals(arch)) {
            if (!"i386".equals(arch) && !"x86".equals(arch)) {
                if (!"aarch64".equals(arch) && !"arm64".equals(arch)) {
                    return arch != null && !arch.trim().isEmpty() ? OTHER : UNKNOWN;
                } else {
                    return ARM64;
                }
            } else {
                return X86;
            }
        } else {
            return X86_64;
        }
    }

    public static boolean isIntel32() {
        return CURRENT == X86;
    }

    public static boolean isIntel64() {
        return CURRENT == X86_64;
    }

    public static boolean isArm32() {
        return CURRENT == ARM32;
    }

    public static boolean isArm64() {
        return CURRENT == ARM64;
    }

    public static boolean is32Bit() {
        return CURRENT.width == 32;
    }

    public static boolean isEmulated() {
        if (ourEmulated == null) {
            if (CURRENT == X86_64) {
                ourEmulated = SystemInfoRt.isMac && isUnderRosetta() || SystemInfoRt.isWindows && !matchesWindowsNativeArch();
            } else if (CURRENT == X86) {
                ourEmulated = SystemInfoRt.isWindows && !matchesWindowsNativeArch();
            } else {
                ourEmulated = Boolean.FALSE;
            }
        }

        return ourEmulated == Boolean.TRUE;
    }

    private static boolean isUnderRosetta() {
        IntByReference p = new IntByReference();
        LibCAPI.size_t.ByReference size = new LibCAPI.size_t.ByReference((long) SystemB.INT_SIZE);
        if (SystemB.INSTANCE.sysctlbyname("sysctl.proc_translated", p.getPointer(), size, (Pointer)null, LibCAPI.size_t.ZERO) != -1) {
            return p.getValue() == 1;
        }

        return false;
    }

    private static boolean matchesWindowsNativeArch() {
        WinBase.SYSTEM_INFO systemInfo = new WinBase.SYSTEM_INFO();
        Kernel32.INSTANCE.GetNativeSystemInfo(systemInfo);
        int arch = systemInfo.processorArchitecture.dwOemID.getLow().intValue();
        if (arch == 0) {
            return CURRENT == X86;
        }

        if (arch == 9) {
            return CURRENT == X86_64;
        }

        if (arch == 12) {
            return CURRENT == ARM64;
        }

        return true;
    }
}