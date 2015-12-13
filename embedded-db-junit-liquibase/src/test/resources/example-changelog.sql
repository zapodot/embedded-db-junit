--liquibase formatted sql

--changeset zapodot:createUserTable
CREATE TABLE User (
  id      INT IDENTITY PRIMARY KEY,
  name    VARCHAR(255),
  created TIMESTAMP NOT NULL DEFAULT (CURRENT_TIMESTAMP)
);
--rollback drop table Uset;

--changeset zapodot:createRolesTable
CREATE TABLE Role (
  id  INT IDENTITY PRIMARY KEY,
  name VARCHAR(255)
);

--changeset zapodot:createUserRolesTable
CREATE TABLE UserRole (
  user_id INT NOT NULL,
  role_id INT NOT NULL,
  FOREIGN KEY (user_id) REFERENCES User(id),
  FOREIGN KEY (role_id) REFERENCES Role(id),
  PRIMARY KEY (user_id, role_id)
);

--changeset zapodot:addInitialRoles context:addUsersAndRoles
INSERT INTO Role(name) VALUES ('Administrator');
INSERT INTO Role(name) VALUES ('Staff');
INSERT INTO Role(name) VALUES ('Student');
--rollback delete from Role where name in('Administrator', 'Staff', 'Student');

--changeset zapodot:addInitialUser context:addUsersAndRoles
INSERT INTO User(name) VALUES ('Ada');
INSERT INTO User(name) VALUES ('Alva');
INSERT INTO User(name) VALUES ('Emma');

--changeset zapodot:addInitialUserRoles context:addUsersAndRoles
INSERT INTO USERROLE (user_id, role_id)
  SELECT
    u.ID AS userId,
    r.ID AS roleId
  FROM USER u, ROLE r
  WHERE (u.NAME = 'Ada' AND r.NAME IN ('Administrator', 'Staff'))
        OR (u.NAME = 'Emma' AND r.NAME = 'Staff')
        OR (u.NAME = 'Alva' AND r.NAME = 'Student');

--changeset zapodot:addNewSequence dbms:oracle
CREATE SEQUENCE seq_test;