-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: localhost    Database: readingweb_db
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
-- Table structure for table `author_info`
--

DROP TABLE IF EXISTS `author_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `author_info` (
  `authorid` int NOT NULL AUTO_INCREMENT,
  `authorName` varchar(45) NOT NULL DEFAULT '佚名',
  `description` varchar(1000) DEFAULT '暂无简介',
  PRIMARY KEY (`authorid`),
  UNIQUE KEY `authorName_UNIQUE` (`authorName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
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
  `bookid` int NOT NULL AUTO_INCREMENT,
  `title` varchar(500) NOT NULL DEFAULT '无标题',
  `authorid` int NOT NULL,
  `description` varchar(140) NOT NULL DEFAULT '暂无简介',
  `cover_url` varchar(500) NOT NULL DEFAULT '无封面',
  `wordNumber` mediumint(8) unsigned zerofill NOT NULL,
  `publishTime` date DEFAULT NULL,
  `categoryid` int DEFAULT NULL,
  PRIMARY KEY (`bookid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `book_info`
--

LOCK TABLES `book_info` WRITE;
/*!40000 ALTER TABLE `book_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `book_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bookchapter_info`
--

DROP TABLE IF EXISTS `bookchapter_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bookchapter_info` (
  `chapterid` int NOT NULL AUTO_INCREMENT,
  `bookid` int NOT NULL,
  `chapterNumber` int unsigned DEFAULT NULL,
  `content` tinytext,
  PRIMARY KEY (`chapterid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bookchapter_info`
--

LOCK TABLES `bookchapter_info` WRITE;
/*!40000 ALTER TABLE `bookchapter_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `bookchapter_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bookrating_info`
--

DROP TABLE IF EXISTS `bookrating_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bookrating_info` (
  `ratingid` int NOT NULL AUTO_INCREMENT,
  `userid` int NOT NULL,
  `bookid` int NOT NULL,
  `status` enum('recommend','average','notrecomend') NOT NULL DEFAULT 'average',
  PRIMARY KEY (`ratingid`),
  UNIQUE KEY `unique_rating_user_book` (`userid`,`bookid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bookrating_info`
--

LOCK TABLES `bookrating_info` WRITE;
/*!40000 ALTER TABLE `bookrating_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `bookrating_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bookreview_info`
--

DROP TABLE IF EXISTS `bookreview_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bookreview_info` (
  `reviewid` int NOT NULL AUTO_INCREMENT,
  `bookid` int NOT NULL,
  `userid` int NOT NULL,
  `content` varchar(1000) NOT NULL,
  `is_spoiled` tinyint DEFAULT '0',
  PRIMARY KEY (`reviewid`),
  KEY `userid_idx` (`userid`),
  KEY `bookid_idx` (`bookid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bookreview_info`
--

LOCK TABLES `bookreview_info` WRITE;
/*!40000 ALTER TABLE `bookreview_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `bookreview_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bookshelf_info`
--

DROP TABLE IF EXISTS `bookshelf_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bookshelf_info` (
  `shelfid` int NOT NULL AUTO_INCREMENT,
  `bookid` int NOT NULL,
  `userid` int NOT NULL,
  `status` enum('notread','reading','read') NOT NULL DEFAULT 'notread',
  PRIMARY KEY (`shelfid`),
  UNIQUE KEY `unique_shelf_user_book` (`userid`,`bookid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
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
  `categoryid` int NOT NULL AUTO_INCREMENT,
  `categoryName` varchar(45) NOT NULL DEFAULT '未命名',
  `descrpition` varchar(140) DEFAULT NULL,
  PRIMARY KEY (`categoryid`),
  UNIQUE KEY `categoryName_UNIQUE` (`categoryName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `category_info`
--

LOCK TABLES `category_info` WRITE;
/*!40000 ALTER TABLE `category_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `category_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `follow_info`
--

DROP TABLE IF EXISTS `follow_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `follow_info` (
  `followingid` int NOT NULL,
  `followerid` int NOT NULL,
  PRIMARY KEY (`followingid`,`followerid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `follow_info`
--

LOCK TABLES `follow_info` WRITE;
/*!40000 ALTER TABLE `follow_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `follow_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `post_info`
--

DROP TABLE IF EXISTS `post_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `post_info` (
  `postid` int NOT NULL AUTO_INCREMENT,
  `userid` int NOT NULL,
  `title` varchar(45) NOT NULL,
  `content` varchar(300) NOT NULL,
  PRIMARY KEY (`postid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `post_info`
--

LOCK TABLES `post_info` WRITE;
/*!40000 ALTER TABLE `post_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `post_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `postcomment_info`
--

DROP TABLE IF EXISTS `postcomment_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `postcomment_info` (
  `commentid` int NOT NULL AUTO_INCREMENT,
  `postid` int NOT NULL,
  `content` varchar(300) DEFAULT NULL,
  `is_fathercomment` int(10) unsigned zerofill NOT NULL,
  `fathercommentid` int DEFAULT NULL,
  PRIMARY KEY (`commentid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `postcomment_info`
--

LOCK TABLES `postcomment_info` WRITE;
/*!40000 ALTER TABLE `postcomment_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `postcomment_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `postlike_info`
--

DROP TABLE IF EXISTS `postlike_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `postlike_info` (
  `likeid` int NOT NULL AUTO_INCREMENT,
  `postid` int NOT NULL,
  `userid` int NOT NULL,
  PRIMARY KEY (`likeid`),
  UNIQUE KEY `unique_like_user_post` (`userid`,`postid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `postlike_info`
--

LOCK TABLES `postlike_info` WRITE;
/*!40000 ALTER TABLE `postlike_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `postlike_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `postrepost_info`
--

DROP TABLE IF EXISTS `postrepost_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `postrepost_info` (
  `repostid` int NOT NULL,
  `fatherpostid` int NOT NULL,
  PRIMARY KEY (`repostid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `postrepost_info`
--

LOCK TABLES `postrepost_info` WRITE;
/*!40000 ALTER TABLE `postrepost_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `postrepost_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `posttag_info`
--

DROP TABLE IF EXISTS `posttag_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `posttag_info` (
  `postid` int NOT NULL,
  `tagid` int NOT NULL,
  PRIMARY KEY (`postid`,`tagid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `posttag_info`
--

LOCK TABLES `posttag_info` WRITE;
/*!40000 ALTER TABLE `posttag_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `posttag_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `readingsession_info`
--

DROP TABLE IF EXISTS `readingsession_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `readingsession_info` (
  `sessionid` int NOT NULL AUTO_INCREMENT,
  `userid` int NOT NULL,
  `startTime` datetime NOT NULL,
  `endTime` datetime DEFAULT NULL,
  PRIMARY KEY (`sessionid`),
  KEY `userid_idx` (`userid`),
  CONSTRAINT `userid` FOREIGN KEY (`userid`) REFERENCES `user_info` (`userid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `readingsession_info`
--

LOCK TABLES `readingsession_info` WRITE;
/*!40000 ALTER TABLE `readingsession_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `readingsession_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tag_info`
--

DROP TABLE IF EXISTS `tag_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tag_info` (
  `tagid` int NOT NULL AUTO_INCREMENT,
  `tagName` varchar(45) NOT NULL DEFAULT '未命名',
  `descrpition` varchar(300) DEFAULT NULL,
  PRIMARY KEY (`tagid`),
  UNIQUE KEY `tagName_UNIQUE` (`tagName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tag_info`
--

LOCK TABLES `tag_info` WRITE;
/*!40000 ALTER TABLE `tag_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `tag_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_info`
--

DROP TABLE IF EXISTS `user_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_info` (
  `userid` int NOT NULL,
  `nickname` varchar(45) NOT NULL DEFAULT 'momo',
  `gender` enum('male','female','other','prefer_not_to_say') NOT NULL DEFAULT 'prefer_not_to_say',
  `phone` varchar(45) DEFAULT '未绑定手机',
  `password` varchar(20) NOT NULL DEFAULT '123456',
  `avatar_url` varchar(500) DEFAULT NULL,
  `bio` varchar(140) DEFAULT NULL,
  PRIMARY KEY (`userid`),
  UNIQUE KEY `phone_UNIQUE` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_info`
--

LOCK TABLES `user_info` WRITE;
/*!40000 ALTER TABLE `user_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_info` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-10-09 16:07:58
