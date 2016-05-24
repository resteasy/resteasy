use contact;

LOCK TABLES Contact WRITE, ContactToContactJoinTable WRITE;

insert into contact (id, name, email, phone) values (1, 'olivier','olivier@yahoo.com', '16506193726');
insert into contact (id, name, email, phone) values (2, 'angela','angela@yahoo.com', '4312432432432');
insert into contact (id, name, email, phone) values (3, 'john','john@yahoo.com', '432432432432');

insert into ContactToContactJoinTable (parentContactId, childContactId) values (1, 2);
insert into ContactToContactJoinTable (parentContactId, childContactId) values (2, 3);
insert into ContactToContactJoinTable (parentContactId, childContactId) values (2, 1);
insert into ContactToContactJoinTable (parentContactId, childContactId) values (3, 2);

UNLOCK TABLES;
