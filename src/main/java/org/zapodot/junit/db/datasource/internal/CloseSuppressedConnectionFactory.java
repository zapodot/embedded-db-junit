package org.zapodot.junit.db.datasource.internal;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.ClassLoadingStrategy;
import net.bytebuddy.instrumentation.FieldAccessor;
import net.bytebuddy.instrumentation.MethodDelegation;
import net.bytebuddy.modifier.Visibility;
import sun.reflect.ReflectionFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;

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

    public static Connection createProxy(final Connection connection) {
        return (Connection) createConnectionProxy(connection);

    }

    private static ConnectionProxy createConnectionProxy(final Connection connection) {
        final ConnectionProxy proxy = createProxyInstance();
        proxy.setDelegatedConnection(connection);
        return proxy;
    }

    private static ConnectionProxy createProxyInstance() {
        final Constructor<?> constructor = createConstructorForProxyClass();
        try {
            return (ConnectionProxy) constructor.newInstance();
        } catch (InstantiationException e) {
            throw new IllegalStateException(e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }

    private static Constructor<?> createConstructorForProxyClass() {
        try {
            return ReflectionFactory.getReflectionFactory()
                                    .newConstructorForSerialization(proxyType,
                                                                    Object.class.getDeclaredConstructor());
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }
}
