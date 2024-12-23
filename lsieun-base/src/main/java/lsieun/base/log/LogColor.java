package lsieun.base.log;

public enum LogColor {
    BLACK("\033[0;30m"),
    RED("\033[0;31m"),
    GREEN("\033[0;32m"),
    YELLOW("\033[0;33m"),
    BLUE("\033[0;34m"),
    MAGENTA("\033[0;35m"),
    CYAN("\033[0;36m"),
    WHITE("\033[0;37m"),
    RESET("\033[0m"),
    ;

    public final String val;

    LogColor(String val) {
        this.val = val;
    }
}
