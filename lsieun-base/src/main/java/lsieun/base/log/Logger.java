package lsieun.base.log;

import java.text.MessageFormat;
import java.util.function.Supplier;

@SuppressWarnings("UnnecessaryLocalVariable")
public class Logger {
    public static LogLevel CURRENT_LEVEL = LogLevel.INFO;

    private final Class<?> clazz;

    Logger(Class<?> clazz) {
        this.clazz = clazz;
    }

    public void trace(String pattern, Object... arguments) {
        if (LogLevel.TRACE.val >= CURRENT_LEVEL.val) {
            String msg = format(pattern, arguments);
            log(LogColor.CYAN, LogLevel.TRACE, msg);
        }
    }

    public void trace(Supplier<String> supplier) {
        if (LogLevel.TRACE.val >= CURRENT_LEVEL.val) {
            String msg = supplier.get();
            log(LogColor.CYAN, LogLevel.TRACE, msg);
        }
    }

    public void debug(String pattern, Object... arguments) {
        if (LogLevel.DEBUG.val >= CURRENT_LEVEL.val) {
            String msg = format(pattern, arguments);
            log(LogColor.GREEN, LogLevel.DEBUG, msg);
        }
    }

    public void debug(Supplier<String> supplier) {
        if (LogLevel.DEBUG.val >= CURRENT_LEVEL.val) {
            String msg = supplier.get();
            log(LogColor.GREEN, LogLevel.DEBUG, msg);
        }
    }

    public void info(String pattern, Object... arguments) {
        if (LogLevel.INFO.val >= CURRENT_LEVEL.val) {
            String msg = format(pattern, arguments);
            log(LogColor.BLUE, LogLevel.INFO, msg);
        }
    }

    public void info(Supplier<String> supplier) {
        if (LogLevel.INFO.val >= CURRENT_LEVEL.val) {
            String msg = supplier.get();
            log(LogColor.BLUE, LogLevel.INFO, msg);
        }
    }

    public void warn(String pattern, Object... arguments) {
        if (LogLevel.WARN.val >= CURRENT_LEVEL.val) {
            String msg = format(pattern, arguments);
            log(LogColor.MAGENTA, LogLevel.WARN, msg);
        }
    }

    public void warn(Supplier<String> supplier) {
        if (LogLevel.WARN.val >= CURRENT_LEVEL.val) {
            String msg = supplier.get();
            log(LogColor.MAGENTA, LogLevel.WARN, msg);
        }
    }

    public void error(String pattern, Object... arguments) {
        if (LogLevel.ERROR.val >= CURRENT_LEVEL.val) {
            String msg = format(pattern, arguments);
            log(LogColor.RED, LogLevel.ERROR, msg);
        }
    }

    public void error(Supplier<String> supplier) {
        if (LogLevel.ERROR.val >= CURRENT_LEVEL.val) {
            String msg = supplier.get();
            log(LogColor.RED, LogLevel.ERROR, msg);
        }
    }

    /**
     * <code>MessageFormat.format("User: id={0}, name={1}", id, name)</code>
     */
    private String format(String pattern, Object... arguments) {
        String msg = MessageFormat.format(pattern, arguments);
        return msg;
    }

    public void log(LogColor color, LogLevel level, String msg) {
        String typeName = clazz.getTypeName();
        String formattedMsg = String.format(
                "%s[%5s] [%s] - %s%s",
                color.val,
                level,
                typeName,
                msg,
                LogColor.RESET.val
        );
        System.out.println(formattedMsg);
    }
}
