INSERT INTO organizations (name, user_name, hashed_password) VALUES ('TestMediCare', 'testAdmin', 'testAdmin312');
--EOQ--

INSERT INTO logins (user_name, hashed_password, org_id ) VALUES ('admin', 'Adminadmin', 301);
--EOQ--

INSERT INTO camps(city_id, address, organized_by, stock) 
VALUES (341, 'Mahatma Gandhi Rd, Potheri, SRM Nagar, Village District, Kattankulathur, Tamil Nadu 603203', 300, 5000);
--EOQ--

INSERT INTO users(first_name, last_name, phone_number, aadhar_number, email, hashed_password) 
VALUES ('Vennila', 'T', '9876543210', '123456789012', 'v@gmail.com', '1234!');
--EOQ--

INSERT INTO slots(time) 
VALUES
('09:00 AM to 11:00 AM'),
('12:00 PM to 02:00 PM'),
('03:00 PM to 05:00 PM');
--EOQ--

INSERT INTO registration (user_id, choosen_camp_id, date_of_vaccination, choosen_slot_id, dosage_count)
VALUES
(7000, 2000, CURRENT_DATE, 1, 1);
--EOQ--

INSERT INTO certificate_of_vaccination (reg_id, user_id)
VALUES
(1, 70001);