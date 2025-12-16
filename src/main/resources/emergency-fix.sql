-- EMERGENCY FIX: Enable all users for immediate access
UPDATE users SET enabled = TRUE WHERE enabled = FALSE;

-- Verify all users are enabled
SELECT username, email, enabled, role FROM users;