package elf4j.log4j;

import elf4j.Logger;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.stream.Collectors;

import static elf4j.util.MessageArguments.arg;
import static org.junit.jupiter.api.Assertions.assertEquals;

class Log4jLoggerTest {
    public static final Logger LOGGER = Logger.instance(Log4jLoggerTest.class);

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
        void messagesArgsAndGuards() {
            logger.atInfo().log("info message");
            logger.atWarn()
                    .log("message arguments of Supplier<?> and other Object types can be mixed and matched, e.g. arg1 {}, arg2 {}, arg3 {}",
                            "a11111",
                            "a22222",
                            arg(() -> Arrays.stream(new Object[] { "a33333 supplier" }).collect(Collectors.toList())));
            Logger debug = logger.atDebug();
            if (debug.isEnabled()) {
                debug.log("a {} guarded by a {}, so {} is created {} DEBUG {} is {}",
                        "long message",
                        "level check",
                        "no message object",
                        "unless",
                        "level",
                        "enabled");
            }
            debug.log(() -> "alternative to the level guard, using a supplier function should achieve the same goal, pending quality of the logging provider");
        }

        @Test
        void throwableAndMessageAndArgs() {
            logger.atInfo().log("let's see immutability in action...");
            Logger error = logger.atError();
            error.log("this is an immutable logger instance whose level is Level.ERROR");
            Throwable ex = new Exception("ex message");
            error.log(ex, "level set omitted but we know the level is Level.ERROR");
            error.atWarn()
                    .log(ex,
                            "the log level switched to WARN on the fly. that is, {} returns a {} and {} Logger {}",
                            "atWarn()",
                            "different",
                            "immutable",
                            "instance");
            error.atError()
                    .log(ex,
                            "here the {} call is {} because the {} instance is {}, and the instance's log level has and will always be Level.ERROR",
                            "atError()",
                            "unnecessary",
                            "error logger",
                            "immutable");
            error.log(ex,
                    "now at Level.ERROR, together with the exception stack trace, logging some items expensive to compute: item1 {}, item2 {}, item3 {}, item4 {}, ...",
                    "i11111",
                    arg(() -> "i22222"),
                    "i33333",
                    arg(() -> Arrays.stream(new Object[] { "i44444" }).collect(Collectors.toList())));
        }
    }
}