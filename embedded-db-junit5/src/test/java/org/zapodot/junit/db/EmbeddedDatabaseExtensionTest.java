package org.zapodot.junit.db;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class EmbeddedDatabaseExtensionTest {

    @Test
    void loadUsingServiceLoader() throws ClassNotFoundException {
        final ServiceLoader<?> serviceLoader = ServiceLoader.load(Class.forName("org.junit.jupiter.api.extension.Extension"));
        assertNotNull(serviceLoader);
        final List<?> services = StreamSupport.stream(serviceLoader.spliterator(), false)
                                             .collect(Collectors.toList());
        assertEquals(1, services.size());
    }
}