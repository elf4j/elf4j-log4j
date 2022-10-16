# elf4j-log4j

The [LOG4J](https://logging.apache.org/log4j/2.x/) service provider binding for the Easy Logging Facade for
Java ([ELF4J](https://github.com/elf4j/elf4j-api)) SPI

## User story

As a service provider of the Easy Logging Facade for Java ([ELF4J](https://github.com/elf4j/elf4j-api)) SPI, I want to
bind the logging capabilities of LOG4J to the ELF4J client application via the
Java [SPI](https://docs.oracle.com/javase/tutorial/sound/SPI-intro.html) mechanism, so that any application using the
ELF4J API for logging can decide to use the log4j features at deployment time without code change.

## Prerequisite

- Java 8 or better
- LOG4J 2.19.0 or better

## Get it...

[![Maven Central](https://img.shields.io/maven-central/v/io.github.elf4j/elf4j-log4j.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.elf4j%22%20AND%20a:%22elf4j-log4j%22)

## Use it...

Simply pack the JAR of this binding in the deployment classpath of any ELF4J client application. e.g. in Maven pom.xml,
this provider bind JAR will be added as a runtime-scoped dependency. No code change needed since the client application
codebase is already using the ELF4J API for logging.

At compile time, the client application is unaware of this run-time logging service provider. Using LOG4J as the
logging implementation is a deployment time decision of such client application.

The usual [LOG4J configuration](https://logging.apache.org/log4j/2.x/manual/configuration.html) applies.

