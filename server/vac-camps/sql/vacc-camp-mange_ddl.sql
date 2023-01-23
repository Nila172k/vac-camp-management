/* VACCINATION CAMP MANAGEMWNT
*  DESC - DDL commands for Vaccination camp management
*  AUTHOR - VENNILA
*/

CREATE DATABASE  vaccination_camp_management;

/**
-- Holds the data about the Organizations --
*/
CREATE SEQUENCE org_id_seq;
--EOQ--

ALTER SEQUENCE org_id_seq RESTART WITH 300;
--EOQ--

CREATE TABLE IF NOT EXISTS organizations(
  id smallint NOT NULL DEFAULT nextval('org_id_seq'),
  name VARCHAR(100) NOT NULL,
  user_name VARCHAR(30) NOT NULL,
  hashed_password VARCHAR NOT NULL,
  created_date DATE DEFAULT CURRENT_DATE,
  state VARCHAR DEFAULT 'active' NOT NULL,
  PRIMARY KEY (id)
);
--EOQ--


/**
-- Holds the data about the admin
*/

CREATE SEQUENCE logins_id_seq;
--EOQ--

ALTER SEQUENCE logins_id_seq RESTART WITH 1000;
--EOQ--

CREATE TABLE IF NOT EXISTS logins(
    id smallint NOT NULL DEFAULT nextval('logins_id_seq'),
    user_name VARCHAR(30) NOT NULL,
    hashed_password VARCHAR NOT NULL,
    org_id smallint,
    created_date DATE DEFAULT CURRENT_DATE,
    state VARCHAR DEFAULT 'active' NOT NULL,
    PRIMARY KEY(id),
    FOREIGN KEY (org_id)
        REFERENCES organizations(id)
);
--EOQ--

/**
    -- Holds the data about the camping cities
*/

CREATE SEQUENCE city_id_seq;
--EOQ--

CREATE TABLE IF NOT EXISTS cities(
    id BIGINT NOT NULL DEFAULT nextval('city_id_seq'),
    name VARCHAR UNIQUE NOT NULL,
    state VARCHAR NOT NULL,
    created_date DATE DEFAULT CURRENT_DATE,
    PRIMARY KEY(id)
);
--EOQ--

/**
    -- Holds the data about the camping sites -- 
*/

CREATE SEQUENCE camp_id_seq;
--EOQ--

ALTER SEQUENCE camp_id_seq RESTART WITH 2000;
--EOQ--

CREATE TABLE IF NOT EXISTS camps(
    id BIGINT NOT NULL DEFAULT nextval('camp_id_seq'),
    organized_by smallint NOT NULL,
    city_id BIGINT NOT NULL,
    address TEXT NOT NULL,
    stock BIGINT NOT NULL, 
    created_date DATE DEFAULT CURRENT_DATE,
    state VARCHAR DEFAULT 'active' NOT NULL,
    PRIMARY KEY(id),
    FOREIGN KEY(organized_by)
        REFERENCES organizations(id)
);
--EOQ--

/**
    -- Holds the data about the users
*/

CREATE SEQUENCE user_id_seq;
--EOQ--

ALTER SEQUENCE user_id_seq RESTART WITH 70000;
--EOQ--

CREATE TABLE IF NOT EXISTS users(
    id BIGINT NOT NULL DEFAULT nextval('user_id_seq'),
    first_name VARCHAR NOT NULL,
    last_name VARCHAR(100),
    gender VARCHAR(10) NOT NULL,
    dob DATE, 
    phone_number VARCHAR(20),
    aadhar_number BIGINT UNIQUE NOT NULL,
    email VARCHAR(100) NOT NULL,
    hashed_password VARCHAR NOT NULL,
    created_date DATE NOT NULL DEFAULT CURRENT_DATE,
    state VARCHAR(30) NOT NULL DEFAULT 'active',
    PRIMARY KEY(id)
);
--EOQ--

/**
    -- Holds the data about the valid vaccination slots
*/

CREATE SEQUENCE slot_id_seq;
--EOQ--

CREATE TABLE IF NOT EXISTS slots(
    id smallint NOT NULL DEFAULT nextval('slot_id_seq'),
    time varchar NOT NULL,
    PRIMARY KEY(id)
);
--EOQ--

/**
    -- Holds the data about the vaccination registration
*/

CREATE SEQUENCE reg_id_seq;
--EOQ--

CREATE TABLE IF NOT EXISTS registrations (
    id BIGINT NOT NULL DEFAULT nextval('reg_id_seq'),
    user_id BIGINT NOT NULL,
    chosen_camp_id BIGINT NOT NULL,
    date_of_vaccination DATE NOT NULL,
    chosen_slot_id SMALLINT NOT NULL,
    dosage_count smallint NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'Inprogress',
    PRIMARY KEY(id),
    FOREIGN KEY(chosen_camp_id)
        REFERENCES camps(id),
    FOREIGN KEY(chosen_slot_id)
        REFERENCES slots(id)
);
--EOQ--

/**
    --Holds the info of vaccination certification 
*/

CREATE SEQUENCE certificate_id_seq;
--EOQ--

CREATE TABLE IF NOT EXISTS certificate_of_vaccination(
    id BIGINT NOT NULL DEFAULT nextval('certificate_id_seq'),
    reg_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY(id),
    FOREIGN KEY(reg_id)
        REFERENCES registrations(id),
    FOREIGN KEY(user_id)
        REFERENCES users(id)
);
--EOQ--
/**
 * Holds the info of the user's/admin's activity
 */

CREATE SEQUENCE audit_id_seq;
--EOQ--

CREATE TABLE IF NOT EXISTS audit(
	id BIGINT NOT NULL DEFAULT nextval('audit_id_seq'),
	user_id BIGINT NOT NULL,
	user_type VARCHAR(30) NOT NULL,
	operation VARCHAR(100) NOT NULL,
	operation_desc VARCHAR,
	created_date DATE DEFAULT CURRENT_DATE,
	created_time TIME DEFAULT CURRENT_TIME, 
	status VARCHAR(30),
	PRIMARY KEY(id)
);
--EOQ--