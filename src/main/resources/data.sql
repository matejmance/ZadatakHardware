INSERT INTO authority (id, authority_name)
SELECT 1, 'ROLE_ADMIN'
    WHERE NOT EXISTS (
    SELECT 1 FROM authority WHERE authority_name = 'ROLE_ADMIN'
);

INSERT INTO authority (id, authority_name)
SELECT 2, 'ROLE_USER'
    WHERE NOT EXISTS (
    SELECT 1 FROM authority WHERE authority_name = 'ROLE_USER'
);

-- Korisnik: user, lozinka: user
INSERT INTO users (username, password)
SELECT 'user',
       'user'
    WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE username = 'user'
);

-- Korisnik: admin, lozinka: admin
INSERT INTO users (username, password)
SELECT 'admin',
       'admin'
    WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE username = 'admin'
);

INSERT INTO users_authority (user_id, authority_id)
SELECT u.id, a.id
FROM users u CROSS JOIN authority a
WHERE u.username = 'user'
  AND a.authority_name = 'ROLE_USER'
  AND NOT EXISTS (
    SELECT 1 FROM users_authority ua
    WHERE ua.user_id = u.id AND ua.authority_id = a.id
);

INSERT INTO users_authority (user_id, authority_id)
SELECT u.id, a.id
FROM users u CROSS JOIN authority a
WHERE u.username = 'admin'
  AND a.authority_name = 'ROLE_ADMIN'
  AND NOT EXISTS (
    SELECT 1 FROM users_authority ua
    WHERE ua.user_id = u.id AND ua.authority_id = a.id
);