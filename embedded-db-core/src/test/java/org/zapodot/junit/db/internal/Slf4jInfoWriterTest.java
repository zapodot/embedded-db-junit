package org.zapodot.junit.db.internal;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class Slf4jInfoWriterTest {

    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Spy
    public final Logger logger = LoggerFactory.getLogger(getClass());

    @Test(expected = IllegalArgumentException.class)
    public void constructNullLogger() {
        assertNotNull(new Slf4jInfoWriter(null));
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
        verifyZeroInteractions(logger);
    }

    @Test
    public void close() {
        final Slf4jInfoWriter slf4jInfoWriter = new Slf4jInfoWriter(logger);
        assertNotNull(slf4jInfoWriter); // Added to avoid SonarLint report about having no assertions in test
        slf4jInfoWriter.close();
        verifyZeroInteractions(logger);
    }
}