package elf4j.log4j;

import elf4j.Level;
import elf4j.Logger;
import lombok.NonNull;
import lombok.ToString;
import net.jcip.annotations.Immutable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.message.FormattedMessage;
import org.apache.logging.log4j.spi.ExtendedLogger;
import org.apache.logging.log4j.util.StackLocatorUtil;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import static elf4j.Level.*;

@Immutable
@ToString
class Log4jElf4jLogger implements Logger {
    private static final EnumMap<Level, Map<String, Log4jElf4jLogger>> CACHED_LOGGERS = initCachedLoggers();
    private static final Level DEFAULT_LEVEL = INFO;
    private static final int INSTANCE_CALLER_DEPTH = 4;
    private static final EnumMap<Level, org.apache.logging.log4j.Level> LEVEL_MAP = setLevelMap();
    @NonNull private final String name;
    @NonNull private final Level level;
    private final ExtendedLogger extendedLogger;

    private Log4jElf4jLogger(@NonNull String name, @NonNull Level level) {
        this.name = name;
        this.level = level;
        this.extendedLogger = LogManager.getContext().getLogger(name);
    }

    static Log4jElf4jLogger instance() {
        return getLogger(StackLocatorUtil.getCallerClass(INSTANCE_CALLER_DEPTH).getName());
    }

    static Log4jElf4jLogger instance(String name) {
        return getLogger(name == null ? StackLocatorUtil.getCallerClass(INSTANCE_CALLER_DEPTH).getName() : name);
    }

    static Log4jElf4jLogger instance(Class<?> clazz) {
        return getLogger(
                clazz == null ? StackLocatorUtil.getCallerClass(INSTANCE_CALLER_DEPTH).getName() : clazz.getName());
    }

    private static Log4jElf4jLogger getLogger(@NonNull String name, @NonNull Level level) {
        return CACHED_LOGGERS.get(level).computeIfAbsent(name, k -> new Log4jElf4jLogger(k, level));
    }

    private static Log4jElf4jLogger getLogger(String name) {
        return getLogger(name, DEFAULT_LEVEL);
    }

    private static EnumMap<Level, Map<String, Log4jElf4jLogger>> initCachedLoggers() {
        EnumMap<Level, Map<String, Log4jElf4jLogger>> cachedLoggers = new EnumMap<>(Level.class);
        cachedLoggers.put(TRACE, new ConcurrentHashMap<>());
        cachedLoggers.put(DEBUG, new ConcurrentHashMap<>());
        cachedLoggers.put(INFO, new ConcurrentHashMap<>());
        cachedLoggers.put(WARN, new ConcurrentHashMap<>());
        cachedLoggers.put(ERROR, new ConcurrentHashMap<>());
        cachedLoggers.put(OFF, new ConcurrentHashMap<>());
        return cachedLoggers;
    }

    private static EnumMap<Level, org.apache.logging.log4j.Level> setLevelMap() {
        EnumMap<Level, org.apache.logging.log4j.Level> levelMap = new EnumMap<>(Level.class);
        levelMap.put(TRACE, org.apache.logging.log4j.Level.TRACE);
        levelMap.put(DEBUG, org.apache.logging.log4j.Level.DEBUG);
        levelMap.put(INFO, org.apache.logging.log4j.Level.INFO);
        levelMap.put(WARN, org.apache.logging.log4j.Level.WARN);
        levelMap.put(ERROR, org.apache.logging.log4j.Level.ERROR);
        levelMap.put(OFF, org.apache.logging.log4j.Level.OFF);
        return levelMap;
    }

    @Override
    public @NonNull String getName() {
        return this.name;
    }

    @Override
    public @NonNull Level getLevel() {
        return this.level;
    }

    @Override
    public Logger atLevel(Level level) {
        if (this.level == level) {
            return this;
        }
        return getLogger(this.name, level);
    }

    @Override
    public Logger atTrace() {
        return atLevel(TRACE);
    }

    @Override
    public Logger atDebug() {
        return atLevel(DEBUG);
    }

    @Override
    public Logger atInfo() {
        return atLevel(INFO);
    }

    @Override
    public Logger atWarn() {
        return atLevel(WARN);
    }

    @Override
    public Logger atError() {
        return atLevel(ERROR);
    }

    @Override
    public void log(Object message) {
        extendedLogger.log(LEVEL_MAP.get(this.level), message);
    }

    @Override
    public void log(Supplier<?> message) {
        if (isLevelDisabled()) {
            return;
        }
        extendedLogger.log(LEVEL_MAP.get(this.level), message.get());
    }

    @Override
    public void log(String message, Object... args) {
        extendedLogger.log(LEVEL_MAP.get(this.level), message, args);
    }

    @Override
    public void log(String message, Supplier<?>... args) {
        if (isLevelDisabled()) {
            return;
        }
        extendedLogger.log(LEVEL_MAP.get(this.level),
                message,
                Arrays.stream(args).map(Supplier::get).toArray(Object[]::new));
    }

    @Override
    public void log(Throwable t) {
        extendedLogger.log(LEVEL_MAP.get(this.level), t);
    }

    @Override
    public void log(Throwable t, String message) {
        extendedLogger.log(LEVEL_MAP.get(this.level), message, t);
    }

    @Override
    public void log(Throwable t, Supplier<String> message) {
        if (isLevelDisabled()) {
            return;
        }
        extendedLogger.log(LEVEL_MAP.get(this.level), message.get(), t);
    }

    @Override
    public void log(Throwable t, String message, Object... args) {
        extendedLogger.log(LEVEL_MAP.get(this.level), new FormattedMessage(message, args, t));
    }

    @Override
    public void log(Throwable t, String message, Supplier<?>... args) {
        if (isLevelDisabled()) {
            return;
        }
        extendedLogger.log(LEVEL_MAP.get(this.level),
                new FormattedMessage(message, Arrays.stream(args).map(Supplier::get).toArray(Object[]::new), t));
    }

    private boolean isLevelDisabled() {
        return !extendedLogger.isEnabled(LEVEL_MAP.get(this.level));
    }
}
