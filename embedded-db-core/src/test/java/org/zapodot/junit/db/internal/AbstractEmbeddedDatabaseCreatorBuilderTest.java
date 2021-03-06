package org.zapodot.junit.db.internal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zapodot.junit.db.common.CompatibilityMode;
import org.zapodot.junit.db.common.EmbeddedDatabaseCreator;
import org.zapodot.junit.db.common.Engine;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class AbstractEmbeddedDatabaseCreatorBuilderTest {

    @Mock
    private Connection connection;

    @Mock
    private DataSource dataSource;

    @Test
    public void buildUsingNullCompatibilityString() {
        assertThrows(IllegalArgumentException.class, () -> new InternalEmbeddedDatabaseCreatorBuilder(Engine.H2).withMode((String) null));
    }

    @Test
    public void buildUsingIllegalCompatibilityString() {
        assertThrows(IllegalArgumentException.class, () -> new InternalEmbeddedDatabaseCreatorBuilder(Engine.H2).withMode("STUFF"));
    }

    @Test
    public void buildUsingLegalCompatibilityString() {
        assertNotNull(new InternalEmbeddedDatabaseCreatorBuilder(Engine.H2).withMode(CompatibilityMode.DB2.name()));
    }

    @Test
    public void buildUsingEmptyCompatibilityString() {
        assertNotNull(new InternalEmbeddedDatabaseCreatorBuilder(Engine.H2).withMode(""));
    }

    @Test
    public void buildUsingNullCompatibilityEnumValue() {
        assertThrows(IllegalArgumentException.class, () -> new InternalEmbeddedDatabaseCreatorBuilder(Engine.H2).withMode((CompatibilityMode) null));
    }

    @Test
    public void jdbcUrlCreatorH2() {
        assertEquals(H2JdbcUrlFactory.class,
                     new InternalEmbeddedDatabaseCreatorBuilder(Engine.H2).getH2JdbcUrlFactory().getClass());
    }

    @Test
    public void jdbcUrlCreatorHyperSQL() {
        assertEquals(HyperSqlJdbcUrlFactory.class,
                     new InternalEmbeddedDatabaseCreatorBuilder(Engine.HSQLDB).getH2JdbcUrlFactory().getClass());
    }

    @Test
    public void withInitialSqlFromResourceNullResource() {
        assertThrows(IllegalArgumentException.class, () -> new InternalEmbeddedDatabaseCreatorBuilder(Engine.HSQLDB).withInitialSqlFromResource(null));
    }

    @Test
    public void withInitialSqlFromResourceAndCharsetNullResource() {
        assertThrows(IllegalArgumentException.class, () -> new InternalEmbeddedDatabaseCreatorBuilder(Engine.HSQLDB).withInitialSqlFromResource(null, null));
    }

    @Test
    public void withInitialSqlFromResourceAndCharsetNullCharset() {
        assertThrows(IllegalArgumentException.class, () -> new InternalEmbeddedDatabaseCreatorBuilder(Engine.HSQLDB)
                              .withInitialSqlFromResource("classpath:file.sql", null));
    }

    @Test
    public void withInitialSqlFromResourceFound() {
        assertEquals(1, new InternalEmbeddedDatabaseCreatorBuilder(Engine.HSQLDB)
                .withInitialSqlFromResource("classpath:initial.sql").initializationPlugins.size());
    }

    @Test
    public void withInitialSqlNull() {
        assertThrows(IllegalArgumentException.class, () -> new InternalEmbeddedDatabaseCreatorBuilder(Engine.H2).withInitialSql(null));
    }

    @Test
    public void withInitialSql() {
        assertEquals(1,
                     new InternalEmbeddedDatabaseCreatorBuilder(Engine.H2)
                             .withInitialSql("CREATE TABLE A(B varchar(255))").initializationPlugins.size());
    }

    @Test
    public void withNoEngine() {
        assertThrows(IllegalArgumentException.class, () -> new InternalEmbeddedDatabaseCreatorBuilder(null));
    }

    @Test
    public void propertiesMap() {
        assertEquals(0, new InternalEmbeddedDatabaseCreatorBuilder(Engine.H2).propertiesMap().size());
    }

    @Test
    public void buildUsingALotOfParams() {
        final String jdbcUrl = "url";
        final InternalEmbeddedDatabaseCreator databaseCreator = new InternalEmbeddedDatabaseCreatorBuilder(Engine.H2)
                .withConnection(connection)
                .withDataSource(dataSource)
                .withJdbcUrl(jdbcUrl)
                .withMode(CompatibilityMode.REGULAR)
                .withName("name")
                .withoutAutoCommit()
                .withProperty("setting", "value")
                .withInitialSqlFromResource("classpath:initial.sql", StandardCharsets.UTF_8)
                .build();
        assertSame(connection, databaseCreator.getConnection());
        assertSame(dataSource, databaseCreator.getDataSource());
        assertEquals(jdbcUrl, databaseCreator.getConnectionJdbcUrl());
    }

    private static class InternalEmbeddedDatabaseCreator implements EmbeddedDatabaseCreator {
        private final Connection connection;

        private final DataSource dataSource;

        private final String jdbcUrl;

        private InternalEmbeddedDatabaseCreator(final Connection connection,
                                                final DataSource dataSource,
                                                final String jdbcUrl) {
            this.connection = connection;
            this.dataSource = dataSource;
            this.jdbcUrl = jdbcUrl;
        }

        @Override
        public Connection getConnection() {
            return connection;
        }

        @Override
        public DataSource getDataSource() {
            return dataSource;
        }

        @Override
        public boolean isAutoCommit() {
            return false;
        }

        @Override
        public String getConnectionJdbcUrl() {
            return jdbcUrl;
        }
    }

    private static class InternalEmbeddedDatabaseCreatorBuilder extends AbstractEmbeddedDatabaseCreatorBuilder<InternalEmbeddedDatabaseCreator> {
        private Connection connection;

        private DataSource dataSource;

        private String jdbcUrl;

        public InternalEmbeddedDatabaseCreatorBuilder(final Engine engine) {
            super(engine);
        }

        public InternalEmbeddedDatabaseCreatorBuilder withConnection(final Connection connection) {
            this.connection = connection;
            return this;
        }

        public InternalEmbeddedDatabaseCreatorBuilder withDataSource(final DataSource dataSource) {
            this.dataSource = dataSource;
            return this;
        }

        public InternalEmbeddedDatabaseCreatorBuilder withJdbcUrl(final String jdbcUrl) {
            this.jdbcUrl = jdbcUrl;
            return this;
        }

        public JdbcUrlFactory getH2JdbcUrlFactory() {
            return super.createJdbcUrlFactory();
        }


        @Override
        public InternalEmbeddedDatabaseCreator build() {
            return new InternalEmbeddedDatabaseCreator(connection, dataSource, jdbcUrl);
        }
    }
}