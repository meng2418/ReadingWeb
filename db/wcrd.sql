-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: localhost    Database: wechatreading_db
-- ------------------------------------------------------
-- Server version	8.0.42

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `achievement_log`
--

DROP TABLE IF EXISTS `achievement_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `achievement_log` (
  `log_id` int NOT NULL AUTO_INCREMENT,
  `achieved_at` datetime(6) DEFAULT NULL,
  `description` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `level` int NOT NULL,
  `type` enum('BOOK_COUNT','DURATION','NOTE_COUNT') COLLATE utf8mb4_unicode_ci NOT NULL,
  `user_id` bigint NOT NULL,
  `value_snapshot` int NOT NULL,
  PRIMARY KEY (`log_id`),
  UNIQUE KEY `UKjp2k4g95ggu2npq884c0srrtk` (`user_id`,`type`,`level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `achievement_log`
--

LOCK TABLES `achievement_log` WRITE;
/*!40000 ALTER TABLE `achievement_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `achievement_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `author_info`
--

DROP TABLE IF EXISTS `author_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `author_info` (
  `author_id` bigint NOT NULL AUTO_INCREMENT,
  `avatar` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `bio` text COLLATE utf8mb4_unicode_ci,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `author_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`author_id`),
  UNIQUE KEY `UKme4hv1oktmw9rcvvrssvvv1g7` (`name`),
  UNIQUE KEY `UKmc835xba54n2677mynj0u3tf9` (`author_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `author_info`
--

LOCK TABLES `author_info` WRITE;
/*!40000 ALTER TABLE `author_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `author_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `book_category`
--

DROP TABLE IF EXISTS `book_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `book_category` (
  `category_id` int NOT NULL AUTO_INCREMENT,
  `book_count` int DEFAULT NULL,
  `category_level` int DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `icon` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_enabled` bit(1) DEFAULT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `parent_id` int DEFAULT NULL,
  `sort_order` int DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `book_category`
--

LOCK TABLES `book_category` WRITE;
/*!40000 ALTER TABLE `book_category` DISABLE KEYS */;
/*!40000 ALTER TABLE `book_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `book_chapter`
--

DROP TABLE IF EXISTS `book_chapter`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `book_chapter` (
  `chapter_id` int NOT NULL AUTO_INCREMENT,
  `book_id` int NOT NULL,
  `chapter_number` int NOT NULL,
  `content` text COLLATE utf8mb4_unicode_ci,
  `created_at` datetime(6) DEFAULT NULL,
  `is_published` bit(1) DEFAULT NULL,
  `is_vip` bit(1) DEFAULT NULL,
  `next_chapter_id` int DEFAULT NULL,
  `prev_chapter_id` int DEFAULT NULL,
  `read_count` int DEFAULT NULL,
  `title` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `word_count` int DEFAULT NULL,
  PRIMARY KEY (`chapter_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `book_chapter`
--

LOCK TABLES `book_chapter` WRITE;
/*!40000 ALTER TABLE `book_chapter` DISABLE KEYS */;
/*!40000 ALTER TABLE `book_chapter` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `book_info`
--

DROP TABLE IF EXISTS `book_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `book_info` (
  `book_id` int NOT NULL AUTO_INCREMENT,
  `author_id` bigint NOT NULL,
  `parentCategory_id` int NOT NULL,
  `category_id` int NOT NULL,
  `cover` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `is_free` bit(1) DEFAULT NULL,
  `isbn` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `price` int DEFAULT NULL,
  `publish_date` datetime(6) DEFAULT NULL,
  `publisher` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `rating` float DEFAULT NULL,
  `read_count` int DEFAULT NULL,
  `title` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `word_count` int DEFAULT NULL,
  `is_member_only` bit(1) DEFAULT NULL,
  `is_published` bit(1) DEFAULT NULL,
  `tags` text COLLATE utf8mb4_unicode_ci,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`book_id`),
  KEY `FKdr31r9ml7jpvhbynv43585b2d` (`author_id`),
  CONSTRAINT `FKdr31r9ml7jpvhbynv43585b2d` FOREIGN KEY (`author_id`) REFERENCES `author_info` (`author_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `book_info`
--

LOCK TABLES `book_info` WRITE;
/*!40000 ALTER TABLE `book_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `book_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bookmark_info`
--

DROP TABLE IF EXISTS `bookmark_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bookmark_info` (
  `bookmark_id` int NOT NULL AUTO_INCREMENT,
  `book_id` int NOT NULL,
  `chapter_id` int NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `note` text COLLATE utf8mb4_unicode_ci,
  `page_number` int DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`bookmark_id`),
  UNIQUE KEY `UK3gc4mki5lanxgovenqnyeaq59` (`user_id`,`book_id`,`chapter_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bookmark_info`
--

LOCK TABLES `bookmark_info` WRITE;
/*!40000 ALTER TABLE `bookmark_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `bookmark_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bookshelf_info`
--

DROP TABLE IF EXISTS `bookshelf_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bookshelf_info` (
  `bookshelf_id` int NOT NULL AUTO_INCREMENT,
  `added_at` datetime(6) NOT NULL,
  `book_id` int NOT NULL,
  `last_read_at` datetime(6) DEFAULT NULL,
  `status` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`bookshelf_id`),
  UNIQUE KEY `UKmhk83rvdn8kqn8b1q34301ex8` (`user_id`,`book_id`),
  KEY `FK8huviyx8tk4g1r0bx3vtkt31k` (`book_id`),
  CONSTRAINT `FK8huviyx8tk4g1r0bx3vtkt31k` FOREIGN KEY (`book_id`) REFERENCES `book_info` (`book_id`),
  CONSTRAINT `FKq2ec3pbrp8j8s4y8m1lgiarxv` FOREIGN KEY (`user_id`) REFERENCES `user_info` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bookshelf_info`
--

LOCK TABLES `bookshelf_info` WRITE;
/*!40000 ALTER TABLE `bookshelf_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `bookshelf_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `category_info`
--

DROP TABLE IF EXISTS `category_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `category_info` (
  `category_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `category_info`
--

LOCK TABLES `category_info` WRITE;
/*!40000 ALTER TABLE `category_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `category_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chapter_access_info`
--

DROP TABLE IF EXISTS `chapter_access_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chapter_access_info` (
  `access_id` int NOT NULL AUTO_INCREMENT,
  `access_level` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `chapter_id` int NOT NULL,
  PRIMARY KEY (`access_id`),
  UNIQUE KEY `UK8wec1t5s7l23a77hle5oid4eb` (`chapter_id`),
  CONSTRAINT `FKdrav4il0j4pia6u96ows0i3ff` FOREIGN KEY (`chapter_id`) REFERENCES `chapter_info` (`chapter_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chapter_access_info`
--

LOCK TABLES `chapter_access_info` WRITE;
/*!40000 ALTER TABLE `chapter_access_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `chapter_access_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chapter_info`
--

DROP TABLE IF EXISTS `chapter_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chapter_info` (
  `chapter_id` int NOT NULL AUTO_INCREMENT,
  `book_id` int NOT NULL,
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `order` int NOT NULL,
  `chapter_number` int NOT NULL,  --新增字段
  `title` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`chapter_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chapter_info`
--

LOCK TABLES `chapter_info` WRITE;
/*!40000 ALTER TABLE `chapter_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `chapter_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `coin_account_info`
--

DROP TABLE IF EXISTS `coin_account_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `coin_account_info` (
  `account_id` int NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `balance` int DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`account_id`),
  UNIQUE KEY `UKtdgvcgik1fwwasamny1irjq4g` (`user_id`),
  CONSTRAINT `FKppxad1qsxg59k5ntcopdrsjub` FOREIGN KEY (`user_id`) REFERENCES `user_info` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `coin_account_info`
--

LOCK TABLES `coin_account_info` WRITE;
/*!40000 ALTER TABLE `coin_account_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `coin_account_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `coin_transaction_info`
--

DROP TABLE IF EXISTS `coin_transaction_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `coin_transaction_info` (
  `transaction_id` int NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `account_id` int NOT NULL,
  `amount` int DEFAULT NULL,
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `type` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`transaction_id`),
  KEY `FKbi0qdpbhj964wwkhfrw23fspi` (`account_id`),
  CONSTRAINT `FKbi0qdpbhj964wwkhfrw23fspi` FOREIGN KEY (`account_id`) REFERENCES `coin_account_info` (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `coin_transaction_info`
--

LOCK TABLES `coin_transaction_info` WRITE;
/*!40000 ALTER TABLE `coin_transaction_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `coin_transaction_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comment_info`
--

DROP TABLE IF EXISTS `comment_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comment_info` (
  `comment_id` bigint NOT NULL AUTO_INCREMENT,
  `content` longtext COLLATE utf8mb4_unicode_ci,
  `created_at` datetime(6) DEFAULT NULL,
  `likes_count` bigint NOT NULL,
  `note_id` bigint DEFAULT NULL,
  `parent_comment_id` bigint DEFAULT NULL,
  `post_id` bigint NOT NULL,
  `reply_count` int NOT NULL,
  `status` int NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`comment_id`),
  KEY `FK20rmxac5qcfkoiek5jn0s3523` (`note_id`),
  KEY `FKl79iid7ghravgghsxsqj15e1r` (`parent_comment_id`),
  KEY `FKhehbbolc3pxpn3x5lluas0p1u` (`post_id`),
  KEY `FK48snme635nlr3j1367bak3u0w` (`user_id`),
  CONSTRAINT `FK20rmxac5qcfkoiek5jn0s3523` FOREIGN KEY (`note_id`) REFERENCES `note_info` (`note_id`),
  CONSTRAINT `FK48snme635nlr3j1367bak3u0w` FOREIGN KEY (`user_id`) REFERENCES `user_info` (`user_id`),
  CONSTRAINT `FKhehbbolc3pxpn3x5lluas0p1u` FOREIGN KEY (`post_id`) REFERENCES `post_info` (`post_id`),
  CONSTRAINT `FKl79iid7ghravgghsxsqj15e1r` FOREIGN KEY (`parent_comment_id`) REFERENCES `comment_info` (`comment_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comment_info`
--

LOCK TABLES `comment_info` WRITE;
/*!40000 ALTER TABLE `comment_info` DISABLE KEYS */;
INSERT INTO `comment_info` VALUES (1,'deserunt','2026-01-10 13:46:02.001795',0,NULL,NULL,1,2,0,3),(2,'string','2026-01-10 14:19:06.892906',0,NULL,1,1,0,0,3),(3,'string','2026-01-10 15:22:50.606397',0,NULL,1,1,0,0,3),(4,'deserunt','2026-01-10 16:01:28.593648',0,NULL,NULL,1,0,0,3);
/*!40000 ALTER TABLE `comment_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `consecutive_reading`
--

DROP TABLE IF EXISTS `consecutive_reading`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `consecutive_reading` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` int NOT NULL COMMENT '用户ID',
  `current_streak` int NOT NULL DEFAULT '0' COMMENT '当前连续天数',
  `longest_streak` int NOT NULL DEFAULT '0' COMMENT '最长连续天数',
  `last_read_date` date NOT NULL COMMENT '最后阅读日期',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  KEY `idx_last_read_date` (`last_read_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='连续阅读记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `consecutive_reading`
--

LOCK TABLES `consecutive_reading` WRITE;
/*!40000 ALTER TABLE `consecutive_reading` DISABLE KEYS */;
/*!40000 ALTER TABLE `consecutive_reading` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `follow_info`
--

DROP TABLE IF EXISTS `follow_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `follow_info` (
  `follow_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `follower_id` bigint NOT NULL,
  `following_id` bigint NOT NULL,
  PRIMARY KEY (`follow_id`),
  UNIQUE KEY `UK595t55apajqp5yij34vfdx1sa` (`follower_id`,`following_id`),
  KEY `FK5qtxill97pobshtyt452fqe8g` (`following_id`),
  CONSTRAINT `FK5qtxill97pobshtyt452fqe8g` FOREIGN KEY (`following_id`) REFERENCES `user_info` (`user_id`),
  CONSTRAINT `FKalxmv7asrdc2t2islofasrs83` FOREIGN KEY (`follower_id`) REFERENCES `user_info` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `follow_info`
--

LOCK TABLES `follow_info` WRITE;
/*!40000 ALTER TABLE `follow_info` DISABLE KEYS */;
INSERT INTO `follow_info` VALUES (3,'2025-12-30 16:56:38.269256',3,2);
/*!40000 ALTER TABLE `follow_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `like_info`
--

DROP TABLE IF EXISTS `like_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `like_info` (
  `like_id` bigint NOT NULL AUTO_INCREMENT,
  `comment_id` bigint DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `note_id` int DEFAULT NULL,
  `post_id` bigint DEFAULT NULL,
  `user_id` bigint NOT NULL,
  `target_type` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`like_id`),
  UNIQUE KEY `UKl6ppu8vkat29xy2uavaetqgit` (`user_id`,`post_id`,`comment_id`,`note_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `like_info`
--

LOCK TABLES `like_info` WRITE;
/*!40000 ALTER TABLE `like_info` DISABLE KEYS */;
INSERT INTO `like_info` VALUES (1,NULL,'2026-01-10 13:34:20.992554',NULL,1,3,'post');
/*!40000 ALTER TABLE `like_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `login_log_info`
--

DROP TABLE IF EXISTS `login_log_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `login_log_info` (
  `login_log_id` bigint NOT NULL AUTO_INCREMENT,
  `device` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ip_address` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `login_location` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `login_time` datetime(6) DEFAULT NULL,
  `remarks` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`login_log_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `login_log_info`
--

LOCK TABLES `login_log_info` WRITE;
/*!40000 ALTER TABLE `login_log_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `login_log_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `member_card`
--

DROP TABLE IF EXISTS `member_card`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `member_card` (
  `card_id` int NOT NULL AUTO_INCREMENT COMMENT '卡片ID',
  `user_id` int NOT NULL COMMENT '用户ID',
  `card_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '卡片名称',
  `duration_days` int NOT NULL COMMENT '有效天数',
  `card_type` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_used` tinyint(1) DEFAULT '0' COMMENT '是否已使用：0-否，1-是',
  `used_at` datetime DEFAULT NULL COMMENT '使用时间',
  `expire_at` datetime NOT NULL COMMENT '过期时间',
  `source_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '来源类型：purchase-购买，gift-赠送，reward-奖励，promotion-促销，system-系统赠送',
  `source_order_no` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '来源订单号',
  `status` int DEFAULT NULL,
  `notes` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`card_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_user_status` (`user_id`,`status`),
  KEY `idx_expire_at` (`expire_at`),
  KEY `idx_source_order` (`source_order_no`),
  KEY `idx_used` (`is_used`),
  KEY `idx_card_type` (`card_type`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会员卡表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `member_card`
--

LOCK TABLES `member_card` WRITE;
/*!40000 ALTER TABLE `member_card` DISABLE KEYS */;
/*!40000 ALTER TABLE `member_card` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `member_info`
--

DROP TABLE IF EXISTS `member_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `member_info` (
  `member_id` int NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `expire_date` datetime(6) DEFAULT NULL,
  `level` int NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`member_id`),
  UNIQUE KEY `UKfnv18odp6neo69byak2154w1e` (`user_id`),
  CONSTRAINT `FKmyfaw3fv4q0g19itnw76okkvg` FOREIGN KEY (`user_id`) REFERENCES `user_info` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `member_info`
--

LOCK TABLES `member_info` WRITE;
/*!40000 ALTER TABLE `member_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `member_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `membership_benefit_record`
--

DROP TABLE IF EXISTS `membership_benefit_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `membership_benefit_record` (
  `record_id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` int NOT NULL COMMENT '用户ID',
  `order_no` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '订单号',
  `benefit_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '权益类型',
  `benefit_value` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '权益值',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态: 0-已失效, 1-有效',
  `expire_time` datetime DEFAULT NULL COMMENT '失效时间',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`record_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_order_no` (`order_no`),
  KEY `idx_benefit_type` (`benefit_type`),
  KEY `idx_expire_time` (`expire_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会员权益记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `membership_benefit_record`
--

LOCK TABLES `membership_benefit_record` WRITE;
/*!40000 ALTER TABLE `membership_benefit_record` DISABLE KEYS */;
/*!40000 ALTER TABLE `membership_benefit_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `membership_order`
--

DROP TABLE IF EXISTS `membership_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `membership_order` (
  `order_id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '订单编号',
  `user_id` int NOT NULL COMMENT '用户ID',
  `package_id` int NOT NULL COMMENT '套餐ID',
  `package_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '套餐名称',
  `duration_type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '时长类型',
  `duration_days` int NOT NULL COMMENT '开通天数',
  `original_amount` decimal(10,2) NOT NULL COMMENT '原价金额',
  `pay_amount` decimal(10,2) NOT NULL COMMENT '支付金额',
  `payment_method` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '支付方式: wechat, alipay, balance, card',
  `payment_status` tinyint NOT NULL DEFAULT '0' COMMENT '支付状态: 0-待支付, 1-已支付, 2-支付失败, 3-已取消, 4-已退款',
  `transaction_no` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '第三方支付流水号',
  `order_status` tinyint NOT NULL DEFAULT '0' COMMENT '订单状态: 0-待处理, 1-已开通, 2-已过期, 3-已取消',
  `expire_at` datetime DEFAULT NULL COMMENT '订单过期时间',
  `paid_at` datetime DEFAULT NULL COMMENT '支付时间',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`order_id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_order_status` (`order_status`),
  KEY `idx_payment_status` (`payment_status`),
  KEY `idx_created_at` (`created_at`),
  KEY `idx_expire_at` (`expire_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会员订单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `membership_order`
--

LOCK TABLES `membership_order` WRITE;
/*!40000 ALTER TABLE `membership_order` DISABLE KEYS */;
/*!40000 ALTER TABLE `membership_order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `membership_package`
--

DROP TABLE IF EXISTS `membership_package`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `membership_package` (
  `package_id` int NOT NULL AUTO_INCREMENT COMMENT '套餐ID',
  `package_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '年会' COMMENT '套餐名称',
  `duration_type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '时长类型: week-周, month-月, quarter-季, year-年',
  `duration_days` int NOT NULL COMMENT '开通天数',
  `original_price` decimal(10,2) NOT NULL COMMENT '原价',
  `discount_price` decimal(10,2) NOT NULL COMMENT '折扣价',
  `description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '套餐描述',
  `is_hot` tinyint(1) DEFAULT '0' COMMENT '是否热门推荐: 0-否, 1-是',
  `is_active` int DEFAULT NULL,
  `display_order` int DEFAULT '0' COMMENT '显示顺序',
  `benefits` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '权益列表(JSON格式)',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`package_id`),
  KEY `idx_active_hot` (`is_active`,`is_hot`),
  KEY `idx_display_order` (`display_order`),
  KEY `idx_duration_type` (`duration_type`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会员套餐表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `membership_package`
--

LOCK TABLES `membership_package` WRITE;
/*!40000 ALTER TABLE `membership_package` DISABLE KEYS */;
INSERT INTO `membership_package` VALUES (1,'年会','year',365,228.00,32.00,NULL,0,1,0,NULL,'2026-01-09 16:48:07','2026-01-09 16:48:07');
/*!40000 ALTER TABLE `membership_package` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `membership_plan_info`
--

DROP TABLE IF EXISTS `membership_plan_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `membership_plan_info` (
  `plan_id` int NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `duration_days` int NOT NULL,
  `is_available` bit(1) NOT NULL,
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `price_cents` int NOT NULL,
  PRIMARY KEY (`plan_id`),
  UNIQUE KEY `UK5yiw93q3bvmq5fl62swisjlxo` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `membership_plan_info`
--

LOCK TABLES `membership_plan_info` WRITE;
/*!40000 ALTER TABLE `membership_plan_info` DISABLE KEYS */;
INSERT INTO `membership_plan_info` VALUES (1,'2025-12-09 23:04:26.418866','2025-12-09 23:04:26.418866','连续包月，每月30元，享受30天会员权益',30,_binary '','月卡',3000),(2,'2025-12-09 23:04:26.456458','2025-12-09 23:04:26.456458','购买3个月会员，限时优惠价60元',90,_binary '','季卡',6000),(3,'2025-12-09 23:04:26.459869','2025-12-09 23:04:26.459869','购买12个月会员，年度最优价228元',365,_binary '','年卡',22800);
/*!40000 ALTER TABLE `membership_plan_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `message_info`
--

DROP TABLE IF EXISTS `message_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `message_info` (
  `message_id` bigint NOT NULL AUTO_INCREMENT,
  `content` text COLLATE utf8mb4_unicode_ci,
  `created_at` datetime(6) DEFAULT NULL,
  `is_read` bit(1) NOT NULL,
  `related_id` bigint DEFAULT NULL,
  `title` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `type` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`message_id`),
  KEY `FK30ywd5k2jc9n5whw1drrcljfq` (`user_id`),
  CONSTRAINT `FK30ywd5k2jc9n5whw1drrcljfq` FOREIGN KEY (`user_id`) REFERENCES `user_info` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `message_info`
--

LOCK TABLES `message_info` WRITE;
/*!40000 ALTER TABLE `message_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `message_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `note_info`
--

DROP TABLE IF EXISTS `note_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `note_info` (
  `note_id` bigint NOT NULL AUTO_INCREMENT,
  `book_id` int NOT NULL,
  `chapter_id` int DEFAULT NULL,
  `color` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `is_public` bit(1) NOT NULL,
  `page` int DEFAULT NULL,
  `type` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`note_id`),
  KEY `FKiyt16cjimydy6p24dtlbl3leu` (`book_id`),
  KEY `FK27o1hsr00vqhssarxirbxp70u` (`chapter_id`),
  KEY `FK72rej2xf96n1s2hnhh1dlnqt5` (`user_id`),
  CONSTRAINT `FK27o1hsr00vqhssarxirbxp70u` FOREIGN KEY (`chapter_id`) REFERENCES `chapter_info` (`chapter_id`),
  CONSTRAINT `FK72rej2xf96n1s2hnhh1dlnqt5` FOREIGN KEY (`user_id`) REFERENCES `user_info` (`user_id`),
  CONSTRAINT `FKiyt16cjimydy6p24dtlbl3leu` FOREIGN KEY (`book_id`) REFERENCES `book_info` (`book_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `note_info`
--

LOCK TABLES `note_info` WRITE;
/*!40000 ALTER TABLE `note_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `note_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `post_book_mapping`
--

DROP TABLE IF EXISTS `post_book_mapping`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `post_book_mapping` (
  `post_id` bigint NOT NULL,
  `book_id` int NOT NULL,
  KEY `FK6uq39qwgqbbgqkmiti3fe0oi2` (`book_id`),
  KEY `FKgx7p0q5ybw2s9xa6losgwigj6` (`post_id`),
  CONSTRAINT `FK6uq39qwgqbbgqkmiti3fe0oi2` FOREIGN KEY (`book_id`) REFERENCES `book_info` (`book_id`),
  CONSTRAINT `FKgx7p0q5ybw2s9xa6losgwigj6` FOREIGN KEY (`post_id`) REFERENCES `post_info` (`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `post_book_mapping`
--

LOCK TABLES `post_book_mapping` WRITE;
/*!40000 ALTER TABLE `post_book_mapping` DISABLE KEYS */;
/*!40000 ALTER TABLE `post_book_mapping` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `post_info`
--

DROP TABLE IF EXISTS `post_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `post_info` (
  `post_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `author_id` bigint NOT NULL,
  `comments_count` int NOT NULL DEFAULT '0',
  `content` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `likes_count` int NOT NULL DEFAULT '0',
  `status` int DEFAULT '1',
  `title` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  `is_hot` bit(1) DEFAULT NULL,
  `is_top` bit(1) DEFAULT NULL,
  `publish_location` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `share_count` int DEFAULT '0',
  `view_count` int DEFAULT '0',
  PRIMARY KEY (`post_id`),
  KEY `FKponab5iwtu4gtiirkxx9hf2gw` (`author_id`),
  CONSTRAINT `FKponab5iwtu4gtiirkxx9hf2gw` FOREIGN KEY (`author_id`) REFERENCES `user_info` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `post_info`
--

LOCK TABLES `post_info` WRITE;
/*!40000 ALTER TABLE `post_info` DISABLE KEYS */;
INSERT INTO `post_info` VALUES (1,'2026-01-10 10:16:41.000000',NULL,3,2,'苦难是文学的温床',1,3,'有感','2026-01-10 22:41:47.240477',NULL,NULL,'美国',0,0),(2,'2026-01-10 14:09:39.144157',NULL,3,0,'ut cillum et non',0,1,'那好哎呀意识熟人钟优秀轻松鲁莽',NULL,_binary '\0',_binary '\0',NULL,0,0),(3,'2026-01-10 15:48:44.765432',NULL,3,0,'ut cillum et non',0,1,'那好哎呀意识熟人钟优秀轻松鲁莽',NULL,_binary '\0',_binary '\0',NULL,0,0),(4,'2026-01-10 15:49:41.563597',NULL,3,0,'Excepteur',0,1,'谁代表嗯的大约以免大老实',NULL,_binary '\0',_binary '\0',NULL,0,0),(5,'2026-01-10 22:36:11.358931',NULL,3,0,'pariatur',0,1,'捺必然除非嗯实际上统统百般',NULL,_binary '\0',_binary '\0',NULL,0,0);
/*!40000 ALTER TABLE `post_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `post_topic_info`
--

DROP TABLE IF EXISTS `post_topic_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `post_topic_info` (
  `post_id` bigint NOT NULL,
  `topic_id` int NOT NULL,
  PRIMARY KEY (`post_id`,`topic_id`),
  KEY `FKktngbyji2nrvhfi6ulymmvlkc` (`topic_id`),
  CONSTRAINT `FKktngbyji2nrvhfi6ulymmvlkc` FOREIGN KEY (`topic_id`) REFERENCES `topic_info` (`topic_id`),
  CONSTRAINT `FKq7r4kh2354e5j7q8a02ddpnbx` FOREIGN KEY (`post_id`) REFERENCES `post_info` (`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `post_topic_info`
--

LOCK TABLES `post_topic_info` WRITE;
/*!40000 ALTER TABLE `post_topic_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `post_topic_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reading_milestone`
--

DROP TABLE IF EXISTS `reading_milestone`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reading_milestone` (
  `milestone_id` bigint NOT NULL AUTO_INCREMENT COMMENT '里程碑ID',
  `user_id` int NOT NULL COMMENT '用户ID',
  `milestone_type` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `target_count` int NOT NULL COMMENT '目标数量',
  `book_id` int DEFAULT NULL COMMENT '达到里程碑时阅读的书籍ID',
  `book_title` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `note_id` bigint DEFAULT NULL COMMENT '笔记ID',
  `note_content_preview` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `message` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `is_latest` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否是最新里程碑',
  `achieved_at` datetime NOT NULL COMMENT '达到时间',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`milestone_id`),
  UNIQUE KEY `uk_user_type_count` (`user_id`,`milestone_type`,`target_count`),
  KEY `idx_user_type` (`user_id`,`milestone_type`),
  KEY `idx_is_latest` (`is_latest`),
  KEY `idx_achieved_at` (`achieved_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='阅读里程碑表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reading_milestone`
--

LOCK TABLES `reading_milestone` WRITE;
/*!40000 ALTER TABLE `reading_milestone` DISABLE KEYS */;
/*!40000 ALTER TABLE `reading_milestone` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reading_reward`
--

DROP TABLE IF EXISTS `reading_reward`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reading_reward` (
  `reward_id` bigint NOT NULL AUTO_INCREMENT COMMENT '奖励ID',
  `user_id` int NOT NULL COMMENT '用户ID',
  `reward_date` date NOT NULL COMMENT '奖励日期',
  `reward_type` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `reward_value` int DEFAULT '0' COMMENT '奖励值（如体验卡天数）',
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_claimed` tinyint(1) DEFAULT '0' COMMENT '是否已领取：0-否，1-是',
  `claimed_at` datetime DEFAULT NULL COMMENT '领取时间',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`reward_id`),
  UNIQUE KEY `uk_user_date_type` (`user_id`,`reward_date`,`reward_type`),
  KEY `idx_user_claimed` (`user_id`,`is_claimed`),
  KEY `idx_reward_date` (`reward_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='阅读奖励表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reading_reward`
--

LOCK TABLES `reading_reward` WRITE;
/*!40000 ALTER TABLE `reading_reward` DISABLE KEYS */;
/*!40000 ALTER TABLE `reading_reward` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `readingprogress_info`
--

DROP TABLE IF EXISTS `readingprogress_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `readingprogress_info` (
  `readingprogress_id` int NOT NULL AUTO_INCREMENT,
  `book_id` int NOT NULL,
  `chapter_id` int DEFAULT NULL,
  `currentpage` int DEFAULT NULL,
  `lastreadat` datetime(6) DEFAULT NULL,
  `progress` float DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`readingprogress_id`),
  UNIQUE KEY `UKhmcvhcwt74ec4yosdi3c4529u` (`user_id`,`book_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `readingprogress_info`
--

LOCK TABLES `readingprogress_info` WRITE;
/*!40000 ALTER TABLE `readingprogress_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `readingprogress_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `readingstatus_info`
--

DROP TABLE IF EXISTS `readingstatus_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `readingstatus_info` (
  `readingstatus_id` int NOT NULL AUTO_INCREMENT,
  `date` datetime(6) NOT NULL,
  `readingtime` int DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`readingstatus_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `readingstatus_info`
--

LOCK TABLES `readingstatus_info` WRITE;
/*!40000 ALTER TABLE `readingstatus_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `readingstatus_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `recharge_order`
--

DROP TABLE IF EXISTS `recharge_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `recharge_order` (
  `order_id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单ID（自增主键）',
  `order_no` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '订单号（业务唯一）',
  `user_id` int NOT NULL COMMENT '用户ID',
  `package_id` int NOT NULL COMMENT '套餐ID',
  `coin_amount` int NOT NULL COMMENT '充值币数',
  `bonus_coins` int NOT NULL DEFAULT '0' COMMENT '赠送币数',
  `pay_amount` decimal(10,2) NOT NULL COMMENT '支付金额',
  `payment_method` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '支付方式：wechat/alipay/unionpay',
  `order_status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'pending' COMMENT '订单状态：pending/paid/failed/cancelled/expired',
  `transaction_no` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '第三方支付交易号',
  `paid_at` datetime DEFAULT NULL COMMENT '支付时间',
  `expire_at` datetime DEFAULT NULL COMMENT '订单过期时间',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`order_id`),
  UNIQUE KEY `order_no` (`order_no`),
  KEY `idx_user` (`user_id`) COMMENT '用户索引',
  KEY `idx_status` (`order_status`) COMMENT '状态索引',
  KEY `idx_created` (`created_at`) COMMENT '创建时间索引',
  KEY `package_id` (`package_id`),
  CONSTRAINT `recharge_order_ibfk_1` FOREIGN KEY (`package_id`) REFERENCES `recharge_package` (`package_id`) ON DELETE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='充值订单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `recharge_order`
--

LOCK TABLES `recharge_order` WRITE;
/*!40000 ALTER TABLE `recharge_order` DISABLE KEYS */;
INSERT INTO `recharge_order` VALUES (1,'R17679528595065563',3,1,228,38,228.00,'ALIPAY','PENDING',NULL,NULL,'2026-01-09 18:31:00','2026-01-09 18:01:00','2026-01-09 18:01:00');
/*!40000 ALTER TABLE `recharge_order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `recharge_package`
--

DROP TABLE IF EXISTS `recharge_package`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `recharge_package` (
  `package_id` int NOT NULL AUTO_INCREMENT COMMENT '套餐ID',
  `coin_amount` int NOT NULL COMMENT '充值币数',
  `cny_amount` double NOT NULL,
  `bonus_coins` int NOT NULL DEFAULT '0' COMMENT '赠送币数',
  `is_active` int DEFAULT NULL,
  `display_order` int DEFAULT '0' COMMENT '显示顺序（从小到大排序）',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`package_id`),
  UNIQUE KEY `uk_display_order` (`display_order`) COMMENT '显示顺序唯一约束',
  KEY `idx_active_order` (`is_active`,`display_order`) COMMENT '按启用状态和顺序查询的索引'
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='充值套餐表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `recharge_package`
--

LOCK TABLES `recharge_package` WRITE;
/*!40000 ALTER TABLE `recharge_package` DISABLE KEYS */;
INSERT INTO `recharge_package` VALUES (1,228,228,38,1,0,'2026-01-09 17:06:41','2026-01-09 17:06:41');
/*!40000 ALTER TABLE `recharge_package` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `topic_follows`
--

DROP TABLE IF EXISTS `topic_follows`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `topic_follows` (
  `tf_id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `topic_id` int NOT NULL COMMENT '话题ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`tf_id`),
  UNIQUE KEY `uk_user_topic` (`user_id`,`topic_id`),
  UNIQUE KEY `UK4d0yt666nckq3dc0fb5u41vr6` (`user_id`,`topic_id`),
  KEY `fk_topic_follows_topic` (`topic_id`),
  CONSTRAINT `fk_topic_follows_topic` FOREIGN KEY (`topic_id`) REFERENCES `topic_info` (`topic_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_topic_follows_user` FOREIGN KEY (`user_id`) REFERENCES `user_info` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='话题关注表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `topic_follows`
--

LOCK TABLES `topic_follows` WRITE;
/*!40000 ALTER TABLE `topic_follows` DISABLE KEYS */;
/*!40000 ALTER TABLE `topic_follows` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `topic_info`
--

DROP TABLE IF EXISTS `topic_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `topic_info` (
  `topic_id` int NOT NULL AUTO_INCREMENT,
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `image` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `post_count` int DEFAULT '0',
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `topic_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `view_count` int DEFAULT '0',
  `follower_count` int DEFAULT NULL,
  `introduction` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `admin_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`topic_id`),
  UNIQUE KEY `UK2edxcqonup7pq7l8g4vgpvugo` (`topic_name`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `topic_info`
--

LOCK TABLES `topic_info` WRITE;
/*!40000 ALTER TABLE `topic_info` DISABLE KEYS */;
INSERT INTO `topic_info` VALUES (1,NULL,NULL,0,'2026-01-09 10:00:00',NULL,'献鱼',0,0,NULL,NULL);
/*!40000 ALTER TABLE `topic_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `trial_balance_info`
--

DROP TABLE IF EXISTS `trial_balance_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `trial_balance_info` (
  `balance_id` int NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `days_balance` int DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`balance_id`),
  UNIQUE KEY `UK3u04ppny88d5wxoivaqn03dt7` (`user_id`),
  CONSTRAINT `FKd067i0cs45mjedj0jf64qcuw8` FOREIGN KEY (`user_id`) REFERENCES `user_info` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `trial_balance_info`
--

LOCK TABLES `trial_balance_info` WRITE;
/*!40000 ALTER TABLE `trial_balance_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `trial_balance_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `trial_reward_log`
--

DROP TABLE IF EXISTS `trial_reward_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `trial_reward_log` (
  `log_id` int NOT NULL AUTO_INCREMENT,
  `claimed_at` datetime(6) DEFAULT NULL,
  `days_granted` int NOT NULL,
  `reward_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`log_id`),
  UNIQUE KEY `UKo4oqddjyseon3wik23dfs3hhj` (`user_id`,`reward_type`),
  CONSTRAINT `FKqwenq7tus9eq7b7u5csw1ld3j` FOREIGN KEY (`user_id`) REFERENCES `user_info` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `trial_reward_log`
--

LOCK TABLES `trial_reward_log` WRITE;
/*!40000 ALTER TABLE `trial_reward_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `trial_reward_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_book_access_info`
--

DROP TABLE IF EXISTS `user_book_access_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_book_access_info` (
  `access_id` int NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `book_id` int NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`access_id`),
  UNIQUE KEY `UKfwbxj55sa0uw6v8gef47m3qj5` (`user_id`,`book_id`),
  KEY `FK13slfwn58x3esrndwmw31twml` (`book_id`),
  CONSTRAINT `FK13slfwn58x3esrndwmw31twml` FOREIGN KEY (`book_id`) REFERENCES `book_info` (`book_id`),
  CONSTRAINT `FKnbx53uytk8sc4gfxnd9cjq1oh` FOREIGN KEY (`user_id`) REFERENCES `user_info` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_book_access_info`
--

LOCK TABLES `user_book_access_info` WRITE;
/*!40000 ALTER TABLE `user_book_access_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_book_access_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_coin`
--

DROP TABLE IF EXISTS `user_coin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_coin` (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `coins` int NOT NULL DEFAULT '0' COMMENT '当前币数',
  `total_recharged_coins` int NOT NULL DEFAULT '0' COMMENT '累计充值币数',
  `total_consumed_coins` int NOT NULL DEFAULT '0' COMMENT '累计消费币数',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`user_id`),
  CONSTRAINT `user_coin_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user_info` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户币数表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_coin`
--

LOCK TABLES `user_coin` WRITE;
/*!40000 ALTER TABLE `user_coin` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_coin` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_info`
--

DROP TABLE IF EXISTS `user_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_info` (
  `user_id` bigint NOT NULL AUTO_INCREMENT,
  `avatar` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `bio` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `coins` int NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `follower_count` int DEFAULT NULL,
  `is_member` bit(1) NOT NULL,
  `member_end_date` datetime(6) NOT NULL,
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `phone` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `total_reading_time` int NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `username` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `following_count` int DEFAULT NULL,
  `last_login_time` datetime(6) DEFAULT NULL,
  `membership_expire_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `UKf2ksd6h8hsjtd57ipfq9myr64` (`username`),
  UNIQUE KEY `UK5m9cb1vslu82sm2y4nd2xaj4f` (`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_info`
--

LOCK TABLES `user_info` WRITE;
/*!40000 ALTER TABLE `user_info` DISABLE KEYS */;
INSERT INTO `user_info` VALUES (1,NULL,NULL,0,'2025-12-30 09:08:55.613717',0,_binary '\0','2025-12-30 09:14:09.000000','$2a$10$6G9qvcRKpwHveGlolhuQTOM4gx5uqT7BFeKnHpvoc05wZlvuIyBFK','15743499172',0,'2025-12-30 09:08:55.613717','嵇国栋',0,NULL,NULL),(2,NULL,NULL,0,'2025-12-30 09:24:00.952639',0,_binary '\0','2025-12-30 09:24:00.926475','$2a$10$lRR9Nakqw59gPFZhTXGh6e5fGqMDOnkguISDuF6Xk2J4d/WFK2BvW','16146612124',0,'2025-12-30 09:24:00.952639','林敬彪',0,NULL,NULL),(3,'https://avatars.githubusercontent.com/u/13274768','模特',0,'2025-12-30 10:33:37.285838',0,_binary '\0','2025-12-30 10:33:37.270402','$2a$10$exZzwk4n56waPGx5TaUTs.NcnxAYdHUqkE/E0WCoJ0p45eR4NFMjy','15593371442',0,'2026-01-10 22:02:03.966469','童宇航',0,NULL,NULL),(5,NULL,NULL,0,'2025-12-30 15:09:03.449356',0,_binary '\0','2025-12-30 15:09:03.417860','$2a$10$W3aePFeHxvOpKEOg9jbdPOyDRGlECkx4hEdCwQ2W4PkgSxzKdBCgm','16731705352',0,'2025-12-30 15:09:03.449356','堵奕辰',0,NULL,NULL),(6,NULL,NULL,0,'2026-01-10 14:45:20.818459',0,_binary '\0','2026-01-10 14:45:20.800621','$2a$10$rBWJ0wu7E/JjwUUkbYJiQ.cKMOGy4heBAzHWf9pX.Mj5Ss/XUUcjy','18900436421',0,'2026-01-10 14:45:20.818459','完阳',0,NULL,NULL),(10,'../../../../../data/pictures/default_avatar.jpg','这个人很懒，什么都没有写',0,'2026-01-10 14:58:13.628925',0,_binary '\0','2026-01-10 14:58:13.586613','$2a$10$tiwPD.Ry5aQ3RzN0xa4WW.PEEI4Y5sRm1zZvsF4eK2HKEtj131PXa','15216540849',0,'2026-01-10 14:58:13.628925','魏思佳',0,NULL,NULL);
/*!40000 ALTER TABLE `user_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_membership`
--

DROP TABLE IF EXISTS `user_membership`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_membership` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` int NOT NULL COMMENT '用户ID',
  `membership_type` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '会员类型',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态: 0-已过期, 1-有效, 2-已取消',
  `source_order_no` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '来源订单号',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  KEY `idx_end_time` (`end_time`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户会员表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_membership`
--

LOCK TABLES `user_membership` WRITE;
/*!40000 ALTER TABLE `user_membership` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_membership` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_reading_record`
--

DROP TABLE IF EXISTS `user_reading_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_reading_record` (
  `record_id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` int NOT NULL COMMENT '用户ID',
  `book_id` int DEFAULT NULL COMMENT '书籍ID',
  `book_title` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `read_date` date NOT NULL COMMENT '阅读日期',
  `reading_time` int NOT NULL DEFAULT '0' COMMENT '阅读时长（分钟）',
  `page_count` int DEFAULT '0' COMMENT '阅读页数',
  `chapter_id` int DEFAULT NULL COMMENT '章节ID',
  `chapter_title` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `record_type` int DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`record_id`),
  KEY `idx_user_date` (`user_id`,`read_date`),
  KEY `idx_user_book` (`user_id`,`book_id`),
  KEY `idx_read_date` (`read_date`),
  KEY `idx_user_type` (`user_id`,`record_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户阅读记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_reading_record`
--

LOCK TABLES `user_reading_record` WRITE;
/*!40000 ALTER TABLE `user_reading_record` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_reading_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_stat_info`
--

DROP TABLE IF EXISTS `user_stat_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_stat_info` (
  `user_stat_id` int NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `total_books_completed` int NOT NULL,
  `total_notes_count` int NOT NULL,
  `total_reading_duration_minutes` int NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`user_stat_id`),
  UNIQUE KEY `UKo7ncrqvvfpjkkb1jmgbv7m4vs` (`user_id`),
  CONSTRAINT `FKh7yw3x5o66kdko05ij635f6us` FOREIGN KEY (`user_id`) REFERENCES `user_info` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_stat_info`
--

LOCK TABLES `user_stat_info` WRITE;
/*!40000 ALTER TABLE `user_stat_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_stat_info` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-01-10 23:33:05
