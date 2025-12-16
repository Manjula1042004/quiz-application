-- File: src/main/resources/cleanup-data.sql
-- Run this to fix garbled text in your database

-- Clean up corrupted questions and options
DELETE FROM attempt_answers WHERE question_id IN (
    SELECT id FROM questions WHERE question_text LIKE 'mbnb%' OR question_text LIKE 'nnmm%'
);

DELETE FROM question_options WHERE question_id IN (
    SELECT id FROM questions WHERE question_text LIKE 'mbnb%' OR question_text LIKE 'nnmm%'
);

DELETE FROM questions WHERE question_text LIKE 'mbnb%' OR question_text LIKE 'nnmm%';

-- Insert clean questions with proper options
INSERT IGNORE INTO questions (question_text, correct_answer_index, difficulty_level, explanation, points, quiz_id) VALUES
('Which keyword is used to define a constant in Java?', 1, 'EASY', 'The final keyword is used to define constants in Java', 1, 1),
('What is the default value of a boolean variable in Java?', 1, 'EASY', 'Boolean variables default to false in Java', 1, 1);

-- Get the IDs of the newly inserted questions
SET @q1_id = (SELECT id FROM questions WHERE question_text = 'Which keyword is used to define a constant in Java?');
SET @q2_id = (SELECT id FROM questions WHERE question_text = 'What is the default value of a boolean variable in Java?');

-- Insert clean options
INSERT IGNORE INTO question_options (question_id, option_text, option_order) VALUES
(@q1_id, 'const', 0),
(@q1_id, 'final', 1),
(@q1_id, 'static', 2),
(@q1_id, 'constant', 3),

(@q2_id, 'true', 0),
(@q2_id, 'false', 1),
(@q2_id, 'null', 2),
(@q2_id, '0', 3);