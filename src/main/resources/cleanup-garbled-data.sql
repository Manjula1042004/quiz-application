-- Clean up ALL corrupted questions and options
DELETE FROM attempt_answers WHERE question_id IN (
    SELECT id FROM questions
    WHERE question_text REGEXP '[a-z]{4,}'
    OR question_text LIKE 'mbnb%'
    OR question_text LIKE 'nnmm%'
    OR question_text LIKE 'ssddddddd%'
);

DELETE FROM question_options WHERE question_id IN (
    SELECT id FROM questions
    WHERE question_text REGEXP '[a-z]{4,}'
    OR question_text LIKE 'mbnb%'
    OR question_text LIKE 'nnmm%'
    OR question_text LIKE 'ssddddddd%'
);

DELETE FROM questions WHERE question_text REGEXP '[a-z]{4,}'
OR question_text LIKE 'mbnb%'
OR question_text LIKE 'nnmm%'
OR question_text LIKE 'ssddddddd%';

-- Insert clean sample questions
INSERT IGNORE INTO questions (question_text, correct_answer_index, difficulty_level, explanation, points, quiz_id) VALUES
('What is the capital of France?', 1, 'EASY', 'Paris is the capital and most populous city of France', 1, 18),
('Which programming language is known for its use in web development?', 0, 'EASY', 'JavaScript is primarily used for client-side web development', 1, 18);

-- Get the IDs of newly inserted questions
SET @q1_id = (SELECT id FROM questions WHERE question_text = 'What is the capital of France?');
SET @q2_id = (SELECT id FROM questions WHERE question_text = 'Which programming language is known for its use in web development?');

-- Insert clean options
INSERT IGNORE INTO question_options (question_id, option_text, option_order) VALUES
(@q1_id, 'London', 0),
(@q1_id, 'Paris', 1),
(@q1_id, 'Berlin', 2),
(@q1_id, 'Madrid', 3),

(@q2_id, 'JavaScript', 0),
(@q2_id, 'Java', 1),
(@q2_id, 'Python', 2),
(@q2_id, 'C++', 3);