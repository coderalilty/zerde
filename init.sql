CREATE DATABASE IF NOT EXISTS `zerde-kidd-house` collate utf8mb4_unicode_ci;
CREATE USER 'root'@'%' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' WITH GRANT OPTION;
