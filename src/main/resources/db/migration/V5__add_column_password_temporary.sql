ALTER TABLE users
    ADD COLUMN password_temporary BOOLEAN DEFAULT FALSE;

UPDATE Users
SET password_temporary = false
WHERE id = 1;

