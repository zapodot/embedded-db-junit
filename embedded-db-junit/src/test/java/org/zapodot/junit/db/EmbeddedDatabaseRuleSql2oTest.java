package org.zapodot.junit.db;

import org.junit.Rule;
import org.junit.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import static org.junit.Assert.assertEquals;

public class EmbeddedDatabaseRuleSql2oTest {

    public static class Element {
        public final Long id;

        public final String name;

        public Element(final Long id, final String name) {
            this.id = id;
            this.name = name;
        }
    }

    @Rule
    public final EmbeddedDatabaseRule databaseRule = EmbeddedDatabaseRule
            .builder()
            .withInitialSql("CREATE TABLE ELEMENT(id IDENTITY PRIMARY KEY, name VARCHAR(512) NOT NULL)")
            .withoutAutoCommit()
            .build();

    @Test
    public void testUsingSql2oWithTransaction() throws Exception {
        final Sql2o sql2o = new Sql2o(databaseRule.getDataSource());
        final String elementName = "element name";
        try (final Connection transaction = sql2o.beginTransaction()) {
            final Long id = transaction
                    .createQuery("INSERT INTO ELEMENT(name) values (:name)")
                    .addParameter("name", elementName)
                    .executeUpdate()
                    .getKey(Long.class);
            assertEquals(1L, id.longValue());
            transaction.commit();
        }
        try (final Connection read = sql2o.open()) {
            final Element element = read
                    .createQuery("SELECT * from ELEMENT")
                    .executeAndFetch(Element.class)
                    .iterator()
                    .next();
            assertEquals(1L, element.id.longValue());
            assertEquals(elementName, element.name);

        }

    }
}