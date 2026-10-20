CREATE DATABASE  IF NOT EXISTS `newsblog_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `newsblog_db`;
-- MySQL dump 10.13  Distrib 8.0.40, for Win64 (x86_64)
--
-- Host: localhost    Database: newsblog_db
-- ------------------------------------------------------
-- Server version	8.0.40

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
-- Table structure for table `article_analytics`
--

DROP TABLE IF EXISTS `article_analytics`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `article_analytics` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `avg_read_time` double DEFAULT NULL,
  `bounce_rate` double DEFAULT NULL,
  `bounces` int NOT NULL,
  `read_samples` int NOT NULL,
  `shares` bigint NOT NULL,
  `unique_views` bigint DEFAULT NULL,
  `views` bigint NOT NULL,
  `article_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKtm0kagosh8v4k9qi5793vk3o2` (`article_id`),
  CONSTRAINT `FKaoas051wo4cdatbwnq10tr2ut` FOREIGN KEY (`article_id`) REFERENCES `articles` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `article_analytics`
--

LOCK TABLES `article_analytics` WRITE;
/*!40000 ALTER TABLE `article_analytics` DISABLE KEYS */;
INSERT INTO `article_analytics` VALUES (1,95,28,0,0,0,899,1284,1),(2,95,28,0,0,0,669,956,2),(3,12,60,3,5,0,429,617,6),(4,95,28,0,0,0,238,340,7),(5,95,28,0,0,0,0,0,5),(6,0,0,0,0,0,0,0,8);
/*!40000 ALTER TABLE `article_analytics` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `article_tags`
--

DROP TABLE IF EXISTS `article_tags`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `article_tags` (
  `article_id` bigint NOT NULL,
  `tag_id` bigint NOT NULL,
  PRIMARY KEY (`article_id`,`tag_id`),
  KEY `FKr17guaxramkeyxq0f1xn3bxbw` (`tag_id`),
  CONSTRAINT `FKeoil73g36agokjw6vuklqgli3` FOREIGN KEY (`article_id`) REFERENCES `articles` (`id`),
  CONSTRAINT `FKr17guaxramkeyxq0f1xn3bxbw` FOREIGN KEY (`tag_id`) REFERENCES `tags` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `article_tags`
--

LOCK TABLES `article_tags` WRITE;
/*!40000 ALTER TABLE `article_tags` DISABLE KEYS */;
INSERT INTO `article_tags` VALUES (1,1),(3,1),(4,1),(5,1),(8,1),(1,2),(2,2),(6,2),(3,3),(5,4),(4,5),(2,6),(1,7),(7,8),(6,9),(8,11),(8,12);
/*!40000 ALTER TABLE `article_tags` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `article_views`
--

DROP TABLE IF EXISTS `article_views`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `article_views` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `ip_address` varchar(64) DEFAULT NULL,
  `viewed_at` datetime(6) DEFAULT NULL,
  `article_id` bigint NOT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_view_time` (`viewed_at`),
  KEY `FKcy2gs222qhspyjswnhhewweml` (`article_id`),
  KEY `FKchqnr7kjrpxlbtcurs6xj8p0j` (`user_id`),
  CONSTRAINT `FKchqnr7kjrpxlbtcurs6xj8p0j` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKcy2gs222qhspyjswnhhewweml` FOREIGN KEY (`article_id`) REFERENCES `articles` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=106 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `article_views`
--

LOCK TABLES `article_views` WRITE;
/*!40000 ALTER TABLE `article_views` DISABLE KEYS */;
INSERT INTO `article_views` VALUES (1,'10.0.0.189','2026-10-17 14:19:36.701464',1,NULL),(2,'10.0.0.55','2026-10-12 15:38:36.715192',1,NULL),(3,'10.0.0.7','2026-10-08 20:24:36.725642',1,NULL),(4,'10.0.0.170','2026-10-13 14:15:36.734583',1,NULL),(5,'10.0.0.64','2026-10-16 22:18:36.747092',1,NULL),(6,'10.0.0.193','2026-10-18 16:16:36.758941',1,NULL),(7,'10.0.0.190','2026-10-16 17:20:36.768062',1,NULL),(8,'10.0.0.111','2026-10-15 21:38:36.777717',1,NULL),(9,'10.0.0.160','2026-10-12 16:59:36.787670',1,NULL),(10,'10.0.0.38','2026-10-16 19:27:36.800003',1,NULL),(11,'10.0.0.55','2026-10-16 16:06:36.813410',1,NULL),(12,'10.0.0.164','2026-10-15 14:43:36.823667',1,NULL),(13,'10.0.0.21','2026-10-05 15:38:36.835283',1,NULL),(14,'10.0.0.16','2026-10-12 18:15:36.843800',1,NULL),(15,'10.0.0.138','2026-10-07 19:40:36.854591',1,NULL),(16,'10.0.0.21','2026-10-06 21:12:36.864617',1,NULL),(17,'10.0.0.158','2026-10-08 17:19:36.875311',1,NULL),(18,'10.0.0.77','2026-10-17 17:16:36.885338',1,NULL),(19,'10.0.0.146','2026-10-08 19:03:36.898331',1,NULL),(20,'10.0.0.105','2026-10-14 22:52:36.909113',1,NULL),(21,'10.0.0.152','2026-10-08 14:49:36.918538',1,NULL),(22,'10.0.0.199','2026-10-12 17:44:36.929953',1,NULL),(23,'10.0.0.115','2026-10-15 20:44:36.938344',1,NULL),(24,'10.0.0.60','2026-10-16 15:57:36.947140',1,NULL),(25,'10.0.0.59','2026-10-12 21:25:36.957735',1,NULL),(26,'10.0.0.173','2026-10-14 21:45:36.977207',2,NULL),(27,'10.0.0.190','2026-10-10 22:46:36.990562',2,NULL),(28,'10.0.0.120','2026-10-13 15:04:36.998377',2,NULL),(29,'10.0.0.95','2026-10-14 22:18:37.010585',2,NULL),(30,'10.0.0.15','2026-10-18 15:56:37.018591',2,NULL),(31,'10.0.0.68','2026-10-05 14:02:37.026905',2,NULL),(32,'10.0.0.32','2026-10-18 15:25:37.036485',2,NULL),(33,'10.0.0.200','2026-10-06 18:36:37.045894',2,NULL),(34,'10.0.0.15','2026-10-16 22:17:37.055633',2,NULL),(35,'10.0.0.154','2026-10-06 16:33:37.065655',2,NULL),(36,'10.0.0.27','2026-10-05 17:02:37.075333',2,NULL),(37,'10.0.0.31','2026-10-06 12:55:37.085853',2,NULL),(38,'10.0.0.16','2026-10-05 19:44:37.095921',2,NULL),(39,'10.0.0.3','2026-10-05 20:14:37.108048',2,NULL),(40,'10.0.0.20','2026-10-14 18:52:37.117011',2,NULL),(41,'10.0.0.135','2026-10-17 20:36:37.127782',2,NULL),(42,'10.0.0.168','2026-10-05 20:06:37.136772',2,NULL),(43,'10.0.0.99','2026-10-17 17:53:37.161635',2,NULL),(44,'10.0.0.110','2026-10-18 19:30:37.170219',2,NULL),(45,'10.0.0.71','2026-10-13 17:38:37.179026',2,NULL),(46,'10.0.0.63','2026-10-16 14:19:37.188386',2,NULL),(47,'10.0.0.135','2026-10-14 20:34:37.198561',2,NULL),(48,'10.0.0.127','2026-10-12 17:05:37.207329',2,NULL),(49,'10.0.0.192','2026-10-10 15:37:37.219268',2,NULL),(50,'10.0.0.89','2026-10-11 15:01:37.230722',2,NULL),(51,'10.0.0.109','2026-10-09 21:07:37.239384',2,NULL),(52,'10.0.0.20','2026-10-17 19:39:37.252451',2,NULL),(53,'10.0.0.173','2026-10-12 17:06:37.260594',2,NULL),(54,'10.0.0.166','2026-10-08 13:55:37.273199',2,NULL),(55,'10.0.0.36','2026-10-18 19:46:37.281394',2,NULL),(56,'10.0.0.136','2026-10-11 20:23:37.290285',2,NULL),(57,'10.0.0.44','2026-10-05 15:02:37.300253',2,NULL),(58,'10.0.0.65','2026-10-18 13:26:37.317250',2,NULL),(59,'10.0.0.110','2026-10-13 20:50:37.327770',2,NULL),(60,'10.0.0.58','2026-10-18 14:28:37.337734',2,NULL),(61,'10.0.0.25','2026-10-17 18:35:37.346991',2,NULL),(62,'10.0.0.148','2026-10-14 17:41:37.354534',2,NULL),(63,'10.0.0.146','2026-10-16 19:04:37.365988',2,NULL),(64,'10.0.0.77','2026-10-12 16:34:37.385247',6,NULL),(65,'10.0.0.42','2026-10-11 16:35:37.394062',6,NULL),(66,'10.0.0.5','2026-10-12 22:11:37.401498',6,NULL),(67,'10.0.0.109','2026-10-18 17:40:37.410759',6,NULL),(68,'10.0.0.112','2026-10-11 22:28:37.434664',6,NULL),(69,'10.0.0.54','2026-10-14 20:21:37.445739',6,NULL),(70,'10.0.0.59','2026-10-15 20:44:37.462447',6,NULL),(71,'10.0.0.98','2026-10-07 17:56:37.471064',6,NULL),(72,'10.0.0.59','2026-10-06 16:35:37.497078',6,NULL),(73,'10.0.0.32','2026-10-16 15:08:37.507219',6,NULL),(74,'10.0.0.65','2026-10-10 14:59:37.518212',6,NULL),(75,'10.0.0.165','2026-10-07 18:48:37.554028',7,NULL),(76,'10.0.0.181','2026-10-12 13:24:37.562871',7,NULL),(77,'10.0.0.50','2026-10-14 13:58:37.573832',7,NULL),(78,'10.0.0.1','2026-10-06 21:04:37.581022',7,NULL),(79,'10.0.0.112','2026-10-07 18:30:37.590026',7,NULL),(80,'10.0.0.162','2026-10-10 14:25:37.600798',7,NULL),(81,'10.0.0.80','2026-10-14 14:08:37.610869',7,NULL),(82,'10.0.0.84','2026-10-15 14:14:37.627934',7,NULL),(83,'10.0.0.5','2026-10-15 16:12:37.640215',7,NULL),(84,'10.0.0.177','2026-10-13 20:49:37.655320',7,NULL),(85,'10.0.0.74','2026-10-17 18:01:37.666342',7,NULL),(86,'10.0.0.136','2026-10-18 15:54:37.674701',7,NULL),(87,'10.0.0.107','2026-10-08 15:38:37.685673',7,NULL),(88,'10.0.0.126','2026-10-06 18:19:37.693495',7,NULL),(89,'10.0.0.48','2026-10-05 12:57:37.704178',7,NULL),(90,'10.0.0.113','2026-10-05 21:36:37.715318',7,NULL),(91,'10.0.0.53','2026-10-14 22:07:37.723503',7,NULL),(92,'10.0.0.195','2026-10-06 18:48:37.758558',7,NULL),(93,'10.0.0.53','2026-10-12 20:07:37.767394',7,NULL),(94,'10.0.0.27','2026-10-11 18:18:37.778601',7,NULL),(95,'10.0.0.126','2026-10-12 19:03:37.804094',7,NULL),(96,'10.0.0.73','2026-10-15 22:32:37.815193',7,NULL),(97,'10.0.0.106','2026-10-11 17:05:37.847943',7,NULL),(98,'10.0.0.55','2026-10-10 18:55:37.862479',7,NULL),(99,'10.0.0.121','2026-10-18 16:16:37.897915',7,NULL),(100,'10.0.0.181','2026-10-17 22:21:37.907426',7,NULL),(101,'0:0:0:0:0:0:0:1','2026-10-18 23:04:39.020136',6,1),(102,'0:0:0:0:0:0:0:1','2026-10-18 23:06:16.438516',6,1),(103,'0:0:0:0:0:0:0:1','2026-10-18 23:06:34.175848',6,1),(104,'0:0:0:0:0:0:0:1','2026-10-18 23:06:39.977627',6,1),(105,'0:0:0:0:0:0:0:1','2026-10-18 23:07:03.833843',6,1);
/*!40000 ALTER TABLE `article_views` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `articles`
--

DROP TABLE IF EXISTS `articles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `articles` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `content` longtext,
  `created_at` datetime(6) DEFAULT NULL,
  `excerpt` varchar(500) DEFAULT NULL,
  `featured` bit(1) NOT NULL,
  `featured_image` varchar(1000) DEFAULT NULL,
  `likes` bigint NOT NULL,
  `published_at` datetime(6) DEFAULT NULL,
  `reading_time` int DEFAULT NULL,
  `shares` bigint NOT NULL,
  `slug` varchar(255) NOT NULL,
  `status` enum('ARCHIVED','DRAFT','PENDING_REVIEW','PUBLISHED','SCHEDULED') NOT NULL,
  `title` varchar(255) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `views` bigint NOT NULL,
  `author_id` bigint NOT NULL,
  `category_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKsn7al9fwhgtf98rvn8nxhjt4f` (`slug`),
  KEY `idx_article_status` (`status`),
  KEY `idx_article_published` (`published_at`),
  KEY `FKe02fs2ut6qqoabfhj325wcjul` (`author_id`),
  KEY `FK7i4rryg7kqwyyrr08temnc71e` (`category_id`),
  CONSTRAINT `FK7i4rryg7kqwyyrr08temnc71e` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`),
  CONSTRAINT `FKe02fs2ut6qqoabfhj325wcjul` FOREIGN KEY (`author_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `articles`
--

LOCK TABLES `articles` WRITE;
/*!40000 ALTER TABLE `articles` DISABLE KEYS */;
INSERT INTO `articles` VALUES (1,'<p>Structure your project by feature, keep controllers thin and let services own the business rules.</p><h3>What to focus on</h3><ul><li>Write tests around services first</li><li>Use DTOs at the web boundary</li><li>Keep configuration in environment variables</li></ul><p>Start small, measure the result, and improve one thing at a time. Every section above can be applied to your own project this week.</p>','2026-10-18 22:52:36.492811','Structure your project by feature, keep controllers thin and let services own the business rules.',_binary '',NULL,32,'2026-10-07 22:52:36.491603',1,12,'10-best-java-full-stack-practices-for-2024','PUBLISHED','10 Best Java Full Stack Practices for 2024','2026-10-18 22:52:36.492811',1284,3,1),(2,'<p>Microservices trade simple deployment for independent scaling. Know the costs before you split a monolith.</p><h3>What to focus on</h3><ul><li>Service discovery and an API gateway</li><li>Circuit breakers for resilience</li><li>Distributed tracing from day one</li></ul><p>Start small, measure the result, and improve one thing at a time. Every section above can be applied to your own project this week.</p>','2026-10-18 22:52:36.529845','Microservices trade simple deployment for independent scaling. Know the costs before you split a monolith.',_binary '',NULL,23,'2026-10-06 22:52:36.529845',1,9,'spring-boot-microservices-a-complete-guide','PUBLISHED','Spring Boot Microservices: A Complete Guide','2026-10-18 22:52:36.529845',956,4,1),(3,'<p>Most slow Hibernate apps suffer from the N+1 query problem. Here is how to find and fix it.</p><h3>What to focus on</h3><ul><li>Fetch joins and entity graphs</li><li>Batch fetching</li><li>Second-level cache basics</li></ul><p>Start small, measure the result, and improve one thing at a time. Every section above can be applied to your own project this week.</p>','2026-10-18 22:52:36.545311','Most slow Hibernate apps suffer from the N+1 query problem. Here is how to find and fix it.',_binary '\0',NULL,0,NULL,1,0,'hibernate-performance-tuning-best-practices','DRAFT','Hibernate Performance Tuning: Best Practices','2026-10-18 22:52:36.613105',0,3,1),(4,'<p>Good APIs are predictable. Use the right HTTP methods, status codes and error format.</p><h3>What to focus on</h3><ul><li>Version your API</li><li>Return consistent error bodies</li><li>Document with OpenAPI</li></ul><p>Start small, measure the result, and improve one thing at a time. Every section above can be applied to your own project this week.</p>','2026-10-18 22:52:36.632592','Good APIs are predictable. Use the right HTTP methods, status codes and error format.',_binary '\0',NULL,0,'2026-10-04 22:52:36.631530',1,0,'rest-api-best-practices-for-java-developers','PENDING_REVIEW','REST API Best Practices for Java Developers','2026-10-18 22:52:36.632592',0,2,2),(5,'<p>A container gives your app the same environment everywhere. Start with a small Dockerfile.</p><h3>What to focus on</h3><ul><li>Multi-stage builds</li><li>docker-compose for MySQL</li><li>Health checks</li></ul><p>Start small, measure the result, and improve one thing at a time. Every section above can be applied to your own project this week.</p>','2026-10-18 22:52:36.647408','A container gives your app the same environment everywhere. Start with a small Dockerfile.',_binary '\0',NULL,0,'2026-10-20 22:52:36.646408',1,0,'docker-for-java-developers-getting-started','SCHEDULED','Docker for Java Developers: Getting Started','2026-10-18 22:52:36.647408',0,4,3),(6,'<p>Authentication proves who you are; authorization decides what you may do. Spring Security separates the two cleanly.</p><h3>What to focus on</h3><ul><li>SecurityFilterChain beans replace WebSecurityConfigurerAdapter</li><li>Hash passwords with BCrypt</li><li>Protect forms with CSRF tokens</li></ul><p>Start small, measure the result, and improve one thing at a time. Every section above can be applied to your own project this week.</p>','2026-10-18 22:52:36.661932','Authentication proves who you are; authorization decides what you may do. Spring Security separates the two cleanly.',_binary '',NULL,16,'2026-10-15 22:52:36.660926',1,6,'spring-security-6-in-plain-english','PUBLISHED','Spring Security 6 in Plain English','2026-10-18 22:52:36.661932',617,3,1),(7,'<p>Your README is the first thing a reviewer opens. Explain what the project does and how to run it in five minutes.</p><h3>What to focus on</h3><ul><li>Add screenshots and a demo link</li><li>List setup steps and environment variables</li><li>Explain design decisions</li></ul><p>Start small, measure the result, and improve one thing at a time. Every section above can be applied to your own project this week.</p>','2026-10-18 22:52:36.678390','Your README is the first thing a reviewer opens. Explain what the project does and how to run it in five minutes.',_binary '\0',NULL,8,'2026-10-16 22:52:36.677221',1,3,'how-to-write-a-readme-that-impresses-reviewers','PUBLISHED','How to Write a README That Impresses Reviewers','2026-10-18 22:52:36.678390',340,4,4),(8,'<p></p>\n<div>\n <div>\n  <div>\n   <h1><span>The</span> <span>Flaws</span> <span>of</span> <span>Vibe</span> <span>Coding:</span> <span>The</span> <span>Hidden</span> <span>Cost</span> <span>of</span> <span>Building</span> <span>Software</span> <span>with</span> <span>AI<br><br></span></h1>\n   <h2><span>Introduction</span></h2>\n   <p><span>Vibe</span> <span>coding</span> <span>is</span> <span>a</span> <span>modern</span> <span>approach</span> <span>to</span> <span>software</span> <span>development</span> <span>in</span> <span>which</span> <span>developers</span> <span>use</span> <span>artificial</span> <span>intelligence</span> <span>(AI)</span> <span>tools</span> <span>to</span> <span>generate</span> <span>code</span> <span>through</span> <span>natural-language</span> <span>prompts.</span> <span>Instead</span> <span>of</span> <span>manually</span> <span>writing</span> <span>every</span> <span>line,</span> <span>developers</span> <span>describe</span> <span>what</span> <span>they</span> <span>want,</span> <span>and</span> <span>AI</span> <span>generates</span> <span>the</span> <span>code</span> <span>needed</span> <span>to</span> <span>build</span> <span>applications,</span> <span>websites,</span> <span>and</span> <span>software</span> <span>products.</span></p>\n   <p><span>Vibe</span> <span>coding</span> <span>has</span> <span>made</span> <span>software</span> <span>development</span> <span>faster</span> <span>and</span> <span>more</span> <span>accessible,</span> <span>especially</span> <span>for</span> <span>beginners,</span> <span>start up</span> <span>founders,</span> <span>and</span> <span>people</span> <span>with</span> <span>limited</span> <span>programming</span> <span>experience.</span> <span>However,</span> <span>behind</span> <span>its</span> <span>convenience</span> <span>lies</span> <span>a</span> <span>set</span> <span>of</span> <span>serious</span> <span>flaws</span> <span>that</span> <span>can</span> <span>affect</span> <span>code</span> <span>quality,</span> <span>security,</span> <span>maintainability,</span> <span>and</span> <span>long-term</span> <span>project</span> <span>success.</span></p>\n   <p><span>While</span> <span>AI</span> <span>can</span> <span>help</span> <span>turn</span> <span>ideas</span> <span>into</span> <span>working</span> <span>products</span> <span>quickly,</span> <span>generating</span> <span>code</span> <span>is</span> <span>not</span> <span>the</span> <span>same</span> <span>as</span> <span>understanding</span> <span>how</span> <span>software</span> <span>should</span> <span>work.</span> <span>Recognizing</span> <span>the</span> <span>limitations</span> <span>of</span> <span>vibe</span> <span>coding</span> <span>is</span> <span>essential</span> <span>for</span> <span>anyone</span> <span>who</span> <span>wants</span> <span>to</span> <span>build</span> <span>reliable</span> <span>applications.</span></p>\n   <h2><span>1.</span> <span>Lack</span> <span>of</span> <span>Understanding</span> <span>of</span> <span>Generated</span> <span>Code</span></h2>\n   <p><span>One</span> <span>of</span> <span>the</span> <span>biggest</span> <span>flaws</span> <span>of</span> <span>vibe</span> <span>coding</span> <span>is</span> <span>that</span> <span>developers</span> <span>may</span> <span>use</span> <span>code</span> <span>without</span> <span>fully</span> <span>understanding</span> <span>how</span> <span>it</span> <span>works.</span> <span>AI</span> <span>can</span> <span>generate</span> <span>complex</span> <span>functions,</span> <span>database</span> <span>queries,</span> <span>authentication</span> <span>systems,</span> <span>and</span> <span>application</span> <span>logic</span> <span>within</span> <span>seconds.</span></p>\n   <p><span>Although</span> <span>this</span> <span>can</span> <span>save</span> <span>time,</span> <span>it</span> <span>creates</span> <span>a</span> <span>problem</span> <span>when</span> <span>something</span> <span>goes</span> <span>wrong.</span> <span>Developers</span> <span>who</span> <span>do</span> <span>not</span> <span>understand</span> <span>the</span> <span>generated</span> <span>code</span> <span>may</span> <span>struggle</span> <span>to</span> <span>identify</span> <span>bugs,</span> <span>fix</span> <span>errors,</span> <span>or</span> <span>improve</span> <span>the</span> <span>application.&nbsp;</span>An application might work perfectly during a demonstration but fail when users interact with it in unexpected ways. Without a clear understanding of the underlying implementation, debugging becomes difficult and time-consuming.</p>\n   <h2><span>2.</span> <span>Security</span> <span>Vulnerabilities</span></h2>\n   <p><span>Security</span> <span>is</span> <span>another</span> <span>major</span> <span>concern</span> <span>in</span> <span>vibe</span> <span>coding.</span> <span>AI-generated</span> <span>code</span> <span>can</span> <span>contain</span> <span>vulnerabilities</span> <span>such</span> <span>as</span> <span>improper</span> <span>input</span> <span>validation,</span> <span>insecure</span> <span>authentication,</span> <span>exposed</span> <span>API</span> <span>keys,</span> <span>weak</span> <span>access</span> <span>controls,</span> <span>and</span> <span>unsafe</span> <span>database</span> <span>operations.&nbsp;</span>For example, an AI tool might generate a login system that appears functional but does not correctly protect sensitive user information. A developer who focuses only on whether the application works may overlook these security weaknesses. Even a small vulnerability can expose personal data, compromise accounts, or damage a business\'s reputation. Therefore, AI-generated code should always undergo security reviews and appropriate testing before deployment.</p>\n   <h2><span>3.</span> <span>Poor</span> <span>Code</span> <span>Quality</span> <span>and</span> <span>Structure</span></h2>\n   <p><span>AI-generated</span> <span>code</span> <span>is</span> <span>not</span> <span>always</span> <span>clean,</span> <span>efficient,</span> <span>or</span> <span>well-organized.</span> <span>In</span> <span>some</span> <span>cases,</span> <span>it</span> <span>may</span> <span>produce</span> <span>duplicated</span> <span>functions,</span> <span>unnecessary</span> <span>dependencies,</span> <span>overly</span> <span>complicated</span> <span>logic,</span> <span>or</span> <span>inconsistent</span> <span>coding</span> <span>patterns.&nbsp;</span>These problems may not be noticeable in small projects. However, as an application grows, poorly structured code becomes harder to understand and maintain.</p>\n   <p><span>A</span> <span>project</span> <span>that</span> <span>initially</span> <span>takes</span> <span>only</span> <span>a</span> <span>few</span> <span>prompts</span> <span>to</span> <span>build</span> <span>may</span> <span>eventually</span> <span>require</span> <span>significant</span> <span>effort</span> <span>to</span> <span>modify.</span> <span>Developers</span> <span>may</span> <span>find</span> <span>themselves</span> <span>fixing</span> <span>one</span> <span>problem</span> <span>only</span> <span>to</span> <span>create</span> <span>another</span> <span>because</span> <span>different</span> <span>parts</span> <span>of</span> <span>the</span> <span>application</span> <span>were</span> <span>generated</span> <span>without</span> <span>a</span> <span>consistent</span> <span>architectural</span> <span>plan.</span></p>\n   <h2><span>4.</span> <span>Difficulty</span> <span>in</span> <span>Debugging</span></h2>\n   <p><span>Vibe</span> <span>coding</span> <span>can</span> <span>make</span> <span>debugging</span> <span>particularly</span> <span>challenging.</span> <span>When</span> <span>developers</span> <span>repeatedly</span> <span>ask</span> <span>AI</span> <span>to</span> <span>fix</span> <span>errors</span> <span>without</span> <span>understanding</span> <span>their</span> <span>root</span> <span>causes,</span> <span>they</span> <span>may</span> <span>end</span> <span>up</span> <span>applying</span> <span>temporary</span> <span>solutions</span> <span>instead</span> <span>of</span> <span>correcting</span> <span>the</span> <span>underlying</span> <span>problem.</span></p>\n   <p><span>One</span> <span>fix</span> <span>might</span> <span>introduce</span> <span>new</span> <span>bugs</span> <span>elsewhere</span> <span>in</span> <span>the</span> <span>application.</span> <span>Repeated</span> <span>changes</span> <span>can</span> <span>also</span> <span>create</span> <span>confusing</span> <span>code</span> <span>that</span> <span>becomes</span> <span>increasingly</span> <span>difficult</span> <span>to</span> <span>maintain.</span></p>\n   <p><span>This</span> <span>process</span> <span>is</span> <span>sometimes</span> <span>called</span> <span>a</span> <span>debugging</span> <span>loop,</span> <span>where</span> <span>the</span> <span>AI</span> <span>repeatedly</span> <span>attempts</span> <span>to</span> <span>correct</span> <span>an</span> <span>issue</span> <span>but</span> <span>introduces</span> <span>new</span> <span>problems</span> <span>in</span> <span>the</span> <span>process.&nbsp;</span>Effective debugging requires more than generating another solution. It requires understanding the error, identifying its cause, testing possible fixes, and verifying that the solution works as intended.</p>\n   <h2><span>5.</span> <span>Overdependence</span> <span>on</span> <span>Artificial</span> <span>Intelligence</span></h2>\n   <p><span>Another</span> <span>flaw</span> <span>of</span> <span>vibe</span> <span>coding</span> <span>is</span> <span>the</span> <span>risk</span> <span>of</span> <span>becoming</span> <span>overly</span> <span>dependent</span> <span>on</span> <span>AI</span> <span>tools.</span> <span>When</span> <span>developers</span> <span>rely</span> <span>on</span> <span>AI</span> <span>for</span> <span>every</span> <span>coding</span> <span>decision,</span> <span>they</span> <span>may</span> <span>miss</span> <span>opportunities</span> <span>to</span> <span>develop</span> <span>essential</span> <span>programming</span> <span>skills.</span></p>\n   <p><span>Problem-solving,</span> <span>logical</span> <span>reasoning,</span> <span>system</span> <span>design,</span> <span>debugging,</span> <span>and</span> <span>algorithmic</span> <span>thinking</span> <span>remain</span> <span>important</span> <span>parts</span> <span>of</span> <span>software</span> <span>development.</span> <span>These</span> <span>skills</span> <span>help</span> <span>developers</span> <span>recognize</span> <span>incorrect</span> <span>suggestions</span> <span>and</span> <span>make</span> <span>better</span> <span>technical</span> <span>decisions.&nbsp;</span>AI should function as an assistant rather than a replacement for critical thinking. Developers who understand programming fundamentals are better equipped to evaluate generated code and determine whether it is appropriate for their projects.</p>\n   <h2><span>6.</span> <span>Hallucinations</span> <span>and</span> <span>Incorrect</span> <span>Solutions</span></h2>\n   <p><span>AI</span> <span>systems</span> <span>can</span> <span>generate</span> <span>code</span> <span>that</span> <span>looks</span> <span>convincing</span> <span>but</span> <span>is</span> <span>incorrect.</span> <span>They</span> <span>may</span> <span>suggest</span> <span>functions</span> <span>that</span> <span>do</span> <span>not</span> <span>exist,</span> <span>use</span> <span>outdated</span> <span>APIs,</span> <span>misunderstand</span> <span>project</span> <span>requirements,</span> <span>or</span> <span>recommend</span> <span>libraries</span> <span>that</span> <span>are</span> <span>incompatible</span> <span>with</span> <span>the</span> <span>existing</span> <span>application.</span></p>\n   <p><span>Because</span> <span>the</span> <span>generated</span> <span>output</span> <span>often</span> <span>appears</span> <span>professional,</span> <span>inexperienced</span> <span>developers</span> <span>may</span> <span>assume</span> <span>it</span> <span>is</span> <span>correct</span> <span>without</span> <span>verifying</span> <span>it.</span></p>\n   <p><span>This</span> <span>can</span> <span>lead</span> <span>to</span> <span>wasted</span> <span>time,</span> <span>unexpected</span> <span>errors,</span> <span>and</span> <span>unreliable</span> <span>software.</span> <span>The</span> <span>solution</span> <span>is</span> <span>to</span> <span>validate</span> <span>AI-generated</span> <span>suggestions</span> <span>against</span> <span>official</span> <span>documentation,</span> <span>run</span> <span>automated</span> <span>tests,</span> <span>and</span> <span>verify</span> <span>actual</span> <span>application</span> <span>behavior</span> <span>instead</span> <span>of</span> <span>trusting</span> <span>the</span> <span>output</span> <span>blindly.</span></p>\n   <h2><span>7.</span> <span>Scalability</span> <span>and</span> <span>Performance</span> <span>Problems</span></h2>\n   <p><span>An</span> <span>application</span> <span>that</span> <span>works</span> <span>for</span> <span>a</span> <span>few</span> <span>users</span> <span>may</span> <span>not</span> <span>perform</span> <span>well</span> <span>when</span> <span>thousands</span> <span>of</span> <span>users</span> <span>access</span> <span>it</span> <span>simultaneously.</span></p>\n   <p><span>AI-generated</span> <span>implementations</span> <span>may</span> <span>overlook</span> <span>important</span> <span>considerations</span> <span>such</span> <span>as</span> <span>database</span> <span>indexing,</span> <span>caching,</span> <span>memory</span> <span>usage,</span> <span>network</span> <span>requests,</span> <span>concurrency,</span> <span>and</span> <span>efficient</span> <span>data</span> <span>processing.</span></p>\n   <p><span>For</span> <span>a</span> <span>small</span> <span>prototype,</span> <span>these</span> <span>limitations</span> <span>may</span> <span>not</span> <span>matter</span> <span>immediately.</span> <span>For</span> <span>a</span> <span>production</span> <span>application,</span> <span>however,</span> <span>they</span> <span>can</span> <span>cause</span> <span>slow</span> <span>response</span> <span>times,</span> <span>server</span> <span>failures,</span> <span>increased</span> <span>infrastructure</span> <span>costs,</span> <span>and</span> <span>a</span> <span>poor</span> <span>user</span> <span>experience.</span></p>\n   <p><span>Scalability</span> <span>must</span> <span>be</span> <span>considered</span> <span>during</span> <span>system</span> <span>design</span> <span>rather</span> <span>than</span> <span>treated</span> <span>as</span> <span>an</span> <span>afterthought.</span></p>\n   <h2><span>8.</span> <span>Technical</span> <span>Debt</span></h2>\n   <p><span>Technical</span> <span>debt</span> <span>occurs</span> <span>when</span> <span>short-term</span> <span>development</span> <span>decisions</span> <span>create</span> <span>additional</span> <span>work</span> <span>in</span> <span>the</span> <span>future.</span> <span>Vibe</span> <span>coding</span> <span>can</span> <span>increase</span> <span>technical</span> <span>debt</span> <span>when</span> <span>developers</span> <span>prioritize</span> <span>quick</span> <span>results</span> <span>over</span> <span>maintainable</span> <span>architecture.</span></p>\n   <p><span>For</span> <span>example,</span> <span>repeatedly</span> <span>asking</span> <span>AI</span> <span>to</span> <span>add</span> <span>features</span> <span>without</span> <span>reviewing</span> <span>the</span> <span>existing</span> <span>implementation</span> <span>can</span> <span>lead</span> <span>to</span> <span>duplicated</span> <span>code,</span> <span>inconsistent</span> <span>data</span> <span>models,</span> <span>unnecessary</span> <span>dependencies,</span> <span>and</span> <span>fragile</span> <span>integrations.&nbsp;</span>As the project grows, developers may spend more time fixing earlier shortcuts than building new features. The speed gained during the initial development stage can therefore become a long-term maintenance burden if the code is not reviewed and improved regularly.</p>\n   <h2><span>9.</span> <span>Inconsistent</span> <span>User</span> <span>Experience</span></h2>\n   <p><span>AI</span> <span>can</span> <span>generate</span> <span>interfaces</span> <span>quickly,</span> <span>but</span> <span>it</span> <span>may</span> <span>not</span> <span>fully</span> <span>understand</span> <span>the</span> <span>needs,</span> <span>expectations,</span> <span>and</span> <span>behavior</span> <span>of</span> <span>real</span> <span>users.&nbsp;</span>A vibe-coded application might contain inconsistent spacing, confusing navigation, inaccessible controls, unclear error messages, or forms that behave unpredictably. An interface may look visually impressive while still being difficult to use. Good user experience requires research, thoughtful design decisions, usability testing, and feedback from actual users. AI can assist with these tasks, but it cannot guarantee that a product will meet every user\'s needs simply because the interface looks attractive.</p>\n   <h2><span>10.</span> <span>Hidden</span> <span>Costs</span> <span>and</span> <span>False</span> <span>Productivity</span></h2>\n   <p><span>Vibe</span> <span>coding</span> <span>creates</span> <span>the</span> <span>impression</span> <span>that</span> <span>software</span> <span>development</span> <span>has</span> <span>become</span> <span>almost</span> <span>effortless.</span> <span>However,</span> <span>rapid</span> <span>code</span> <span>generation</span> <span>does</span> <span>not</span> <span>eliminate</span> <span>the</span> <span>need</span> <span>for</span> <span>planning,</span> <span>testing,</span> <span>reviewing,</span> <span>deploying,</span> <span>and</span> <span>maintaining</span> <span>software.</span></p>\n   <p><span>Developers</span> <span>may</span> <span>spend</span> <span>less</span> <span>time</span> <span>writing</span> <span>code</span> <span>but</span> <span>more</span> <span>time</span> <span>correcting</span> <span>errors,</span> <span>understanding</span> <span>generated</span> <span>implementations,</span> <span>resolving</span> <span>dependency</span> <span>conflicts,</span> <span>and</span> <span>fixing</span> <span>unexpected</span> <span>behavior.</span></p>\n   <p><span>There</span> <span>may</span> <span>also</span> <span>be</span> <span>costs</span> <span>associated</span> <span>with</span> <span>AI</span> <span>subscriptions,</span> <span>API</span> <span>usage,</span> <span>cloud</span> <span>infrastructure,</span> <span>and</span> <span>third-party</span> <span>services.</span></p>\n   <p><span>The</span> <span>real</span> <span>measure</span> <span>of</span> <span>productivity</span> <span>is</span> <span>not</span> <span>how</span> <span>quickly</span> <span>an</span> <span>application</span> <span>is</span> <span>generated,</span> <span>but</span> <span>how</span> <span>reliably</span> <span>it</span> <span>works</span> <span>and</span> <span>how</span> <span>much</span> <span>effort</span> <span>is</span> <span>required</span> <span>to</span> <span>maintain</span> <span>it</span> <span>over</span> <span>time.</span></p>\n   <h2><span>11.</span> <span>Privacy</span> <span>and</span> <span>Data</span> <span>Protection</span> <span>Risks</span></h2>\n   <p><span>When</span> <span>developers</span> <span>share</span> <span>source</span> <span>code,</span> <span>database</span> <span>structures,</span> <span>API</span> <span>credentials,</span> <span>or</span> <span>sensitive</span> <span>business</span> <span>information</span> <span>with</span> <span>AI</span> <span>tools,</span> <span>they</span> <span>must</span> <span>consider</span> <span>the</span> <span>privacy</span> <span>and</span> <span>data-handling</span> <span>policies</span> <span>of</span> <span>those</span> <span>services.</span></p>\n   <p><span>Accidentally</span> <span>including</span> <span>passwords,</span> <span>access</span> <span>tokens,</span> <span>customer</span> <span>information,</span> <span>or</span> <span>confidential</span> <span>business</span> <span>logic</span> <span>in</span> <span>prompts</span> <span>can</span> <span>create</span> <span>unnecessary</span> <span>risks.</span></p>\n   <p><span>Developers</span> <span>should</span> <span>follow</span> <span>their</span> <span>organization\'s</span> <span>security</span> <span>policies,</span> <span>avoid</span> <span>sharing</span> <span>sensitive</span> <span>information</span> <span>unnecessarily,</span> <span>use</span> <span>environment</span> <span>variables</span> <span>or</span> <span>secret-management</span> <span>systems</span> <span>for</span> <span>credentials,</span> <span>and</span> <span>review</span> <span>the</span> <span>permissions</span> <span>granted</span> <span>to</span> <span>AI-powered</span> <span>development</span> <span>tools.</span></p>\n   <p><span>Privacy</span> <span>should</span> <span>be</span> <span>considered</span> <span>throughout</span> <span>the</span> <span>development</span> <span>process,</span> <span>not</span> <span>only</span> <span>after</span> <span>the</span> <span>application</span> <span>is</span> <span>completed.</span></p>\n   <h2><span>12.</span> <span>The</span> <span>Illusion</span> <span>of</span> <span>Expertise</span></h2>\n   <p><span>Perhaps</span> <span>the</span> <span>most</span> <span>subtle</span> <span>flaw</span> <span>of</span> <span>vibe</span> <span>coding</span> <span>is</span> <span>that</span> <span>it</span> <span>can</span> <span>make</span> <span>someone</span> <span>feel</span> <span>like</span> <span>an</span> <span>experienced</span> <span>software</span> <span>developer</span> <span>before</span> <span>they</span> <span>have</span> <span>developed</span> <span>the</span> <span>necessary</span> <span>technical</span> <span>judgment.</span></p>\n   <p><span>A</span> <span>person</span> <span>may</span> <span>successfully</span> <span>create</span> <span>a</span> <span>website,</span> <span>mobile</span> <span>application,</span> <span>or</span> <span>dashboard</span> <span>by</span> <span>writing</span> <span>prompts</span> <span>without</span> <span>understanding</span> <span>databases,</span> <span>networking,</span> <span>authentication,</span> <span>deployment,</span> <span>or</span> <span>software</span> <span>architecture.&nbsp;</span>Building a prototype is a meaningful achievement, but it does not automatically mean the application is secure, scalable, or production-ready. The danger is not that beginners use AI. The danger is assuming that successful code generation eliminates the need for technical knowledge and responsible engineering.</p>\n   <h2><span>How</span> <span>to</span> <span>Avoid</span> <span>the</span> <span>Flaws</span> <span>of</span> <span>Vibe</span> <span>Coding</span></h2>\n   <p><span>Vibe</span> <span>coding</span> <span>can</span> <span>be</span> <span>useful</span> <span>when</span> <span>combined</span> <span>with</span> <span>sound</span> <span>development</span> <span>practices.</span> <span>Developers</span> <span>can</span> <span>reduce</span> <span>its</span> <span>risks</span> <span>by</span> <span>following</span> <span>a</span> <span>few</span> <span>important</span> <span>principles:</span></p>\n   <ul>\n    <li>\n     <div>\n      <div>\n       <p><span><span>Understand</span> <span>before</span> <span>accepting:</span></span> <span>Review</span> <span>the</span> <span>generated</span> <span>code</span> <span>and</span> <span>make</span> <span>sure</span> <span>you</span> <span>understand</span> <span>its</span> <span>purpose.</span></p>\n      </div>\n     </div></li>\n    <li>\n     <div>\n      <div>\n       <p><span><span>Test</span> <span>every</span> <span>important</span> <span>feature:</span></span> <span>Use</span> <span>unit</span> <span>tests,</span> <span>integration</span> <span>tests,</span> <span>and</span> <span>end-to-end</span> <span>tests</span> <span>where</span> <span>appropriate.</span></p>\n      </div>\n     </div></li>\n    <li>\n     <div>\n      <div>\n       <p><span><span>Prioritize</span> <span>security:</span></span> <span>Validate</span> <span>inputs,</span> <span>protect</span> <span>credentials,</span> <span>enforce</span> <span>authorization,</span> <span>and</span> <span>review</span> <span>dependencies.</span></p>\n      </div>\n     </div></li>\n    <li>\n     <div>\n      <div>\n       <p><span><span>Build</span> <span>incrementally:</span></span> <span>Add</span> <span>features</span> <span>in</span> <span>small</span> <span>steps</span> <span>instead</span> <span>of</span> <span>generating</span> <span>an</span> <span>entire</span> <span>complex</span> <span>application</span> <span>in</span> <span>one</span> <span>prompt.</span></p>\n      </div>\n     </div></li>\n    <li>\n     <div>\n      <div>\n       <p><span><span>Use</span> <span>version</span> <span>control:</span></span> <span>Keep</span> <span>changes</span> <span>traceable</span> <span>and</span> <span>make</span> <span>it</span> <span>possible</span> <span>to</span> <span>revert</span> <span>faulty</span> <span>implementations.</span></p>\n      </div>\n     </div></li>\n    <li>\n     <div>\n      <div>\n       <p><span><span>Review</span> <span>architecture:</span></span> <span>Ensure</span> <span>that</span> <span>the</span> <span>application\'s</span> <span>structure</span> <span>remains</span> <span>consistent</span> <span>as</span> <span>new</span> <span>features</span> <span>are</span> <span>introduced.</span></p>\n      </div>\n     </div></li>\n    <li>\n     <div>\n      <div>\n       <p><span><span>Verify</span> <span>AI</span> <span>suggestions:</span></span> <span>Check</span> <span>official</span> <span>documentation</span> <span>and</span> <span>test</span> <span>the</span> <span>behavior</span> <span>of</span> <span>generated</span> <span>code.</span></p>\n      </div>\n     </div></li>\n    <li>\n     <div>\n      <div>\n       <p><span><span>Monitor</span> <span>production</span> <span>systems:</span></span> <span>Track</span> <span>errors,</span> <span>performance,</span> <span>security</span> <span>issues,</span> <span>and</span> <span>user</span> <span>feedback</span> <span>after</span> <span>deployment.</span></p>\n      </div>\n     </div></li>\n    <li>\n     <div>\n      <div>\n       <p><span><span>Keep</span> <span>learning:</span></span> <span>Develop</span> <span>programming</span> <span>fundamentals</span> <span>so</span> <span>that</span> <span>AI</span> <span>becomes</span> <span>a</span> <span>tool</span> <span>for</span> <span>improving</span> <span>your</span> <span>work</span> <span>rather</span> <span>than</span> <span>a</span> <span>substitute</span> <span>for</span> <span>understanding</span> <span>it.</span></p>\n      </div>\n     </div></li>\n   </ul>\n   <h2><span>Conclusion</span></h2>\n   <p><span>Vibe</span> <span>coding</span> <span>represents</span> <span>an</span> <span>important</span> <span>shift</span> <span>in</span> <span>software</span> <span>development.</span> <span>It</span> <span>allows</span> <span>people</span> <span>to</span> <span>transform</span> <span>ideas</span> <span>into</span> <span>working</span> <span>prototypes</span> <span>quickly</span> <span>and</span> <span>lowers</span> <span>the</span> <span>barrier</span> <span>to</span> <span>creating</span> <span>digital</span> <span>products.</span> <span>However,</span> <span>its</span> <span>convenience</span> <span>can</span> <span>hide</span> <span>serious</span> <span>problems</span> <span>involving</span> <span>security,</span> <span>code</span> <span>quality,</span> <span>debugging,</span> <span>scalability,</span> <span>and</span> <span>long-term</span> <span>maintenance.</span></p>\n   <p><span>The</span> <span>central</span> <span>issue</span> <span>is</span> <span>not</span> <span>the</span> <span>use</span> <span>of</span> <span>AI</span> <span>itself,</span> <span>but</span> <span>the</span> <span>assumption</span> <span>that</span> <span>generated</span> <span>code</span> <span>can</span> <span>be</span> <span>trusted</span> <span>without</span> <span>verification.</span> <span>Software</span> <span>must</span> <span>do</span> <span>more</span> <span>than</span> <span>run</span> <span>successfully</span> <span>once.</span> <span>It</span> <span>must</span> <span>remain</span> <span>secure,</span> <span>reliable,</span> <span>understandable,</span> <span>and</span> <span>maintainable</span> <span>as</span> <span>requirements</span> <span>change</span> <span>and</span> <span>users</span> <span>increase.</span></p>\n   <p><span>The</span> <span>most</span> <span>effective</span> <span>approach</span> <span>is</span> <span>to</span> <span>combine</span> <span>the</span> <span>speed</span> <span>of</span> <span>AI</span> <span>with</span> <span>the</span> <span>judgment</span> <span>of</span> <span>a</span> <span>skilled</span> <span>developer.</span> <span>AI</span> <span>can</span> <span>generate</span> <span>solutions,</span> <span>suggest</span> <span>improvements,</span> <span>and</span> <span>accelerate</span> <span>repetitive</span> <span>work,</span> <span>but</span> <span>humans</span> <span>must</span> <span>remain</span> <span>responsible</span> <span>for</span> <span>evaluating</span> <span>the</span> <span>results.</span></p>\n   <p><span><span>Vibe</span> <span>coding</span> <span>can</span> <span>help</span> <span>you</span> <span>build</span> <span>software</span> <span>faster,</span> <span>but</span> <span>understanding</span> <span>what</span> <span>you</span> <span>build</span> <span>is</span> <span>what</span> <span>makes</span> <span>that</span> <span>software</span> <span>trustworthy.</span></span></p>\n  </div>\n </div>\n <div></div>\n</div>','2026-10-18 23:14:40.672511','The Flaws of Vibe Coding: The Hidden Cost of Building Software with AI Introduction Vibe coding is a modern approach to software development in which developers use artificial intelligence (AI) tools…',_binary '\0',NULL,0,NULL,9,0,'flaws-of-vibe-coding','DRAFT','Flaws Of Vibe Coding','2026-10-18 23:14:40.672511',0,1,NULL);
/*!40000 ALTER TABLE `articles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `categories`
--

DROP TABLE IF EXISTS `categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categories` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `description` varchar(500) DEFAULT NULL,
  `name` varchar(100) NOT NULL,
  `slug` varchar(120) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKt8o6pivur7nn124jehx7cygw5` (`name`),
  UNIQUE KEY `UKoul14ho7bctbefv8jywp5v3i2` (`slug`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categories`
--

LOCK TABLES `categories` WRITE;
/*!40000 ALTER TABLE `categories` DISABLE KEYS */;
INSERT INTO `categories` VALUES (1,'2026-10-18 22:52:36.240965','Core Java, Spring Boot and the wider ecosystem.','Java & Spring','java-spring'),(2,'2026-10-18 22:52:36.254300','Frontend, backend and everything between.','Web Development','web-development'),(3,'2026-10-18 22:52:36.265232','Build, ship and run software reliably.','DevOps','devops'),(4,'2026-10-18 22:52:36.273991','Internships, interviews and growing as a developer.','Career','career');
/*!40000 ALTER TABLE `categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comments`
--

DROP TABLE IF EXISTS `comments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `comment` varchar(2000) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `status` enum('APPROVED','PENDING','REJECTED') NOT NULL,
  `article_id` bigint NOT NULL,
  `parent_id` bigint DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKk4ib6syde10dalk7r7xdl0m5p` (`article_id`),
  KEY `FKlri30okf66phtcgbe5pok7cc0` (`parent_id`),
  KEY `FK8omq0tc18jd43bu5tjh6jvraq` (`user_id`),
  CONSTRAINT `FK8omq0tc18jd43bu5tjh6jvraq` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKk4ib6syde10dalk7r7xdl0m5p` FOREIGN KEY (`article_id`) REFERENCES `articles` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FKlri30okf66phtcgbe5pok7cc0` FOREIGN KEY (`parent_id`) REFERENCES `comments` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comments`
--

LOCK TABLES `comments` WRITE;
/*!40000 ALTER TABLE `comments` DISABLE KEYS */;
INSERT INTO `comments` VALUES (1,'Great article! Very helpful for my current project.','2026-10-18 22:52:37.962608','APPROVED',1,NULL,5),(2,'Thanks Michael, glad it helped!','2026-10-18 22:52:37.974531','APPROVED',1,1,3),(3,'Could you add more examples for Spring Security configuration?','2026-10-18 22:52:38.007867','PENDING',2,NULL,5),(4,'This saved me hours of debugging! Thank you.','2026-10-18 22:52:38.039977','APPROVED',6,NULL,5),(5,'good info','2026-10-18 23:07:03.792810','APPROVED',6,NULL,1);
/*!40000 ALTER TABLE `comments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `newsletter_subscribers`
--

DROP TABLE IF EXISTS `newsletter_subscribers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `newsletter_subscribers` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `is_active` bit(1) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `subscribed_at` datetime(6) DEFAULT NULL,
  `verification_token` varchar(255) DEFAULT NULL,
  `verified_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKqqdefkuupml4s7190ettcy6jy` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `newsletter_subscribers`
--

LOCK TABLES `newsletter_subscribers` WRITE;
/*!40000 ALTER TABLE `newsletter_subscribers` DISABLE KEYS */;
INSERT INTO `newsletter_subscribers` VALUES (1,_binary '','john.doe@email.com','2026-10-18 22:52:38.048370','0b1ff6b4-44af-4ebd-97b5-3a5222b90f3c','2026-10-18 22:52:38.047356'),(2,_binary '','sarah.smith@email.com','2026-10-18 22:52:38.060200','d7b93989-9b15-404c-8b1e-225355fe3a20',NULL),(3,_binary '','michael.brown@email.com','2026-10-18 22:52:38.069842','59a9b66b-0e1c-49eb-8824-b185d6e112c2','2026-10-18 22:52:38.068840'),(4,_binary '','emily.davis@email.com','2026-10-18 22:52:38.077670','f78945f1-ae73-4007-ba41-dd9ef03eef09','2026-10-18 22:52:38.076657');
/*!40000 ALTER TABLE `newsletter_subscribers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `newsletters`
--

DROP TABLE IF EXISTS `newsletters`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `newsletters` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `content` longtext,
  `recipient_count` int DEFAULT NULL,
  `sent_at` datetime(6) DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL,
  `subject` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `newsletters`
--

LOCK TABLES `newsletters` WRITE;
/*!40000 ALTER TABLE `newsletters` DISABLE KEYS */;
/*!40000 ALTER TABLE `newsletters` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `saved_articles`
--

DROP TABLE IF EXISTS `saved_articles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `saved_articles` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `saved_at` datetime(6) DEFAULT NULL,
  `article_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKi7ismr3qc5mqnpxrllgmv2lnj` (`user_id`,`article_id`),
  KEY `FKc6gggdjh7xkyxfmeadwy6r2ai` (`article_id`),
  CONSTRAINT `FK875p08fpsgr0habmgygppye4h` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKc6gggdjh7xkyxfmeadwy6r2ai` FOREIGN KEY (`article_id`) REFERENCES `articles` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `saved_articles`
--

LOCK TABLES `saved_articles` WRITE;
/*!40000 ALTER TABLE `saved_articles` DISABLE KEYS */;
INSERT INTO `saved_articles` VALUES (1,'2026-10-18 23:06:39.935966',6,1);
/*!40000 ALTER TABLE `saved_articles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tags`
--

DROP TABLE IF EXISTS `tags`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tags` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `name` varchar(50) NOT NULL,
  `slug` varchar(60) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKt48xdq560gs3gap9g7jg36kgc` (`name`),
  UNIQUE KEY `UKsn0d91hxu700qcw0n4pebp5vc` (`slug`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tags`
--

LOCK TABLES `tags` WRITE;
/*!40000 ALTER TABLE `tags` DISABLE KEYS */;
INSERT INTO `tags` VALUES (1,'2026-10-18 22:52:36.286915','Java','java'),(2,'2026-10-18 22:52:36.304668','Spring Boot','spring-boot'),(3,'2026-10-18 22:52:36.314648','Hibernate','hibernate'),(4,'2026-10-18 22:52:36.325733','Docker','docker'),(5,'2026-10-18 22:52:36.333900','REST API','rest-api'),(6,'2026-10-18 22:52:36.347784','Microservices','microservices'),(7,'2026-10-18 22:52:36.362038','MySQL','mysql'),(8,'2026-10-18 22:52:36.374484','Internship','internship'),(9,'2026-10-18 22:52:36.383804','Security','security'),(10,'2026-10-18 22:59:49.347486','name','name'),(11,'2026-10-18 23:14:40.663598','python','python'),(12,'2026-10-18 23:14:40.670511','c++','c');
/*!40000 ALTER TABLE `tags` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `avatar` varchar(255) DEFAULT NULL,
  `bio` varchar(1000) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `enabled` bit(1) NOT NULL,
  `name` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `reset_token` varchar(255) DEFAULT NULL,
  `reset_token_expiry` datetime(6) DEFAULT NULL,
  `role` enum('ADMIN','AUTHOR','EDITOR','READER') NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,NULL,'Runs the platform.','2026-10-18 22:52:35.421874','admin@newsblog.com',_binary '','Site Admin','$2a$10$dSEbqFgZW.0Zh.v7lkbbg.xFlWW4nkoIrloq9jODuxHmD343jnYi2',NULL,NULL,'ADMIN','2026-10-18 22:52:35.421874'),(2,NULL,'Editor who reviews and publishes stories.','2026-10-18 22:52:35.728668','editor@newsblog.com',_binary '','Emily Davis','$2a$10$0iErsgtghjNtKf59ufnrF..v3Frag/u.ZnPewOksn6Kh.SWfk3Y.u',NULL,NULL,'EDITOR','2026-10-18 22:52:35.728668'),(3,NULL,'Full stack developer writing about Java and Spring.','2026-10-18 22:52:35.887456','author@newsblog.com',_binary '','John Smith','$2a$10$3xgWAcZY2me2j8rpQbKLwOvK9Ee8F7w5pqwJa834LnC3Ff06Opxju',NULL,NULL,'AUTHOR','2026-10-18 22:52:35.887456'),(4,NULL,'Backend engineer. Microservices and cloud.','2026-10-18 22:52:36.064728','sarah@newsblog.com',_binary '','Sarah Johnson','$2a$10$np1CTodnbyhp.1qcIecrSeXrNktmRtxtjWxKpUkyo93CStsrujXZ2',NULL,NULL,'AUTHOR','2026-10-18 22:52:36.064728'),(5,NULL,NULL,'2026-10-18 22:52:36.215144','reader@newsblog.com',_binary '','Michael Brown','$2a$10$ouy2padNTBAVFF9CQoUm8uQAAgZYykFHvK3T87uWoFeob09q80JES',NULL,NULL,'READER','2026-10-18 22:52:36.215144');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-10-20 10:25:20
