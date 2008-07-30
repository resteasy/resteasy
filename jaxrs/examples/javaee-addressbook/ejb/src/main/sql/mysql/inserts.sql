INSERT INTO contact (contact_id,first_name,middle_name,last_name,salutation,title,version)
	VALUES (1,"Ryan", "J.","McDonough","Mr.","Developer",0);

INSERT INTO email_address (email_address,label,contact_id,version)
	VALUE ("ryan@damnhandy.com","PERSONAL",1,0);

INSERT INTO email_address (email_address,label,contact_id,version)
	VALUE ("rmcdonough2@sapient.com","WORK",1,0);

INSERT INTO phone_number (extention,number,label,contact_id,version)
	VALUE ("123","617-666-xxxx","WORK",1,0);

INSERT INTO phone_number (extention,number,label,contact_id,version)
	VALUE ("456","617-555-1212","HOME",1,0);

INSERT INTO address (label, address_line_1,city, state, zip, contact_id,version)
 VALUES ("WORK", "131 Dartmouth St.", "Boston", "MA", "02113", 1,0);