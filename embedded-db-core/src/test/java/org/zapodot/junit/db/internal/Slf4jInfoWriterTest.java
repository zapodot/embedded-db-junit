package org.zapodot.junit.db.internal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
public class Slf4jInfoWriterTest {

    @Spy
    public final Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void constructNullLogger() {
        assertThrows(IllegalArgumentException.class, () -> new Slf4jInfoWriter(null));
    }

    @Test
    public void write() {
        final Slf4jInfoWriter slf4jInfoWriter = new Slf4jInfoWriter(logger);
        assertNotNull(slf4jInfoWriter); // Added to avoid SonarLint report about having no assertions in test
        final String logMessage = "Log message";

        slf4jInfoWriter.write(logMessage.toCharArray(), 0, logMessage.length());
        verify(logger).info(eq(logMessage));
    }

    @Test
    public void flush() {
        final Slf4jInfoWriter slf4jInfoWriter = new Slf4jInfoWriter(logger);
        assertNotNull(slf4jInfoWriter); // Added to avoid SonarLint report about having no assertions in test
        slf4jInfoWriter.flush();
        verifyNoInteractions(logger);
    }

    @Test
    public void close() {
        final Slf4jInfoWriter slf4jInfoWriter = new Slf4jInfoWriter(logger);
        assertNotNull(slf4jInfoWriter); // Added to avoid SonarLint report about having no assertions in test
        slf4jInfoWriter.close();
        verifyNoInteractions(logger);
    }
}