package lsieun.base.log;

public enum LogLevel {
    TRACE(1),
    DEBUG(2),
    INFO(3),
    WARN(4),
    ERROR(5),
    OFF(100);

    public final int val;

    LogLevel(int val) {
        this.val = val;
    }
}
