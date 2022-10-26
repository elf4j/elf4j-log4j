package elf4j.log4j;

import elf4j.Level;
import elf4j.Logger;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Log4jLoggerTest {
    public static final Logger LOGGER = Logger.instance(Log4jLoggerTest.class);

    @Nested
    class levels {

        @Test
        void optToSupplyDefaultLevelAsInfo() {
            assertEquals(Level.INFO, LOGGER.getLevel());
            LOGGER.log("opt to provide default level");
        }

        @Test
        void noArgAtHonorsLeveOnMethodName() {
            assertEquals(Level.TRACE, LOGGER.atTrace().getLevel());
            assertEquals(Level.DEBUG, LOGGER.atDebug().getLevel());
            assertEquals(Level.INFO, LOGGER.atInfo().getLevel());
            assertEquals(Level.WARN, LOGGER.atWarn().getLevel());
            assertEquals(Level.ERROR, LOGGER.atError().getLevel());
        }
    }

    @Nested
    class log {

        @Test
        void object() {
            LOGGER.log("log message");
        }

        @Test
        void supplier() {
            LOGGER.atTrace().log(() -> "supplier message");
        }

        @Test
        void messageAndArgs() {
            LOGGER.atDebug().log("message arg1 {}, arg2 {}", "a11111", new Object());
        }

        @Test
        void messageAndSuppliers() {
            LOGGER.atWarn()
                    .log("message supplier arg1 {}, arg2 {}, arg3 {}",
                            () -> "a11111",
                            () -> "a22222",
                            () -> Arrays.stream(new Object[] { "a33333" }).collect(Collectors.toList()));
        }

        @Test
        void throwable() {
            LOGGER.atError().log(new Exception("ex message"));
        }

        @Test
        void throwableAndMessage() {
            LOGGER.atError().log(new Exception("ex message"), "log message");
        }

        @Test
        void throwableAndSupplier() {
            LOGGER.atError().log(new Exception("ex message"), () -> "supplier log message");
        }

        @Test
        void throwableAndMessageAndArgs() {
            LOGGER.atError().log(new Exception("ex message"), "log message with arg {}", "a11111");
        }

        @Test
        void throwableAndMessageAndSupplierArgs() {
            LOGGER.atError()
                    .log(new Exception("ex message"),
                            "log message with supplier arg1 {}, arg2 {}, arg3 {}",
                            () -> "a11111",
                            () -> "a22222",
                            () -> Arrays.stream(new Object[] { "a33333" }).collect(Collectors.toList()));
        }
    }

    @Nested
    class name {
        @Test
        void loggerNameForNullOrNoargInstanceCaller() {
            String thisClassName = this.getClass().getName();
            assertEquals(thisClassName, Logger.instance((Class<?>) null).getName());
            assertEquals(thisClassName, Logger.instance((String) null).getName());
            assertEquals(thisClassName, Logger.instance().getName());
        }

        @Test
        void blankOrEmptyNamesStayAsIs() {
            String blank = "   ";
            assertEquals(blank, Logger.instance(blank).getName());
            String empty = "";
            assertEquals("", Logger.instance(empty).getName());
        }
    }

    @Nested
    class readmeSamples {
        private final Logger logger = Logger.instance(readmeSamples.class);

        @Test
        void messageAndArgs() {
            logger.atInfo().log("info message");
            logger.atInfo().log("{} is a shorthand of {}", "atInfo()", "atLevel(Level.INFO)");
            logger.atWarn()
                    .log("warn message with supplier arg1 {}, arg2 {}, arg3 {}",
                            () -> "a11111",
                            () -> "a22222",
                            () -> Arrays.stream(new Object[] { "a33333" }).collect(Collectors.toList()));
        }

        @Test
        void throwableAndMessageAndArgs() {
            logger.atInfo().log("let see immutability in action...");
            Logger errorLogger = logger.atError();
            Throwable ex = new Exception("ex message");
            errorLogger.log(ex, "level set omitted, the log level is Level.ERROR");
            errorLogger.atWarn()
                    .log(ex,
                            "the log level switched to WARN on the fly. that is, {} returns a {} and {} Logger {}",
                            "atWarn()",
                            "different",
                            "immutable",
                            "instance");
            errorLogger.atError()
                    .log(ex,
                            "the atError() call is {} because the errorLogger instance is {}, and the instance's log level has always been Level.ERROR",
                            "unnecessary",
                            "immutable");
            errorLogger.log(ex,
                    "now at Level.ERROR, together with the exception stack trace, logging some items expensive to compute: item1 {}, item2 {}, item3 {}, item4 {}, ...",
                    () -> "i11111",
                    () -> "i22222",
                    () -> Arrays.asList("i33333"),
                    () -> Arrays.stream(new Object[] { "i44444" }).collect(Collectors.toList()));
        }
    }
}