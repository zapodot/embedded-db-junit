package org.zapodot.junit.db.internal;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.MethodDelegation;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.util.Objects;

import static net.bytebuddy.matcher.ElementMatchers.any;

/**
 * Needs to be public to be used by ByteBuddy. Part of internal api, so it may be changed or removed without prior warning
 */
public class CloseSuppressedConnectionFactory {

    // Added to prevent instantiation
    private CloseSuppressedConnectionFactory() {
    }

    private static final Class<? extends Connection> proxyType = new ByteBuddy().subclass(Connection.class)
                                                                                .method(any())
                                                                                .intercept(MethodDelegation.to(
                                                                                        ConnectionInterceptor.class))
                                                                                .defineField("delegatedConnection",
                                                                                             Connection.class,
                                                                                             Visibility.PRIVATE)
                                                                                .implement(ConnectionProxy.class)
                                                                                .intercept(FieldAccessor
                                                                                                   .ofBeanProperty())
                                                                                .make()
                                                                                .load(CloseSuppressedConnectionFactory.class
                                                                                              .getClassLoader(),
                                                                                      ClassLoadingStrategy.Default.WRAPPER)
                                                                                .getLoaded();

    /**
     * Create a proxy that delegates to the provided Connection except for calls to "close()" which will be suppressed.
     *
     * @param connection the connection that is to be used as an underlying connection
     * @return a Connection proxy
     */
    public static Connection createProxy(final Connection connection) {
        Objects.requireNonNull(connection, "The \"connection\" argument can not be null");
        return (Connection) createConnectionProxy(connection);

    }

    private static ConnectionProxy createConnectionProxy(final Connection connection) {
        final ConnectionProxy proxy = createProxyInstance();
        proxy.setDelegatedConnection(connection);
        return proxy;
    }

    private static ConnectionProxy createProxyInstance() {
        final Constructor<? extends Connection> constructor = createConstructorForProxyClass();
        try {
            final Connection connectionProxy = constructor.newInstance();
            return (ConnectionProxy) connectionProxy;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }

    private static Constructor<? extends Connection> createConstructorForProxyClass() {
        try {
            return proxyType.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Found no default constructor for proxy class", e);
        }
    }
}
