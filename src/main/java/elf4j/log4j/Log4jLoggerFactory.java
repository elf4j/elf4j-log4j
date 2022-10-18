package elf4j.log4j;

import elf4j.Logger;
import elf4j.spi.LoggerFactory;

import javax.annotation.Nullable;

public class Log4jLoggerFactory implements LoggerFactory {
    @Override
    public Logger logger() {
        return Log4jLogger.instance();
    }

    @Override
    public Logger logger(@Nullable String name) {
        return Log4jLogger.instance(name);
    }

    @Override
    public Logger logger(@Nullable Class<?> clazz) {
        return Log4jLogger.instance(clazz);
    }
}
