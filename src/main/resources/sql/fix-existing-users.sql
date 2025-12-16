-- Fix existing users with null values
UPDATE users
SET
    account_locked = COALESCE(account_locked, false),
    login_attempts = COALESCE(login_attempts, 0),
    enabled = COALESCE(enabled, true)
WHERE
    account_locked IS NULL
    OR login_attempts IS NULL
    OR enabled IS NULL;

-- Verify the fix
SELECT username, enabled, login_attempts, account_locked
FROM users;