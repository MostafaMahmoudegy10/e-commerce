INSERT INTO users_category (user_id, category_id)
VALUES
    ((SELECT id FROM users WHERE email = 'ali@test.com'), 1),
    ((SELECT id FROM users WHERE email = 'ali@test.com'), 2),
    ((SELECT id FROM users WHERE email = 'ali@test.com'), 3),
    ((SELECT id FROM users WHERE email = 'ali@test.com'), 4);