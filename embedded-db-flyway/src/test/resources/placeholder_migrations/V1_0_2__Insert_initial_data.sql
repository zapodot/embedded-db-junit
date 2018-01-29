INSERT INTO ${schema}.Role(name) VALUES ('Staff');
INSERT INTO ${schema}.Role(name) VALUES ('Student');
INSERT INTO ${schema}.User(name) VALUES ('Ada');
INSERT INTO ${schema}.User(name) VALUES ('Alva');
INSERT INTO ${schema}.User(name) VALUES ('Emma');
INSERT INTO ${schema}.USERROLE (user_id, role_id)
  SELECT
    u.ID AS userId,
    r.ID AS roleId
  FROM ${schema}.USER u, ${schema}.ROLE r
  WHERE (u.NAME = 'Ada' AND r.NAME IN ('Administrator', 'Staff'))
        OR (u.NAME = 'Emma' AND r.NAME = 'Staff')
        OR (u.NAME = 'Alva' AND r.NAME = 'Student');