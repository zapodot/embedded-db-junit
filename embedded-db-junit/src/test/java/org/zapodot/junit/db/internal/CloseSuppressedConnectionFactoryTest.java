package org.zapodot.junit.db.internal;

import org.junit.Test;

import java.lang.reflect.Constructor;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by cc31904 on 19.05.2017.
 */
public class CloseSuppressedConnectionFactoryTest {

    @Test(expected = IllegalAccessException.class)
    public void instantiationUsingPrivateConstructor() throws Exception {
        final Constructor<CloseSuppressedConnectionFactory> constructor = CloseSuppressedConnectionFactory.class
                .getDeclaredConstructor();
        constructor.newInstance();
    }

    @Test
    public void instantiateAnyway() throws Exception {
        final Constructor<CloseSuppressedConnectionFactory> constructor = CloseSuppressedConnectionFactory.class
                .getDeclaredConstructor();
        try {
            constructor.setAccessible(true);
            assertThat(constructor.newInstance(), notNullValue(CloseSuppressedConnectionFactory.class));
        } finally {
            constructor.setAccessible(false);
        }
    }
}