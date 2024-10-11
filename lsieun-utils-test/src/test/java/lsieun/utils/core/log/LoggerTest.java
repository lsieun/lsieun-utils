package lsieun.utils.core.log;

import org.junit.jupiter.api.Test;

class LoggerTest {
    @Test
    void test() {
        Logger logger = LoggerFactory.getLogger(LoggerTest.class);
        Logger.CURRENT_LEVEL = LogLevel.INFO;
        String msg = "Hello World!";
        logger.trace(msg);
        logger.debug(msg);
        logger.info(msg);
        logger.warn(msg);
        logger.error(msg);
    }
}