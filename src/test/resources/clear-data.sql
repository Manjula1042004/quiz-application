-- Clear tables in correct order to avoid foreign key constraints
DELETE FROM attempt_answers;
DELETE FROM quiz_attempts;
DELETE FROM question_options;
DELETE FROM question_tags;
DELETE FROM questions;
DELETE FROM quizzes;
DELETE FROM categories;
DELETE FROM tags;
DELETE FROM email_verification_tokens;
DELETE FROM password_reset_tokens;
DELETE FROM two_factor_auth;
DELETE FROM users;


