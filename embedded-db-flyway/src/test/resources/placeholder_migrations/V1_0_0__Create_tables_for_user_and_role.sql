CREATE TABLE ${schema}.User (
  id      INT IDENTITY PRIMARY KEY,
  name    VARCHAR(255),
  created TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE TABLE ${schema}.Role (
  id  INT IDENTITY PRIMARY KEY,
  name VARCHAR(255)
);