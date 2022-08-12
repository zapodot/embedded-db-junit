package org.zapodot.junit.db.plugin.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class RoleDao {

    private final Connection connection;

    public RoleDao(final Connection connection) {
        this.connection = connection;
    }

    public List<String> rolesForUser(final String userName) throws SQLException {
        try(final PreparedStatement statement = connection.prepareStatement("Select * FROM ROLES r INNER JOIN USERROLE ur on r.ID = ur.ROLE_ID INNER JOIN USERS u on ur.USER_ID = u.ID where u.NAME = ?")) {
            statement.setString(1, userName);
            try(final ResultSet resultSet = statement.executeQuery()) {
                final List<String> roles = new LinkedList<>();
                while(resultSet.next()) {
                    roles.add(resultSet.getString("name"));
                }
                return roles;
            }
        }
    }
}
