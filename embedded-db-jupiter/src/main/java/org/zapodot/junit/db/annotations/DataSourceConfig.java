package org.zapodot.junit.db.annotations;

import org.zapodot.junit.db.common.CompatibilityMode;
import org.zapodot.junit.db.common.Engine;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Configuration for the Embedded database server.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface DataSourceConfig {

    /**
     * Engine to use for the embedded database
     *
     * @return {@link Engine} to use (default is H2)
     */
    Engine engine() default Engine.H2;

    /**
     * Compatibility mode to use for the embedded database
     *
     * @return
     */
    CompatibilityMode compatibilityMode() default CompatibilityMode.REGULAR;

    /**
     * A predefined name for the datasource. Is normally not needed
     * @return
     */
    String name() default "";

    boolean autoCommit()  default true;

    /**
     * Properties to be set on the Embedded database server
     *
     * @return
     */
    ConfigurationProperty[] properties() default {};

    /**
     * SQL statements to be run once the embedded database server has been created
     * @return
     */
    String[] initialSqls() default {};

    /**
     * Resources containing SQL to be executed once the embedded database server has been created
     * @return
     */
    String[] initialSqlResources() default {};

}
