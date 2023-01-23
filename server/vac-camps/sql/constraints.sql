    -- 1nd Condition -- 
-- This query should return one row --
SELECT COUNT(*) FROM certificate_of_vaccination
WHERE user_id = ?;
--EOQ--

-- This query will be triggerd if and onl if the above return one row--
-- This query also should return one row
SELECT COUNT(*) FROM registration
WHERE dosage_count = 1 and 
user_id = ? and 
status = 'Processed' and 
date_of_vaccination < CURRENT_DATE - INTERVAL '45 day';
--EOQ--

    -- 2nd Condition -- 
-- First check the availability of stock in the city --
-- Will check the stock column from the ResultSet
SELECT * FROM camps
WHERE city_code = ? and status = 'active';

-- If the above query satisfies then check the slots availability --
-- Below should return less than 10
SELECT COUNT(*)  FROM registration 
WHERE choosen_camp_id = ? and 
status = 'active' and choosen_slot_id = ?
and date_of_vaccination = ?;
