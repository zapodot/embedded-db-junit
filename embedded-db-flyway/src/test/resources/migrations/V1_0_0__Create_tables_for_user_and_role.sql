create table "User"
(
    ID      INTEGER auto_increment,
    NAME    CHARACTER VARYING(255),
    CREATED TIMESTAMP default CURRENT_TIMESTAMP,
    constraint PK_USER_ID
        primary key (ID)
);
create table "Role"
(
    ID      INTEGER auto_increment,
    NAME    CHARACTER VARYING(255),
    constraint PK_ROLE_ID
        primary key (ID)
);
