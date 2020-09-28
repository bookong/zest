CREATE TABLE `tab1` (
  `id`          bigint       NOT NULL AUTO_INCREMENT,
  `login_name`  varchar(32)  NOT NULL ,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_u_1` (`login_name`)
) ;