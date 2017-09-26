-- --------------------------------------------------------
-- 호스트:                          127.0.0.1
-- 서버 버전:                        5.6.36 - MySQL Community Server (GPL)
-- 서버 OS:                        Win64
-- HeidiSQL 버전:                  9.4.0.5125
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


-- dorothy 데이터베이스 구조 내보내기
CREATE DATABASE IF NOT EXISTS `dorothy` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci */;
USE `dorothy`;

-- 테이블 dorothy.maps 구조 내보내기
CREATE TABLE IF NOT EXISTS `maps` (
  `lat` double NOT NULL,
  `lng` double NOT NULL,
  `reporter` varchar(50) COLLATE utf8mb4_unicode_520_ci NOT NULL,
  `whatkind` int(11) NOT NULL DEFAULT '1',
  `howmuch` int(11) NOT NULL DEFAULT '1',
  `fake` int(10) NOT NULL DEFAULT '0',
  `date` date DEFAULT NULL,
  `uid` int(11) NOT NULL AUTO_INCREMENT,
  KEY `uid` (`uid`),
  KEY `FK_maps_user` (`reporter`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;

-- 내보낼 데이터가 선택되어 있지 않습니다.
-- 테이블 dorothy.user 구조 내보내기
CREATE TABLE IF NOT EXISTS `user` (
  `id` varchar(50) COLLATE utf8mb4_unicode_520_ci NOT NULL,
  `password` varchar(50) COLLATE utf8mb4_unicode_520_ci NOT NULL,
  `rank` int(10) NOT NULL DEFAULT '0',
  `phone` varchar(50) COLLATE utf8mb4_unicode_520_ci NOT NULL,
  `name` varchar(50) COLLATE utf8mb4_unicode_520_ci NOT NULL,
  KEY `id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;

-- 내보낼 데이터가 선택되어 있지 않습니다.
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
