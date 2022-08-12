INSERT INTO ${schema}.Roles(name) VALUES ('Staff');
INSERT INTO ${schema}.Roles(name) VALUES ('Student');
INSERT INTO ${schema}.Users(name) VALUES ('Ada');
INSERT INTO ${schema}.Users(name) VALUES ('Alva');
INSERT INTO ${schema}.Users(name) VALUES ('Emma');
INSERT INTO ${schema}.USERROLE (user_id, role_id)
  SELECT
    u.ID AS userId,
    r.ID AS roleId
  FROM ${schema}.USERS u, ${schema}.ROLES r
  WHERE (u.NAME = 'Ada' AND r.NAME IN ('Administrator', 'Staff'))
        OR (u.NAME = 'Emma' AND r.NAME = 'Staff')
        OR (u.NAME = 'Alva' AND r.NAME = 'Student');