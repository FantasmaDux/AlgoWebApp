CREATE TABLE IF NOT EXISTS patterns (
    id SERIAL PRIMARY KEY,
    name VARCHAR(128) NOT NULL UNIQUE,
    description TEXT NOT NULL,
    example TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS tasks (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    number INT NOT NULL,
    pattern_id INT NOT NULL REFERENCES patterns (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS tests (
    id SERIAL PRIMARY KEY,
    input TEXT NOT NULL,
    expected TEXT NOT NULL,
    task_id INT NOT NULL REFERENCES tasks (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS solution (
    id BIGSERIAL PRIMARY KEY,
    task_id INT NOT NULL,
    user_id UUID NOT NULL,
    solution TEXT NOT NULL,
    status VARCHAR(32) NOT NULL,
    submitted_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS solution_user_task_idx ON solution (user_id, task_id);

INSERT INTO patterns (name, description, example)
VALUES
    ('Two Pointers', 'Используем левый и правый указатели для прохода по данным', 'while (left < right) { ... }'),
    ('Sliding Window', 'Двигаем окно по массиву и поддерживаем состояние', 'for (right = 0; right < n; right++) { ... }')
ON CONFLICT (name) DO NOTHING;

INSERT INTO tasks (name, description, number, pattern_id)
SELECT x.name, x.description, x.number, p.id
FROM (
    VALUES
      ('Valid Palindrome', 'Проверить, является ли строка палиндромом', 125, 'Two Pointers'),
      ('Two Sum II', 'Найти индексы пары в отсортированном массиве', 167, 'Two Pointers'),
      ('Longest Substring Without Repeating Characters', 'Найти длину максимальной подстроки без повторений', 3, 'Sliding Window')
) AS x(name, description, number, pattern_name)
JOIN patterns p ON p.name = x.pattern_name
WHERE NOT EXISTS (
    SELECT 1 FROM tasks t WHERE t.number = x.number
);

INSERT INTO tests (input, expected, task_id)
SELECT y.input, y.expected, t.id
FROM (
    VALUES
      ('"A man, a plan, a canal: Panama"', 'true', 125),
      ('[2,7,11,15], target=9', '[1,2]', 167),
      ('"abcabcbb"', '3', 3)
) AS y(input, expected, task_number)
JOIN tasks t ON t.number = y.task_number
WHERE NOT EXISTS (
    SELECT 1 FROM tests tt WHERE tt.task_id = t.id AND tt.input = y.input
);
