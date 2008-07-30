DROP DATABASE addressbook;
CREATE DATABASE addressbook;
USE addressbook;


DROP TABLE IF EXISTS address;
CREATE TABLE address (
  address_id bigint NOT NULL AUTO_INCREMENT,
  label varchar(40) NOT NULL,
  address_line_1 varchar(255) NOT NULL,
  address_line_2 varchar(255) DEFAULT NULL,
  address_line_3 varchar(255) DEFAULT NULL,
  city varchar(100) NOT NULL,
  state varchar(2) NOT NULL,
  zip varchar(20) NOT NULL,
  contact_id bigint NOT NULL,
  version bigint NOT NULL,
  PRIMARY KEY (address_id)
) ENGINE=InnoDB;

DROP TABLE IF EXISTS contact;
CREATE TABLE contact (
  contact_id bigint NOT NULL AUTO_INCREMENT,
  first_name varchar(255) NOT NULL,
  last_name varchar(255) NOT NULL,
  middle_name varchar(255) DEFAULT NULL,
  salutation varchar(255) DEFAULT NULL,
  title varchar(255) DEFAULT NULL,
  version bigint NOT NULL,
  PRIMARY KEY (contact_id)
) ENGINE=InnoDB;



DROP TABLE IF EXISTS email_address;
CREATE TABLE email_address (
  email_id bigint NOT NULL AUTO_INCREMENT,
  label varchar(40) NOT NULL,
  email_address varchar(255) NOT NULL,
  contact_id bigint NOT NULL,
  version bigint NOT NULL,
  PRIMARY KEY (email_id)
) ENGINE=InnoDB;


DROP TABLE IF EXISTS phone_number;
CREATE TABLE phone_number (
  phone_id bigint NOT NULL AUTO_INCREMENT,
  label varchar(40) NOT NULL,
  extention varchar(10) DEFAULT NULL,
  number varchar(20) NOT NULL,
  contact_id bigint NOT NULL,
  version bigint NOT NULL,
  PRIMARY KEY (phone_id)
) ENGINE=InnoDB;


ALTER TABLE email_address ADD CONSTRAINT fk_email_address_contact
	FOREIGN KEY (contact_id) REFERENCES contact(contact_id);
	
ALTER TABLE email_address ADD CONSTRAINT ak_email_address UNIQUE(contact_id,email_address); 
	
ALTER TABLE phone_number ADD CONSTRAINT fk_phone_number_contact
	FOREIGN KEY (contact_id) REFERENCES contact(contact_id);
	
ALTER TABLE phone_number ADD CONSTRAINT ak_phone_number UNIQUE(contact_id,number); 

ALTER TABLE address ADD CONSTRAINT fk_address_contact
	FOREIGN KEY (contact_id) REFERENCES contact(contact_id);
	
ALTER TABLE address ADD CONSTRAINT ak_address UNIQUE(contact_id,address_line_1,zip);

