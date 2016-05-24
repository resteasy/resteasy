drop database if exists contact;
create database contact character set utf8;
use contact;

/* Table used for MZT release purposes */ 
CREATE TABLE Contact (
    id integer not null primary key AUTO_INCREMENT,
    name varchar(75),
	email varchar(75),
    phone varchar(75)
);

/* N to N recursive relationship */
CREATE TABLE ContactToContactJoinTable (
	parentContactId integer,
	childContactId  integer,
	PRIMARY KEY(parentContactId,childContactId),
	FOREIGN KEY (parentContactId) REFERENCES Contact (id),
	FOREIGN KEY (childContactId) REFERENCES Contact (id)
);