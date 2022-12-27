-------------------------------------
-- Tag
-------------------------------------
INSERT INTO tag(id, type, name) VALUES(1, 'CATEGORY', 'CATEGORY-1');
INSERT INTO tag(id, type, name) VALUES(2, 'CATEGORY', 'CATEGORY-2');
INSERT INTO tag(id, type, name) VALUES(3, 'CATEGORY', 'CATEGORY-3');

INSERT INTO tag(id, type, name) VALUES(4, 'TYPE', 'TYPE-1');
INSERT INTO tag(id, type, name) VALUES(5, 'TYPE', 'TYPE-2');
INSERT INTO tag(id, type, name) VALUES(6, 'TYPE', 'TYPE-3');

INSERT INTO tag(id, type, name) VALUES(7, 'STATUS', 'STATUS-1');
INSERT INTO tag(id, type, name) VALUES(8, 'STATUS', 'STATUS-2');
INSERT INTO tag(id, type, name) VALUES(9, 'STATUS', 'STATUS-3');

INSERT INTO tag(id, type, name) VALUES(10, 'CLIENT_ID', 'CLIENT_ID-1');
INSERT INTO tag(id, type, name) VALUES(11, 'CLIENT_ID', 'CLIENT_ID-2');
INSERT INTO tag(id, type, name) VALUES(12, 'CLIENT_ID', 'CLIENT_ID-3');

-------------------------------------
-- Errand
-------------------------------------
INSERT INTO errand(id, assigned_group_id, assigned_user_id, category_tag, customer_id, customer_type, client_id_tag, priority, reporter_user_id, status_tag, title, type_tag)
VALUES('ec677eb3-604c-4935-bff7-f8f0b500c8f4', 'ASSIGNED_GROUP_ID-1', 'ASSIGNED_USER_ID-1', 'CATEGORY-1', 'CUSTOMER_ID-1', 'EMPLOYEE', 'CLIENT_ID-1', 'LOW', 'REPORTER_USER_ID-1', 'STATUS-1', 'TITLE-1', 'TYPE-1');
INSERT INTO errand(id, assigned_group_id, assigned_user_id, category_tag, customer_id, customer_type, client_id_tag, priority, reporter_user_id, status_tag, title, type_tag)
VALUES('cc236cf1-c00f-4479-8341-ecf5dd90b5b9', 'ASSIGNED_GROUP_ID-1', 'ASSIGNED_USER_ID-1', 'CATEGORY-1', 'CUSTOMER_ID-2', 'PRIVATE', 'CLIENT_ID-1', 'LOW', 'REPORTER_USER_ID-1', 'STATUS-1', 'TITLE-1', 'TYPE-1');
INSERT INTO errand(id, assigned_group_id, assigned_user_id, category_tag, customer_id, customer_type, client_id_tag, priority, reporter_user_id, status_tag, title, type_tag)
VALUES('1be673c0-6ba3-4fb0-af4a-43acf23389f6', 'ASSIGNED_GROUP_ID-3', 'ASSIGNED_USER_ID-3', 'CATEGORY-3', 'CUSTOMER_ID-3', 'ENTERPRISE', 'CLIENT_ID-3', 'HIGH', 'REPORTER_USER_ID-3', 'STATUS-3', 'TITLE-3', 'TYPE-3');

INSERT INTO external_tag(errand_id, `key`, `value`) VALUES ('ec677eb3-604c-4935-bff7-f8f0b500c8f4', 'KEY-1', 'VALUE-1');
INSERT INTO external_tag(errand_id, `key`, `value`) VALUES ('ec677eb3-604c-4935-bff7-f8f0b500c8f4', 'KEY-2', 'VALUE-2');
INSERT INTO external_tag(errand_id, `key`, `value`) VALUES ('cc236cf1-c00f-4479-8341-ecf5dd90b5b9', 'KEY-3', 'VALUE-3');
INSERT INTO external_tag(errand_id, `key`, `value`) VALUES ('1be673c0-6ba3-4fb0-af4a-43acf23389f6', 'KEY-4', 'VALUE-4');
INSERT INTO external_tag(errand_id, `key`, `value`) VALUES ('1be673c0-6ba3-4fb0-af4a-43acf23389f6', 'KEY-5', 'VALUE-5');
