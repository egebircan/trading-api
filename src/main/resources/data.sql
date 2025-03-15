-- user and role data (password for all users: deneme)
INSERT INTO USERS (id, username, password, blocked, active, failed_login_attempts) VALUES
(1, 'user1', '$2a$12$h2/Q8i.M6oXJ3pzOL.GZDeuYaMVQgLA9sI6lsU1ugb.R1L.omn3re', false, true, 0),
(2, 'user2', '$2a$12$h2/Q8i.M6oXJ3pzOL.GZDeuYaMVQgLA9sI6lsU1ugb.R1L.omn3re', false, true, 0),
(3, 'adminuser', '$2a$12$h2/Q8i.M6oXJ3pzOL.GZDeuYaMVQgLA9sI6lsU1ugb.R1L.omn3re', false, true, 0);

INSERT INTO ROLE (id, name) VALUES (1, 'ROLE_ADMIN');
INSERT INTO ROLE (id, name) VALUES (2, 'ROLE_USER');

INSERT INTO USERS_ROLES (user_id, roles_id) VALUES
(1, 2),
(2, 2),
(3, 1);

-- asset data
INSERT INTO ASSETS (id, customer_id, symbol, size, usable_size) VALUES
(100, 1, 'TRY', 1000, 1000),
(101, 2, 'TRY', 1000, 1000);