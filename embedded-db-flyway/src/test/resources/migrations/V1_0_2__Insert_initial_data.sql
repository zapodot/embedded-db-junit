INSERT INTO Roles(name) VALUES ('Staff');
INSERT INTO Roles(name) VALUES ('Student');
INSERT INTO Users(name) VALUES ('Ada');
INSERT INTO Users(name) VALUES ('Alva');
INSERT INTO Users(name) VALUES ('Emma');
INSERT INTO USERROLE (user_id, role_id)
  SELECT
    u.ID AS userId,
    r.ID AS roleId
  FROM Users u, Roles r
  WHERE (u.NAME = 'Ada' AND r.NAME IN ('Administrator', 'Staff'))
        OR (u.NAME = 'Emma' AND r.NAME = 'Staff')
        OR (u.NAME = 'Alva' AND r.NAME = 'Student');