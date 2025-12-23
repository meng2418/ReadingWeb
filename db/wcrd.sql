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
  PRIMARY KEY (`author_id`),
  UNIQUE KEY `UKme4hv1oktmw9rcvvrssvvv1g7` (`name`)
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
-- Table structure for table `book_info`
--

DROP TABLE IF EXISTS `book_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `book_info` (
  `book_id` int NOT NULL AUTO_INCREMENT,
  `author_id` bigint NOT NULL,
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comment_info`
--

LOCK TABLES `comment_info` WRITE;
/*!40000 ALTER TABLE `comment_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `comment_info` ENABLE KEYS */;
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
  UNIQUE KEY `UK595t55apajqp5yij34vfdx1sa` (`follower_id`,`following_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `follow_info`
--

LOCK TABLES `follow_info` WRITE;
/*!40000 ALTER TABLE `follow_info` DISABLE KEYS */;
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
  PRIMARY KEY (`like_id`),
  UNIQUE KEY `UKl6ppu8vkat29xy2uavaetqgit` (`user_id`,`post_id`,`comment_id`,`note_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `like_info`
--

LOCK TABLES `like_info` WRITE;
/*!40000 ALTER TABLE `like_info` DISABLE KEYS */;
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
  `updated_at` datetime(6) NOT NULL,
  `author_id` bigint NOT NULL,
  `comments_count` int NOT NULL,
  `content` longtext COLLATE utf8mb4_unicode_ci,
  `likes_count` int NOT NULL,
  `status` int NOT NULL,
  `title` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`post_id`),
  KEY `FKponab5iwtu4gtiirkxx9hf2gw` (`author_id`),
  CONSTRAINT `FKponab5iwtu4gtiirkxx9hf2gw` FOREIGN KEY (`author_id`) REFERENCES `user_info` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `post_info`
--

LOCK TABLES `post_info` WRITE;
/*!40000 ALTER TABLE `post_info` DISABLE KEYS */;
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
  `topic_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
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
-- Table structure for table `topic_info`
--

DROP TABLE IF EXISTS `topic_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `topic_info` (
  `topic_id` int NOT NULL AUTO_INCREMENT,
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`topic_id`),
  UNIQUE KEY `UKgd6b0a6mdpxc55qbibre2cldc` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `topic_info`
--

LOCK TABLES `topic_info` WRITE;
/*!40000 ALTER TABLE `topic_info` DISABLE KEYS */;
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
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `UKf2ksd6h8hsjtd57ipfq9myr64` (`username`),
  UNIQUE KEY `UK5m9cb1vslu82sm2y4nd2xaj4f` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_info`
--

LOCK TABLES `user_info` WRITE;
/*!40000 ALTER TABLE `user_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_info` ENABLE KEYS */;
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

-- Dump completed on 2025-12-16 15:24:24
