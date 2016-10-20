
/*
确保
/etc/mysql/my.cnf文件的[mysql]段中有以下配置：
default-character-set=utf8
以避免中文乱码
*/

DROP DATABASE IF EXISTS etl;
CREATE DATABASE etl CHARACTER SET utf8;
CREATE USER 'etl'@'localhost' IDENTIFIED BY 'etl888';
GRANT ALL PRIVILEGES ON etl.* TO 'etl'@'localhost';
