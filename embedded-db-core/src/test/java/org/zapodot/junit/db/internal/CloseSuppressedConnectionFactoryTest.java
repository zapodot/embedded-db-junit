package org.zapodot.junit.db.internal;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.SQLException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
/**
 * Created by zapodot on 19.05.2017.
 */
public class CloseSuppressedConnectionFactoryTest {

    @Test
    public void instantiationUsingPrivateConstructor() throws Exception {
        final Constructor<CloseSuppressedConnectionFactory> constructor = CloseSuppressedConnectionFactory.class
                .getDeclaredConstructor();
        assertThrows(IllegalAccessException.class, () -> constructor.newInstance());
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

    @Test
    public void wrapMockedConnection() throws SQLException {
        final Connection connection = mock(Connection.class);
        final Connection proxy = CloseSuppressedConnectionFactory.createProxy(connection);
        assertNotNull(proxy);
        proxy.close();
        verifyNoInteractions(connection);
    }

}