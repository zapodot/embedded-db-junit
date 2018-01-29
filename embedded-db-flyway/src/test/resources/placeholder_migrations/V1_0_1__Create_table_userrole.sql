CREATE TABLE ${schema}.UserRole (
  user_id INT NOT NULL,
  role_id INT NOT NULL,
  FOREIGN KEY (user_id) REFERENCES User(id),
  FOREIGN KEY (role_id) REFERENCES Role(id),
  PRIMARY KEY (user_id, role_id)
);
