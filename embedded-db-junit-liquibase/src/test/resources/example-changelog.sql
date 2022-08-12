--liquibase formatted sql

--changeset zapodot:createUserTable
create table Users
(
    ID      INTEGER IDENTITY PRIMARY KEY,
    NAME    CHARACTER VARYING(255),
    CREATED TIMESTAMP default CURRENT_TIMESTAMP
);
--rollback drop table Uset;

--changeset zapodot:createRolesTable
create table Roles
(
    ID      INTEGER IDENTITY PRIMARY KEY,
    NAME    CHARACTER VARYING(255)
);

--changeset zapodot:createUserRolesTable
CREATE TABLE UserRole (
  user_id INT NOT NULL,
  role_id INT NOT NULL,
  FOREIGN KEY (user_id) REFERENCES Users(id),
  FOREIGN KEY (role_id) REFERENCES Roles(id),
  PRIMARY KEY (user_id, role_id)
);

--changeset zapodot:addInitialRoles context:addUsersAndRoles
INSERT INTO Roles(name) VALUES ('Administrator');
INSERT INTO Roles(name) VALUES ('Staff');
INSERT INTO Roles(name) VALUES ('Student');
--rollback delete from "Role" where name in('Administrator', 'Staff', 'Student');

--changeset zapodot:addInitialUser context:addUsersAndRoles
INSERT INTO Users(name) VALUES ('Ada');
INSERT INTO Users(name) VALUES ('Alva');
INSERT INTO Users(name) VALUES ('Emma');

--changeset zapodot:addInitialUserRoles context:addUsersAndRoles
INSERT INTO USERROLE (user_id, role_id)
  SELECT
    u.ID AS userId,
    r.ID AS roleId
  FROM Users u, Roles r
  WHERE (u.NAME = 'Ada' AND r.NAME IN ('Administrator', 'Staff'))
        OR (u.NAME = 'Emma' AND r.NAME = 'Staff')
        OR (u.NAME = 'Alva' AND r.NAME = 'Student');

--changeset zapodot:addNewSequence dbms:oracle
CREATE SEQUENCE seq_test;