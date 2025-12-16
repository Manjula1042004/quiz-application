-- File: src/main/resources/clean-options-data.sql
-- Clean up ALL corrupted options
DELETE FROM question_options WHERE option_text REGEXP '[a-z]{4,}'
   OR option_text LIKE 'mbnb%'
   OR option_text LIKE 'nnmm%'
   OR option_text LIKE 'dggh%'
   OR option_text LIKE 'fgghh%'
   OR LENGTH(option_text) < 2;

-- Reset correct answer indices for affected questions
UPDATE questions q
SET correct_answer_index = 0
WHERE correct_answer_index >= (
    SELECT COUNT(*) FROM question_options o
    WHERE o.question_id = q.id
);

-- Verify the cleanup
SELECT q.id, q.question_text,
       COUNT(o.id) as option_count,
       GROUP_CONCAT(o.option_text ORDER BY o.option_order) as options
FROM questions q
LEFT JOIN question_options o ON q.id = o.question_id
GROUP BY q.id
HAVING option_count < 2 OR option_count > 10;