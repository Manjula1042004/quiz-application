-- File: src/main/resources/data.sql
-- Create ONLY the admin user

use quiz_app;

INSERT IGNORE INTO users (username, email, password, role, enabled) VALUES
('admin', 'admin@quizapp.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', 'ADMIN', 1);

-- In src/main/resources/data.sql
INSERT INTO users (username, email, password, enabled)
VALUES ('testuser', 'test@example.com', 'encodedpassword', true);