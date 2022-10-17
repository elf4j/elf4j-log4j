package elf4j.log4j;

import elf4j.Logger;
import elf4j.spi.LoggerFactory;

public class Log4jLoggerFactory implements LoggerFactory {
    @Override
    public Logger logger() {
        return Log4jLogger.instance();
    }

    @Override
    public Logger logger(String name) {
        return Log4jLogger.instance(name);
    }

    @Override
    public Logger logger(Class<?> clazz) {
        return Log4jLogger.instance(clazz);
    }
}
