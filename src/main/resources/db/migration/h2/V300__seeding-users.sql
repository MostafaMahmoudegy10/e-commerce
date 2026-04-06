INSERT INTO users
(id,first_name, last_name, email, age, is_model, role, gender)
VALUES
    (CAST(RANDOM_UUID() AS BINARY(16)),'Ali','Hassan','ali@test.com',25,true,'user','M'),

    (CAST(RANDOM_UUID() AS BINARY(16)),'Sara','Ahmed','sara@test.com',22,false,'user','F'),

    (CAST(RANDOM_UUID() AS BINARY(16)),'Omar','Mahmoud','omar@test.com',30,true,'brand','M');