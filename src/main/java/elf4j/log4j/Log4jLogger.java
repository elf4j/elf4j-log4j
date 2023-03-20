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
import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import static elf4j.Level.*;

@Immutable
@ToString
class Log4jLogger implements Logger {
    private static final Level DEFAULT_LEVEL = INFO;
    private static final String FQCN = Log4jLogger.class.getName();
    private static final int INSTANCE_CALLER_DEPTH = 4;
    private static final EnumMap<Level, org.apache.logging.log4j.Level> LEVEL_MAP = setLevelMap();
    private static final EnumMap<Level, Map<String, Log4jLogger>> LOGGER_CACHE = initLoggerCache();
    private final boolean enabled;
    @NonNull private final ExtendedLogger extendedLogger;
    @NonNull private final Level level;
    @NonNull private final String name;

    private Log4jLogger(@NonNull String name, @NonNull Level level) {
        this.name = name;
        this.level = level;
        this.extendedLogger = LogManager.getContext().getLogger(name);
        this.enabled = this.extendedLogger.isEnabled(LEVEL_MAP.get(this.level));
    }

    static Log4jLogger instance() {
        return getLogger(StackLocatorUtil.getCallerClass(INSTANCE_CALLER_DEPTH).getName());
    }

    private static Log4jLogger getLogger(@NonNull String name, @NonNull Level level) {
        return LOGGER_CACHE.get(level).computeIfAbsent(name, k -> new Log4jLogger(k, level));
    }

    private static Log4jLogger getLogger(String name) {
        return getLogger(name, DEFAULT_LEVEL);
    }

    private static EnumMap<Level, Map<String, Log4jLogger>> initLoggerCache() {
        EnumMap<Level, Map<String, Log4jLogger>> loggerCache = new EnumMap<>(Level.class);
        EnumSet.allOf(Level.class).forEach(level -> loggerCache.put(level, new ConcurrentHashMap<>()));
        return loggerCache;
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

    private static Object supply(Object o) {
        return o instanceof Supplier<?> ? ((Supplier<?>) o).get() : o;
    }

    private static Object[] supply(Object[] objects) {
        return Arrays.stream(objects).map(Log4jLogger::supply).toArray();
    }

    @Override
    public Logger atLevel(Level level) {
        if (this.level == level) {
            return this;
        }
        return getLogger(this.name, level);
    }

    @Override
    public @NonNull Level getLevel() {
        return this.level;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public void log(Object message) {
        extendedLogger.logIfEnabled(FQCN, LEVEL_MAP.get(this.level), null, supply(message), null);
    }

    @Override
    public void log(String message, Object... args) {
        if (!this.isEnabled()) {
            return;
        }
        extendedLogger.logIfEnabled(FQCN, LEVEL_MAP.get(this.level), null, message, supply(args));
    }

    @Override
    public void log(Throwable t) {
        if (!this.isEnabled()) {
            return;
        }
        extendedLogger.logIfEnabled(FQCN, LEVEL_MAP.get(this.level), null, t.getMessage(), t);
    }

    @Override
    public void log(Throwable t, Object message) {
        if (!this.isEnabled()) {
            return;
        }
        extendedLogger.logIfEnabled(FQCN, LEVEL_MAP.get(this.level), null, supply(message), t);
    }

    @Override
    public void log(Throwable t, String message, Object... args) {
        if (!this.isEnabled()) {
            return;
        }
        extendedLogger.logIfEnabled(FQCN,
                LEVEL_MAP.get(this.level),
                null,
                new FormattedMessage(message, supply(args)),
                t);
    }
}
