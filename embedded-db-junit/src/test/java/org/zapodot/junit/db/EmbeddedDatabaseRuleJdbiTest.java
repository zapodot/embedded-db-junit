package org.zapodot.junit.db;

import org.junit.Rule;
import org.junit.Test;
import org.skife.jdbi.v2.*;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author zapodot
 */
public class EmbeddedDatabaseRuleJdbiTest {

    public static class Employee {

        public final Long id;
        public final String firstName;
        public final String lastName;

        public Employee() {
            this(null, null, null);
        }

        public Employee(final Long id, final String firstName, final String lastName) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public Employee(final String firstName, final String lastName) {
            this(null, firstName, lastName);
        }

    }


    @Rule
    public EmbeddedDatabaseRule embeddedDatabaseRule = EmbeddedDatabaseRule.builder()
            .withInitialSql("CREATE TABLE EMPLOYEE(id IDENTITY PRIMARY KEY, firstName varchar(512) NOT NULL, lastName varchar(512) NOT NULL)")
            .build();

    @Test
    public void testJdbi() throws Exception {
        final DBI dbi = new DBI(embeddedDatabaseRule.getDataSource());
        final Employee initialEmployee = new Employee("John", "Doe");
        try(final Handle handle = dbi.open()) {
            final Update statement = handle.createStatement("insert into EMPLOYEE (firstName, lastName) values (:firstName, :lastName)");
            statement.bind("firstName", initialEmployee.firstName);
            statement.bind("lastName", initialEmployee.lastName);
            final Long id = statement.executeAndReturnGeneratedKeys(
                    (i, resultSet, statementContext) -> resultSet.getLong(1)).first();
            assertEquals(1L, id.longValue());

            final List<Employee> employeeList = handle.createQuery("SELECT * from EMPLOYEE").map(new ReflectionBeanMapper<>(Employee.class)).list();
            assertEquals(1, employeeList.size());

            final Employee persistedEmployee = employeeList.iterator().next();
            assertEquals(1L, persistedEmployee.id.longValue());
            assertEquals(initialEmployee.firstName, persistedEmployee.firstName);
            assertEquals(initialEmployee.lastName, persistedEmployee.lastName);
        }

    }
}
