CREATE TABLE ${schema}.UserRole (
  user_id INT NOT NULL,
  role_id INT NOT NULL,
  FOREIGN KEY (user_id) REFERENCES Users(id),
  FOREIGN KEY (role_id) REFERENCES Roles(id),
  PRIMARY KEY (user_id, role_id)
);
