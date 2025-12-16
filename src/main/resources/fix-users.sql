-- Enable all existing users who are disabled (for testing purposes)
UPDATE users SET enabled = TRUE WHERE enabled = FALSE;

-- Verify the changes
SELECT username, email, enabled, role FROM users;