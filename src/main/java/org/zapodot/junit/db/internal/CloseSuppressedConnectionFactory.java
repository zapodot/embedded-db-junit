package org.zapodot.junit.db.internal;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.ClassLoadingStrategy;
import net.bytebuddy.instrumentation.FieldAccessor;
import net.bytebuddy.instrumentation.MethodDelegation;
import net.bytebuddy.modifier.Visibility;
import sun.reflect.ReflectionFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.util.Objects;

import static net.bytebuddy.matcher.ElementMatchers.any;

public class CloseSuppressedConnectionFactory {

    private static Class<? extends Connection> proxyType = new ByteBuddy().subclass(Connection.class)
                                                                          .method(any())
                                                                          .intercept(MethodDelegation.to(
                                                                                  ConnectionInterceptor.class))
                                                                          .defineField("delegatedConnection",
                                                                                       Connection.class,
                                                                                       Visibility.PRIVATE)
                                                                          .implement(ConnectionProxy.class)
                                                                          .intercept(FieldAccessor.ofBeanProperty())
                                                                          .make()
                                                                          .load(CloseSuppressedConnectionFactory.class
                                                                                        .getClassLoader(),
                                                                                ClassLoadingStrategy.Default.WRAPPER)
                                                                          .getLoaded();

    /**
     * Create a proxy that delegates to the provided Connection except for calls to "close()" which will be suppressed.
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
        final Constructor<ConnectionProxy> constructor = createConstructorForProxyClass();
        try {
            return constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }

    private static Constructor<ConnectionProxy> createConstructorForProxyClass() {
        try {
            return (Constructor<ConnectionProxy>) ReflectionFactory.getReflectionFactory()
                                    .newConstructorForSerialization(proxyType,
                                                                    Object.class.getDeclaredConstructor());
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }
}
