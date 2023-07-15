CREATE DATABASE  IF NOT EXISTS `sakancom_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `sakancom_db`;
-- MySQL dump 10.13  Distrib 8.0.33, for Win64 (x86_64)
--
-- Host: localhost    Database: sakancom_db
-- ------------------------------------------------------
-- Server version	8.0.33

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
-- Table structure for table `admin`
--

DROP TABLE IF EXISTS `admin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `admin` (
  `admin_id` int unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `password` varchar(260) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`admin_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin`
--

LOCK TABLES `admin` WRITE;
/*!40000 ALTER TABLE `admin` DISABLE KEYS */;
INSERT INTO `admin` VALUES (1,'Admin','d033e22ae348aeb5660fc2140aec35850c4da997','0592793930','amroosous@gmail.com');
/*!40000 ALTER TABLE `admin` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `furniture`
--

DROP TABLE IF EXISTS `furniture`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `furniture` (
  `furniture_id` int unsigned NOT NULL AUTO_INCREMENT,
  `tenant_id` int unsigned NOT NULL,
  `name` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `price` int NOT NULL,
  PRIMARY KEY (`furniture_id`),
  KEY `furniture_forkey` (`tenant_id`),
  CONSTRAINT `furniture_forkey` FOREIGN KEY (`tenant_id`) REFERENCES `tenants` (`tenant_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1188 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `furniture`
--

LOCK TABLES `furniture` WRITE;
/*!40000 ALTER TABLE `furniture` DISABLE KEYS */;
INSERT INTO `furniture` VALUES (1167,1004,'Sofa','Comfortable sofa with leather upholstery',500),(1168,1005,'Dining Table','Solid wood dining table with six chairs',800),(1169,1004,'Bed Frame','Queen-sized bed frame with storage drawers',600),(1170,1004,'Bookshelf','Tall bookshelf with adjustable shelves',300),(1171,1005,'TV Stand','Modern TV stand with built-in media storage',250),(1172,1006,'Office Chair','Ergonomic office chair with lumbar support',200),(1173,1006,'Desk','Spacious computer desk with keyboard tray',400),(1174,1006,'File Cabinet','Lockable file cabinet with two drawers',150),(1175,1006,'Bookcase','Low bookcase with open shelves',150),(1176,1007,'Coffee Table','Glass coffee table with metal frame',150),(1177,1004,'TV Stand','Rustic-style TV stand with barn doors',350),(1178,1007,'Accent Chair','Upholstered accent chair with patterned fabric',300),(1179,1007,'Side Table','Small side table with a drawer',100),(1180,1008,'Dresser','Six-drawer dresser with a mirror',700),(1181,1008,'Nightstand','Bedside table with two drawers',150),(1182,1008,'Wardrobe','Large wardrobe with sliding doors',900),(1183,1009,'Sectional Sofa','Modular sectional sofa with chaise',1200),(1184,1009,'Coffee Table','Marble top coffee table with gold legs',350),(1185,1009,'TV Stand','Floating TV stand with LED lighting',400),(1186,1009,'Bar Stools','Set of four modern bar stools',300),(1187,1010,'Bed Frame','King-sized bed frame with tufted headboard',800);
/*!40000 ALTER TABLE `furniture` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `housing`
--

DROP TABLE IF EXISTS `housing`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `housing` (
  `housing_id` int unsigned NOT NULL AUTO_INCREMENT,
  `owner_id` int unsigned NOT NULL,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `location` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `rent` int NOT NULL,
  `water_inclusive` tinyint NOT NULL DEFAULT '0',
  `electricity_inclusive` tinyint NOT NULL DEFAULT '0',
  `services` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `floors` int NOT NULL,
  `apart_per_floor` int NOT NULL,
  `available` tinyint NOT NULL DEFAULT '0',
  `picture` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`housing_id`),
  UNIQUE KEY `picture_UNIQUE` (`picture`),
  KEY `housing_tenant_forkey_idx` (`owner_id`),
  CONSTRAINT `housing_tenant_forkey` FOREIGN KEY (`owner_id`) REFERENCES `owners` (`owner_id`)
) ENGINE=InnoDB AUTO_INCREMENT=165 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `housing`
--

LOCK TABLES `housing` WRITE;
/*!40000 ALTER TABLE `housing` DISABLE KEYS */;
INSERT INTO `housing` VALUES (151,11006,'City Apartment','123 Main Street, City A',1000,1,1,'Gym, Swimming Pool, Parking',5,4,1,'city_apartment.jpg'),(152,11006,'Country farmhouse','456 Park Avenue, City B',3000,0,1,'Garden, Terrace, Security',2,1,1,'country_farmhouse.jpg'),(153,11007,'Cozy Cottage','789 Elm Road, City C',1500,1,0,'Roof Deck, Concierge Service',10,2,0,'cozy_cottage.jpeg'),(154,11007,'Family Home','321 Oak Street, City D',800,1,0,'Fireplace, Garden, Pet-Friendly',1,1,1,'family_home.jpeg'),(155,11008,'Garden Bungalow','987 Maple Lane, City E',5000,1,1,'Private Elevator, Panoramic View',20,1,0,'garden_bungalow.jpg'),(156,11008,'Historic Townhouse','654 Pine Avenue, City F',900,1,1,'Laundry, Balcony',8,6,1,'historic_townhouse.jpg'),(157,11009,'Lakeview Villa','789 Elm Road, City C',2000,1,1,'Parking, Community Center',3,2,1,'lakeview_villa.png'),(158,11009,'Luxury Penthouse','123 Main Street, City A',1200,0,1,'Backyard, Storage',2,1,1,'luxury_penthouse.jpeg'),(159,11010,'Modern Loft','987 Maple Lane, City E',2500,1,0,'Private Dock, Patio',1,1,1,'modern_loft.jpg'),(160,11010,'Mountain Chalet','456 Park Avenue, City B',1800,1,1,'Exposed Brick, High Ceilings',6,4,0,'mountain_chalet.jpg'),(161,11011,'Riverside Retreat','654 Pine Avenue, City F',4000,1,1,'Infinity Pool, Ocean View',1,1,1,'riverside_retreat.jpg'),(162,11011,'Suburban House','789 Elm Road, City C',1200,1,0,'Garden, Fireplace',2,1,0,'suburban_house.jpeg'),(163,11012,'Urban Studio','123 Main Street, City A',1800,1,1,'Hiking Trails, Scenic Views',1,1,1,'urban_studio.jpg'),(164,11012,'Beachfront Resort','456 Park Avenue, City B',1100,0,1,'Nearby Shops, Public Transit',15,12,1,'beachfront_resort.jpg');
/*!40000 ALTER TABLE `housing` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary view structure for view `invoice`
--

DROP TABLE IF EXISTS `invoice`;
/*!50001 DROP VIEW IF EXISTS `invoice`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `invoice` AS SELECT 
 1 AS `reservation_id`,
 1 AS `tenant_id`,
 1 AS `housing_id`,
 1 AS `reservation_date`,
 1 AS `floor_num`,
 1 AS `apart_num`,
 1 AS `accepted`,
 1 AS `tenant_name`,
 1 AS `tenant_age`,
 1 AS `tenant_email`,
 1 AS `tenant_phone`,
 1 AS `university_major`,
 1 AS `owner_name`,
 1 AS `owner_phone`,
 1 AS `owner_email`,
 1 AS `housing_name`,
 1 AS `location`,
 1 AS `owner_id`,
 1 AS `rent`,
 1 AS `water_inclusive`,
 1 AS `electricity_inclusive`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `owners`
--

DROP TABLE IF EXISTS `owners`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `owners` (
  `owner_id` int unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `password` varchar(260) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`owner_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11021 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `owners`
--

LOCK TABLES `owners` WRITE;
/*!40000 ALTER TABLE `owners` DISABLE KEYS */;
INSERT INTO `owners` VALUES (11006,'Amro Sous','4297f44b13955235245b2497399d7a93','0592793930','amroosous@gmail.com'),(11007,'Emily Smith','4297f44b13955235245b2497399d7a93','0592222222','emily.smith@example.com'),(11008,'Michael Johnson','4297f44b13955235245b2497399d7a93','0593333333','michael.johnson@example.com'),(11009,'Olivia Davis','4297f44b13955235245b2497399d7a93','0594444444','olivia.davis@example.com'),(11010,'William Martinez','4297f44b13955235245b2497399d7a93','0595555555','william.martinez@example.com'),(11011,'Sophia Thompson','4297f44b13955235245b2497399d7a93','0596666666','sophia.thompson@example.com'),(11012,'Daniel Anderson','4297f44b13955235245b2497399d7a93','0597777777','daniel.anderson@example.com'),(11013,'Ava White','4297f44b13955235245b2497399d7a93','0598888888','ava.white@example.com'),(11014,'James Garcia','4297f44b13955235245b2497399d7a93','0599999999','james.garcia@example.com'),(11015,'Emma Rodriguez','4297f44b13955235245b2497399d7a93','0591010101','emma.rodriguez@example.com'),(11016,'Noah Hernandez','4297f44b13955235245b2497399d7a93','0591111112','noah.hernandez@example.com'),(11017,'Isabella Wilson','4297f44b13955235245b2497399d7a93','0591111113','isabella.wilson@example.com'),(11018,'Liam Taylor','4297f44b13955235245b2497399d7a93','0591111114','liam.taylor@example.com'),(11019,'Mia Brown','4297f44b13955235245b2497399d7a93','0591111115','mia.brown@example.com'),(11020,'Alexander Clark','4297f44b13955235245b2497399d7a93','0591111116','alexander.clark@example.com');
/*!40000 ALTER TABLE `owners` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reservations`
--

DROP TABLE IF EXISTS `reservations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reservations` (
  `reservation_id` int unsigned NOT NULL AUTO_INCREMENT,
  `tenant_id` int unsigned NOT NULL,
  `housing_id` int unsigned NOT NULL,
  `reservation_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `floor_num` int NOT NULL,
  `apart_num` int NOT NULL,
  `accepted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`reservation_id`),
  KEY `reservation_tenant_forkey` (`tenant_id`),
  KEY `reservation_housing_forkey` (`housing_id`),
  CONSTRAINT `reservation_housing_forkey` FOREIGN KEY (`housing_id`) REFERENCES `housing` (`housing_id`),
  CONSTRAINT `reservation_tenant_forkey` FOREIGN KEY (`tenant_id`) REFERENCES `tenants` (`tenant_id`)
) ENGINE=InnoDB AUTO_INCREMENT=239 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reservations`
--

LOCK TABLES `reservations` WRITE;
/*!40000 ALTER TABLE `reservations` DISABLE KEYS */;
INSERT INTO `reservations` VALUES (200,1004,151,'2023-07-15 15:12:58',3,2,1),(201,1006,153,'2023-07-15 15:12:58',5,6,1),(202,1007,154,'2023-07-15 15:12:58',1,1,0),(203,1008,155,'2023-07-15 15:12:58',10,1,1),(204,1009,156,'2023-07-15 15:12:58',2,4,0),(205,1010,157,'2023-07-15 15:12:58',3,1,1),(206,1011,158,'2023-07-15 15:12:58',1,2,1),(207,1012,159,'2023-07-15 15:12:58',1,1,1),(208,1013,160,'2023-07-15 15:12:58',4,3,0),(209,1014,161,'2023-07-15 15:12:58',1,1,1),(210,1005,162,'2023-07-15 15:12:58',2,1,0),(211,1004,163,'2023-07-15 15:12:58',1,1,1),(212,1005,164,'2023-07-15 15:12:58',7,9,1),(213,1005,152,'2023-07-15 15:12:58',1,1,1),(214,1004,153,'2023-07-15 15:12:58',8,2,0),(215,1007,155,'2023-07-15 15:12:58',15,1,1),(216,1008,156,'2023-07-15 15:12:58',2,1,1),(217,1009,157,'2023-07-15 15:12:58',3,1,0),(218,1010,158,'2023-07-15 15:12:58',1,1,1),(219,1011,160,'2023-07-15 15:12:58',6,3,1),(220,1012,161,'2023-07-15 15:12:58',1,1,1),(221,1013,162,'2023-07-15 15:12:58',2,1,0),(222,1014,163,'2023-07-15 15:12:58',1,1,1),(223,1010,164,'2023-07-15 15:12:58',10,5,1),(224,1006,151,'2023-07-15 15:12:58',5,3,1),(225,1007,152,'2023-07-15 15:12:58',1,1,0);
/*!40000 ALTER TABLE `reservations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tenants`
--

DROP TABLE IF EXISTS `tenants`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tenants` (
  `tenant_id` int unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `password` varchar(260) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `age` int DEFAULT NULL,
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `university_major` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`tenant_id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=1022 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tenants`
--

LOCK TABLES `tenants` WRITE;
/*!40000 ALTER TABLE `tenants` DISABLE KEYS */;
INSERT INTO `tenants` VALUES (1004,'Amro Sous','4297f44b13955235245b2497399d7a93',21,'0592793930','amroosous@gmail.com','Computer Engineer'),(1005,'John Smith','4297f44b13955235245b2497399d7a93',21,'0591111111','john.smith@example.com','Computer Science'),(1006,'Emily Johnson','4297f44b13955235245b2497399d7a93',25,'0592222222','emily.johnson@example.com','Business Administration'),(1007,'Michael Davis','4297f44b13955235245b2497399d7a93',30,'0593333333','michael.davis@example.com','Mechanical Engineering'),(1008,'Olivia Martinez','4297f44b13955235245b2497399d7a93',28,'0594444444','olivia.martinez@example.com','Psychology'),(1009,'William Wilson','4297f44b13955235245b2497399d7a93',23,'0595555555','william.wilson@example.com','Electrical Engineering'),(1010,'Sophia Thompson','4297f44b13955235245b2497399d7a93',26,'0596666666','sophia.thompson@example.com','Marketing'),(1011,'Daniel Anderson','4297f44b13955235245b2497399d7a93',29,'0597777777','daniel.anderson@example.com','Finance'),(1012,'Ava White','4297f44b13955235245b2497399d7a93',27,'0598888888','ava.white@example.com','Biology'),(1013,'James Garcia','4297f44b13955235245b2497399d7a93',24,'0599999999','james.garcia@example.com','Political Science'),(1014,'Emma Rodriguez','4297f44b13955235245b2497399d7a93',31,'0591010101','emma.rodriguez@example.com','English Literature');
/*!40000 ALTER TABLE `tenants` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Final view structure for view `invoice`
--

/*!50001 DROP VIEW IF EXISTS `invoice`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `invoice` AS select `reservations`.`reservation_id` AS `reservation_id`,`reservations`.`tenant_id` AS `tenant_id`,`reservations`.`housing_id` AS `housing_id`,`reservations`.`reservation_date` AS `reservation_date`,`reservations`.`floor_num` AS `floor_num`,`reservations`.`apart_num` AS `apart_num`,`reservations`.`accepted` AS `accepted`,`tenants`.`name` AS `tenant_name`,`tenants`.`age` AS `tenant_age`,`tenants`.`email` AS `tenant_email`,`tenants`.`phone` AS `tenant_phone`,`tenants`.`university_major` AS `university_major`,`owners`.`name` AS `owner_name`,`owners`.`phone` AS `owner_phone`,`owners`.`email` AS `owner_email`,`housing`.`name` AS `housing_name`,`housing`.`location` AS `location`,`housing`.`owner_id` AS `owner_id`,`housing`.`rent` AS `rent`,`housing`.`water_inclusive` AS `water_inclusive`,`housing`.`electricity_inclusive` AS `electricity_inclusive` from (((`reservations` join `tenants`) join `owners`) join `housing`) where ((`reservations`.`tenant_id` = `tenants`.`tenant_id`) and (`housing`.`owner_id` = `owners`.`owner_id`) and (`reservations`.`housing_id` = `housing`.`housing_id`)) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-07-15 15:53:38
