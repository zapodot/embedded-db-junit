INSERT INTO Role(name) VALUES ('Staff');
INSERT INTO Role(name) VALUES ('Student');
INSERT INTO User(name) VALUES ('Ada');
INSERT INTO User(name) VALUES ('Alva');
INSERT INTO User(name) VALUES ('Emma');
INSERT INTO USERROLE (user_id, role_id)
  SELECT
    u.ID AS userId,
    r.ID AS roleId
  FROM USER u, ROLE r
  WHERE (u.NAME = 'Ada' AND r.NAME IN ('Administrator', 'Staff'))
        OR (u.NAME = 'Emma' AND r.NAME = 'Staff')
        OR (u.NAME = 'Alva' AND r.NAME = 'Student');