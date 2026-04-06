INSERT INTO users_category (user_id, category_id)
SELECT u.id, c.id
FROM users u
         JOIN category c ON c.category_name IN ('MEN','WOMEN','SHOES','SPORT')
WHERE u.email = 'ali@test.com';