package org.zapodot.junit.db.annotations;

import org.junit.jupiter.api.extension.ExtendWith;
import org.zapodot.junit.db.EmbeddedDatabaseExtension;
import org.zapodot.junit.db.common.CompatibilityMode;
import org.zapodot.junit.db.common.Engine;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@ExtendWith(EmbeddedDatabaseExtension.class)
public @interface EmbeddedDatabaseTest {

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
