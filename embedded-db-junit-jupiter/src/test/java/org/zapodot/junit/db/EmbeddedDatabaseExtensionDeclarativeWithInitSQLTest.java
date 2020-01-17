package org.zapodot.junit.db;


import org.junit.jupiter.api.Test;
import org.zapodot.junit.db.annotations.EmbeddedDatabase;
import org.zapodot.junit.db.annotations.EmbeddedDatabaseTest;
import org.zapodot.junit.db.common.Engine;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;

@EmbeddedDatabaseTest(engine = Engine.H2,
        initialSqls = {"CREATE TABLE Customer(id INTEGER PRIMARY KEY, firstname VARCHAR(512));",
                "INSERT INTO Customer values(1, 'Name');"})
public class EmbeddedDatabaseExtensionDeclarativeWithInitSQLTest {

    @EmbeddedDatabase
    private DataSource dataSource;

    @Test
    void checkIfInitialSqlHasRun() throws SQLException {
        try (final Connection connection = dataSource.getConnection();
             final Statement statement = connection.createStatement();
             final ResultSet resultSet = statement.executeQuery("SELECT id,firstname FROM Customer Where id=1")
        ) {
            assertEquals(true, resultSet.next());
            assertEquals(1, resultSet.getInt("id"));
            assertEquals("Name", resultSet.getString("firstName"));
        }
    }
}
