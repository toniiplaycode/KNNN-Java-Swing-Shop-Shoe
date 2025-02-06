CREATE DATABASE  IF NOT EXISTS `shoe_shop` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `shoe_shop`;
-- MySQL dump 10.13  Distrib 8.0.36, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: shoe_shop
-- ------------------------------------------------------
-- Server version	8.0.36

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
-- Table structure for table `cart`
--

DROP TABLE IF EXISTS `cart`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cart` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `variant_id` int NOT NULL,
  `quantity` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `cart_ibfk_2_idx` (`variant_id`),
  CONSTRAINT `cart_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `cart_ibfk_2` FOREIGN KEY (`variant_id`) REFERENCES `product_variants` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart`
--

LOCK TABLES `cart` WRITE;
/*!40000 ALTER TABLE `cart` DISABLE KEYS */;
INSERT INTO `cart` VALUES (2,3,1,2);
/*!40000 ALTER TABLE `cart` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `categories`
--

DROP TABLE IF EXISTS `categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categories` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categories`
--

LOCK TABLES `categories` WRITE;
/*!40000 ALTER TABLE `categories` DISABLE KEYS */;
INSERT INTO `categories` VALUES (1,'Sneakers','2025-02-04 08:55:55'),(2,'Boots','2025-02-04 08:55:55'),(3,'Giày Thể Thao','2025-02-04 08:55:55'),(4,'Giày Tây','2025-02-04 08:55:55'),(5,'Giày bảo hộ','2025-02-05 03:09:31'),(7,'Giày Lười','2025-02-05 04:38:33'),(8,'Giày Cao Gót','2025-02-05 04:38:33'),(9,'Giày Chạy Bộ','2025-02-05 04:38:33');
/*!40000 ALTER TABLE `categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `colors`
--

DROP TABLE IF EXISTS `colors`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `colors` (
  `id` int NOT NULL AUTO_INCREMENT,
  `color_name` varchar(50) NOT NULL,
  `color_code` varchar(7) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `color_name` (`color_name`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `colors`
--

LOCK TABLES `colors` WRITE;
/*!40000 ALTER TABLE `colors` DISABLE KEYS */;
INSERT INTO `colors` VALUES (1,'Đen','#000000'),(2,'Trắng','#FFFFFF'),(3,'Xanh','#0000FF'),(4,'Đỏ','#FF0000');
/*!40000 ALTER TABLE `colors` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_details`
--

DROP TABLE IF EXISTS `order_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_details` (
  `id` int NOT NULL AUTO_INCREMENT,
  `order_id` int NOT NULL,
  `variant_id` int NOT NULL,
  `quantity` int NOT NULL,
  `unit_price` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `order_id` (`order_id`),
  KEY `variant_id` (`variant_id`),
  CONSTRAINT `order_details_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE,
  CONSTRAINT `order_details_ibfk_2` FOREIGN KEY (`variant_id`) REFERENCES `product_variants` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_details`
--

LOCK TABLES `order_details` WRITE;
/*!40000 ALTER TABLE `order_details` DISABLE KEYS */;
INSERT INTO `order_details` VALUES (1,1,1,1,1500000.00),(2,2,3,1,2500000.00),(3,3,2,1,1500000.00),(4,3,6,1,2500000.00),(11,7,75,1,2000000.00),(12,7,85,1,1700000.00),(13,8,51,1,2800000.00),(14,8,78,1,2200000.00),(15,9,69,3,5200000.00),(16,9,75,1,2000000.00),(17,9,78,1,2200000.00),(18,10,51,1,2800000.00),(19,10,75,1,2000000.00);
/*!40000 ALTER TABLE `order_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `total_price` decimal(10,2) NOT NULL,
  `status` enum('pending','processing','shipped','delivered','cancelled') DEFAULT 'pending',
  `order_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (1,2,1500000.00,'pending','2025-02-04 08:55:55'),(2,3,2500000.00,'shipped','2025-02-04 08:55:55'),(3,2,4000000.00,'pending','2025-02-06 08:22:51'),(7,2,3700000.00,'pending','2025-02-06 08:58:01'),(8,2,5000000.00,'pending','2025-02-06 09:01:18'),(9,2,19800000.00,'pending','2025-02-06 09:03:25'),(10,2,4800000.00,'pending','2025-02-06 09:13:48');
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payments`
--

DROP TABLE IF EXISTS `payments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payments` (
  `id` int NOT NULL AUTO_INCREMENT,
  `order_id` int NOT NULL,
  `payment_method` enum('cash','credit_card','paypal','bank_transfer') DEFAULT NULL,
  `status` enum('paid','failed','refunded') DEFAULT 'paid',
  `payment_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `order_id` (`order_id`),
  CONSTRAINT `payments_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payments`
--

LOCK TABLES `payments` WRITE;
/*!40000 ALTER TABLE `payments` DISABLE KEYS */;
INSERT INTO `payments` VALUES (1,1,'credit_card','paid','2025-02-04 08:55:55'),(2,2,'paypal','paid','2025-02-04 08:55:55'),(3,7,'paypal','paid','2025-02-06 08:58:01'),(4,8,'bank_transfer','paid','2025-02-06 09:01:18'),(5,9,'bank_transfer','paid','2025-02-06 09:03:25'),(6,10,'credit_card','paid','2025-02-06 09:13:48');
/*!40000 ALTER TABLE `payments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_variants`
--

DROP TABLE IF EXISTS `product_variants`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_variants` (
  `id` int NOT NULL AUTO_INCREMENT,
  `product_id` int NOT NULL,
  `size_id` int NOT NULL,
  `color_id` int NOT NULL,
  `stock` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `product_id` (`product_id`),
  KEY `size_id` (`size_id`),
  KEY `color_id` (`color_id`),
  CONSTRAINT `product_variants_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE,
  CONSTRAINT `product_variants_ibfk_2` FOREIGN KEY (`size_id`) REFERENCES `sizes` (`id`) ON DELETE CASCADE,
  CONSTRAINT `product_variants_ibfk_3` FOREIGN KEY (`color_id`) REFERENCES `colors` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=93 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_variants`
--

LOCK TABLES `product_variants` WRITE;
/*!40000 ALTER TABLE `product_variants` DISABLE KEYS */;
INSERT INTO `product_variants` VALUES (1,1,3,1,10),(2,1,4,2,4),(3,2,3,3,100),(4,3,5,1,3),(5,4,2,4,8),(6,2,5,3,99),(7,2,3,2,100),(8,34,5,1,100),(9,1,1,1,25),(10,1,2,1,30),(11,1,3,1,35),(12,1,1,2,20),(13,1,2,2,25),(14,1,3,2,30),(15,2,2,1,15),(16,2,3,1,20),(17,2,4,1,25),(18,2,2,3,15),(19,2,3,3,20),(20,2,4,3,25),(21,3,3,1,10),(22,3,4,1,15),(23,3,5,1,20),(24,3,3,4,10),(25,3,4,4,15),(26,3,5,4,20),(27,4,2,1,12),(28,4,3,1,15),(29,4,4,1,18),(30,4,2,4,10),(31,4,3,4,12),(32,4,4,4,15),(33,29,2,2,20),(34,29,3,2,25),(35,29,4,2,30),(36,29,2,3,20),(37,29,3,3,25),(38,29,4,3,30),(39,30,2,1,15),(40,30,3,1,20),(41,30,4,1,25),(42,30,2,4,15),(43,30,3,4,20),(44,30,4,4,25),(45,31,1,1,30),(46,31,2,1,35),(47,31,3,1,40),(48,31,1,2,30),(49,31,2,2,35),(50,31,3,2,40),(51,32,2,2,18),(52,32,3,2,22),(53,32,4,2,25),(54,32,2,3,18),(55,32,3,3,22),(56,32,4,3,25),(57,33,2,1,20),(58,33,3,1,25),(59,33,4,1,30),(60,33,2,2,20),(61,33,3,2,25),(62,33,4,2,30),(63,34,3,1,15),(64,34,4,1,20),(65,34,5,1,25),(66,34,3,4,15),(67,34,4,4,20),(68,34,5,4,25),(69,35,3,1,10),(70,35,4,1,15),(71,35,5,1,20),(72,35,3,4,10),(73,35,4,4,15),(74,35,5,4,20),(75,36,2,1,12),(76,36,3,1,15),(77,36,4,1,18),(78,37,2,1,15),(79,37,3,1,18),(80,37,4,1,20),(81,37,2,4,15),(82,37,3,4,18),(83,37,4,4,20),(84,38,1,1,20),(85,38,2,1,25),(86,38,3,1,30),(87,39,1,1,15),(88,39,2,1,20),(89,39,3,1,25),(90,39,1,4,15),(91,39,2,4,20),(92,39,3,4,25);
/*!40000 ALTER TABLE `product_variants` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `products`
--

DROP TABLE IF EXISTS `products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `products` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `category_id` int NOT NULL,
  `description` text,
  `price` decimal(10,2) NOT NULL,
  `image_url` varchar(255) DEFAULT NULL,
  `status` tinyint DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `category_id` (`category_id`),
  CONSTRAINT `products_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `products`
--

LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
INSERT INTO `products` VALUES (1,'Nike Air Force 1',1,'Giày sneaker cổ điển',1500000.00,'https://th.bing.com/th/id/OIP.8kE8Y5LLw4ZCWaR5z5ADRAHaFN?w=274&h=192&c=7&r=0&o=5&dpr=1.3&pid=1.7',1,'2025-02-04 08:55:55'),(2,'Adidas Ultraboost',3,'Giày chạy bộ êm ái',2500000.00,'https://th.bing.com/th/id/OIP.0HPr8HRr7uMktw75W5TiXAHaEy?rs=1&pid=ImgDetMain',1,'2025-02-04 08:55:55'),(3,'Dr. Martens 1460',2,'Boots da bền bỉ',3500000.00,'https://th.bing.com/th/id/OIP.toKGbw5kRHlTDvLHZmdgTgHaGM?rs=1&pid=ImgDetMain',1,'2025-02-04 08:55:55'),(4,'Giày Tây Oxford',4,'Giày công sở lịch lãm',1800000.00,'https://th.bing.com/th/id/OIP.qNL8mMWsxmL6MMVg8MxtFQHaGW?rs=1&pid=ImgDetMain',1,'2025-02-04 08:55:55'),(29,'Puma RS-X',1,'Giày sneaker phong cách hiện đại',2200000.00,'https://th.bing.com/th/id/OIP.B_H5VaBEu0I2t6Cp88hjwQHaHa?rs=1&pid=ImgDetMain',1,'2025-02-05 13:59:41'),(30,'Nike Air Jordan 1',1,'Giày sneaker huyền thoại',3500000.00,'https://th.bing.com/th/id/OIP.G2zXJRTF1IUi01k42_MrCQHaF0?rs=1&pid=ImgDetMain',1,'2025-02-05 13:59:41'),(31,'Converse Chuck 70',1,'Giày sneaker vải canvas cổ điển',1600000.00,'https://th.bing.com/th/id/R.ae4273143bf035026c6a526a837ffc2d?rik=Hf5TXgLczzStFQ&pid=ImgRaw&r=0',1,'2025-02-05 13:59:41'),(32,'Asics Gel-Kayano 28',3,'Giày chạy bộ cao cấp của Asics',2800000.00,'https://th.bing.com/th/id/OIP.8YRE3pqAr8I1NRhBMVVk5AHaFB?rs=1&pid=ImgDetMain',1,'2025-02-05 13:59:41'),(33,'Reebok Nano X',3,'Giày tập gym chuyên dụng',1900000.00,'https://th.bing.com/th/id/OIP.zVCzikFlD29bpk53BosYwAHaEa?rs=1&pid=ImgDetMain',1,'2025-02-05 13:59:41'),(34,'Timberland 6 Inch',2,'Boots da chống nước',4500000.00,'https://th.bing.com/th/id/R.815f464b691c425d37313325b178076f?rik=bbKEMHeKhipw%2bA&pid=ImgRaw&r=0',1,'2025-02-05 13:59:41'),(35,'Red Wing Iron Ranger',2,'Boots da cao cấp phong cách vintage',5200000.00,'https://th.bing.com/th/id/OIP.1i_CXaX89nM3z1L-loRMEAHaHa?rs=1&pid=ImgDetMain',1,'2025-02-05 13:59:41'),(36,'Giày Derby Da Bóng',4,'Giày công sở phong cách lịch lãm',2000000.00,'https://th.bing.com/th/id/OIP.-xiHB7orSyZVhpxy4CoAcwHaHa?rs=1&pid=ImgDetMain',1,'2025-02-05 13:59:41'),(37,'Giày Loafer Da Lộn',4,'Giày Tây không dây phong cách Ý',2200000.00,'https://th.bing.com/th/id/OIP.jx5vVwDBNB7gDrbhCdds5QHaHa?rs=1&pid=ImgDetMain',1,'2025-02-05 13:59:41'),(38,'Giày Cao Gót Đen',7,'Giày cao gót nữ thanh lịch',1700000.00,'https://th.bing.com/th/id/OIP.MQzSFNBwLlolq8APzITnfAHaKG?rs=1&pid=ImgDetMain',1,'2025-02-05 13:59:41'),(39,'Giày Cao Gót Mũi Nhọn',7,'Giày cao gót mũi nhọn sang trọng',2000000.00,'https://th.bing.com/th/id/OIP.9GUCeNdAmdDluAwCHKPOVwHaKG?rs=1&pid=ImgDetMain',1,'2025-02-05 13:59:41');
/*!40000 ALTER TABLE `products` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reviews`
--

DROP TABLE IF EXISTS `reviews`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reviews` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `product_id` int NOT NULL,
  `rating` int NOT NULL,
  `comment` text,
  `review_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `product_id` (`product_id`),
  CONSTRAINT `reviews_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `reviews_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE,
  CONSTRAINT `reviews_chk_1` CHECK ((`rating` between 1 and 5))
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reviews`
--

LOCK TABLES `reviews` WRITE;
/*!40000 ALTER TABLE `reviews` DISABLE KEYS */;
INSERT INTO `reviews` VALUES (1,2,1,5,'Giày đẹp, chất lượng tốt!','2025-02-04 08:55:55'),(2,3,2,4,'Rất êm chân, đáng tiền.','2025-02-04 08:55:55');
/*!40000 ALTER TABLE `reviews` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sizes`
--

DROP TABLE IF EXISTS `sizes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sizes` (
  `id` int NOT NULL AUTO_INCREMENT,
  `size` varchar(10) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `size` (`size`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sizes`
--

LOCK TABLES `sizes` WRITE;
/*!40000 ALTER TABLE `sizes` DISABLE KEYS */;
INSERT INTO `sizes` VALUES (1,'38'),(2,'39'),(3,'40'),(4,'41'),(5,'42');
/*!40000 ALTER TABLE `sizes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `email` varchar(100) NOT NULL,
  `phone` varchar(15) DEFAULT NULL,
  `address` text,
  `role` enum('admin','customer','employee') DEFAULT 'customer',
  `status` tinyint DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'admin','admin123','admin@example.com','0123456789','Hà Nội','admin',1,'2025-02-04 08:55:53'),(2,'customer1','123','cus1@example.com','0987654321','TP HCM','customer',1,'2025-02-04 08:55:53'),(3,'customer2','123','cus2@example.com','0971234567','Đà Nẵng','customer',1,'2025-02-04 08:55:53'),(4,'employee1','123','emp1@example.com','0971234567','Đà Nẵng','employee',1,'2025-02-04 08:55:53'),(8,'customer3','123','cus3@example.com','0976543210','Hải Phòng','customer',1,'2025-02-05 04:38:11'),(9,'customer4','123','cus4@example.com','0961234567','Cần Thơ','customer',1,'2025-02-05 04:38:11'),(10,'employee2','123','emp2@example.com','0988888888','Hà Nội','employee',1,'2025-02-05 04:38:11'),(11,'duy','123','duy@gmail.com','09232425325','Hậu Giang','customer',1,'2025-02-05 23:04:06');
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

-- Dump completed on 2025-02-06 16:16:16
