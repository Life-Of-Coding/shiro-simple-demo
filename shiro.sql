/*
 Navicat Premium Data Transfer

 Source Server         : mysql
 Source Server Type    : MySQL
 Source Server Version : 80017
 Source Host           : localhost:3306
 Source Schema         : shiro

 Target Server Type    : MySQL
 Target Server Version : 80017
 File Encoding         : 65001

 Date: 02/01/2020 21:35:47
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for roles_permissions
-- ----------------------------
DROP TABLE IF EXISTS `roles_permissions`;
CREATE TABLE `roles_permissions` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `role_name` varchar(100) DEFAULT NULL,
  `permission` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_roles_permissions` (`role_name`,`permission`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of roles_permissions
-- ----------------------------
BEGIN;
INSERT INTO `roles_permissions` VALUES (1, 'admin', 'user:delete');
COMMIT;

-- ----------------------------
-- Table structure for shiro_web_roles_permissions
-- ----------------------------
DROP TABLE IF EXISTS `shiro_web_roles_permissions`;
CREATE TABLE `shiro_web_roles_permissions` (
  `role` varchar(20) NOT NULL,
  `permission` varchar(20) NOT NULL,
  PRIMARY KEY (`role`,`permission`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of shiro_web_roles_permissions
-- ----------------------------
BEGIN;
INSERT INTO `shiro_web_roles_permissions` VALUES ('admin', 'user:delete');
INSERT INTO `shiro_web_roles_permissions` VALUES ('user', 'user:login');
COMMIT;

-- ----------------------------
-- Table structure for shiro_web_user_roles
-- ----------------------------
DROP TABLE IF EXISTS `shiro_web_user_roles`;
CREATE TABLE `shiro_web_user_roles` (
  `username` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `role` varchar(20) NOT NULL,
  PRIMARY KEY (`username`,`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of shiro_web_user_roles
-- ----------------------------
BEGIN;
INSERT INTO `shiro_web_user_roles` VALUES ('go', 'admin');
INSERT INTO `shiro_web_user_roles` VALUES ('go', 'user');
INSERT INTO `shiro_web_user_roles` VALUES ('golang', 'admin');
INSERT INTO `shiro_web_user_roles` VALUES ('golang', 'user');
INSERT INTO `shiro_web_user_roles` VALUES ('java', 'admin');
INSERT INTO `shiro_web_user_roles` VALUES ('java', 'user');
INSERT INTO `shiro_web_user_roles` VALUES ('python', 'admin');
INSERT INTO `shiro_web_user_roles` VALUES ('python', 'user');
INSERT INTO `shiro_web_user_roles` VALUES ('test', 'admin');
INSERT INTO `shiro_web_user_roles` VALUES ('test', 'user');
INSERT INTO `shiro_web_user_roles` VALUES ('test1', 'admin');
INSERT INTO `shiro_web_user_roles` VALUES ('test1', 'user');
INSERT INTO `shiro_web_user_roles` VALUES ('test2', 'admin');
INSERT INTO `shiro_web_user_roles` VALUES ('test2', 'user');
COMMIT;

-- ----------------------------
-- Table structure for shiro_web_users
-- ----------------------------
DROP TABLE IF EXISTS `shiro_web_users`;
CREATE TABLE `shiro_web_users` (
  `username` varchar(20) DEFAULT NULL,
  `password` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `salt` varchar(64) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of shiro_web_users
-- ----------------------------
BEGIN;
INSERT INTO `shiro_web_users` VALUES ('java', '29763fd0f600e60a281a3e0b31e01536', '5.911297000215786');
INSERT INTO `shiro_web_users` VALUES ('python', 'ca5c85dd7005a1c20ec7e0f2c9506145', '0.016997541814989248');
INSERT INTO `shiro_web_users` VALUES ('go', 'cf234dd044e1622756aeeb13f3df0f7f', '3.9669089181650765');
INSERT INTO `shiro_web_users` VALUES ('golang', 'bec4e43ef3fd51e188e81cc9599d92bd', '4.004619402637606');
INSERT INTO `shiro_web_users` VALUES ('test', 'ef51ce9390207a288675cda0272aaae9', '2.9471983131274215');
INSERT INTO `shiro_web_users` VALUES ('test1', '12831f07e2b5afa374a1fc225b02e641', '9.207987027450228');
INSERT INTO `shiro_web_users` VALUES ('test2', 'b77c42699357f9057e6221cb76bc27fd', '1.6463770734190541');
COMMIT;

-- ----------------------------
-- Table structure for test_roles_permissions
-- ----------------------------
DROP TABLE IF EXISTS `test_roles_permissions`;
CREATE TABLE `test_roles_permissions` (
  `role` varchar(20) NOT NULL,
  `permission` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of test_roles_permissions
-- ----------------------------
BEGIN;
INSERT INTO `test_roles_permissions` VALUES ('admin', 'user:delete');
COMMIT;

-- ----------------------------
-- Table structure for test_user_roles
-- ----------------------------
DROP TABLE IF EXISTS `test_user_roles`;
CREATE TABLE `test_user_roles` (
  `username` varchar(20) DEFAULT NULL,
  `role` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of test_user_roles
-- ----------------------------
BEGIN;
INSERT INTO `test_user_roles` VALUES ('java', 'admin');
COMMIT;

-- ----------------------------
-- Table structure for test_users
-- ----------------------------
DROP TABLE IF EXISTS `test_users`;
CREATE TABLE `test_users` (
  `username` varchar(20) DEFAULT NULL,
  `password` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of test_users
-- ----------------------------
BEGIN;
INSERT INTO `test_users` VALUES ('java', '123');
COMMIT;

-- ----------------------------
-- Table structure for user_roles
-- ----------------------------
DROP TABLE IF EXISTS `user_roles`;
CREATE TABLE `user_roles` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(100) DEFAULT NULL,
  `role_name` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_user_roles` (`username`,`role_name`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user_roles
-- ----------------------------
BEGIN;
INSERT INTO `user_roles` VALUES (1, 'java', 'admin');
COMMIT;

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(100) DEFAULT NULL,
  `password` varchar(100) DEFAULT NULL,
  `password_salt` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_users_username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of users
-- ----------------------------
BEGIN;
INSERT INTO `users` VALUES (1, 'java', '123', NULL);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
