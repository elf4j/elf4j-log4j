package elf4j.log4j;

import elf4j.Level;
import elf4j.Logger;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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

        @ParameterizedTest
        @ValueSource(strings = { "TRACE", "DEBUG", "INFO", "WARN", "ERROR", "OFF" })
        void atLevel(String level) {
            assertEquals(Level.valueOf(level), LOGGER.atLevel(Level.valueOf(level)).getLevel());
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
            LOGGER.atLevel(Level.TRACE).log(() -> "supplier message");
        }

        @Test
        void messageAndArgs() {
            LOGGER.atDebug().log("message arg1 {}, arg2 {}", "a11111", new Object());
        }

        @Test
        void messageAndSuppliers() {
            LOGGER.atLevel(Level.WARN)
                    .log("message supplier arg1 {}, arg2 {}, arg3 {}",
                            () -> "a11111",
                            () -> "a22222",
                            () -> Arrays.stream(new Object[] { "a33333" }).collect(Collectors.toList()));
        }

        @Test
        void throwable() {
            LOGGER.atLevel(Level.ERROR).log(new Exception("ex message"));
        }

        @Test
        void throwableAndMessage() {
            LOGGER.atLevel(Level.ERROR).log(new Exception("ex message"), "log message");
        }

        @Test
        void throwableAndSupplier() {
            LOGGER.atLevel(Level.ERROR).log(new Exception("ex message"), () -> "supplier log message");
        }

        @Test
        void throwableAndMessageAndArgs() {
            LOGGER.atLevel(Level.ERROR).log(new Exception("ex message"), "log message with arg {}", "a11111");
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
}