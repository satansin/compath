-- MySQL dump 10.13  Distrib 5.6.11, for Win64 (x86_64)
--
-- Host: localhost    Database: compath
-- ------------------------------------------------------
-- Server version	5.6.11

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `announcement`
--

DROP TABLE IF EXISTS `announcement`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `announcement` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `content` text,
  `time` bigint(20) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_Announcement_user1_idx` (`user_id`),
  CONSTRAINT `fk_Announcement_user1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `announcement`
--

LOCK TABLES `announcement` WRITE;
/*!40000 ALTER TABLE `announcement` DISABLE KEYS */;
/*!40000 ALTER TABLE `announcement` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `city`
--

DROP TABLE IF EXISTS `city`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `city` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) DEFAULT NULL,
  `province` varchar(10) DEFAULT NULL,
  `latitude` int(11) DEFAULT NULL,
  `longitude` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=370 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `city`
--

LOCK TABLES `city` WRITE;
/*!40000 ALTER TABLE `city` DISABLE KEYS */;
INSERT INTO `city` VALUES (2,'北京','北京',40450000,115970001),(3,'天津','天津',40049999,117400001),(4,'上海','上海',31620000,121400001),(5,'重庆','重庆',29299999,108169998),(6,'蚌埠','安徽',33319999,117319999),(7,'合肥','安徽',31719999,117169998),(8,'阜阳','安徽',32630001,116269996),(9,'芜湖','安徽',30920000,118330001),(10,'安庆','安徽',31049999,116949996),(11,'亳州','安徽',33150001,116199996),(12,'滁州','安徽',32779998,117980003),(13,'宣城','安徽',30629999,118980003),(14,'淮南','安徽',32700000,116720001),(15,'巢湖','安徽',31719999,118370002),(16,'黄山','安徽',29870000,117720001),(17,'淮北','安徽',33919998,116769996),(18,'六安','安徽',31399999,116330001),(19,'池州','安徽',30649999,117849998),(20,'铜陵','安徽',30950000,117779998),(21,'马鞍山','安徽',31549999,118480003),(22,'宿州','安徽',33479999,117879997),(23,'南平','福建',27329999,118120002),(24,'泉州','福建',24969999,118379997),(25,'宁德','福建',27329999,120220001),(26,'龙岩','福建',25299999,117419998),(27,'厦门','福建',24620000,118230003),(28,'莆田','福建',25370000,118680000),(29,'三明','福建',25979999,117370002),(30,'漳州','福建',24450000,117819999),(31,'福州','福建',25969999,119519996),(32,'嘉峪关','甘肃',39799999,98269996),(33,'金昌','甘肃',38250000,101970001),(34,'白银','甘肃',37150001,104069999),(35,'兰州','甘肃',35849998,104120002),(36,'酒泉','甘肃',40130001,94669998),(37,'张掖','甘肃',38779998,101080001),(38,'武威','甘肃',36979999,103129997),(39,'庆阳','甘肃',35680000,107199996),(40,'定西','甘肃',34430000,104029998),(41,'临夏','甘肃',35669998,103400001),(42,'天水','甘肃',35000000,106220001),(43,'甘南','甘肃',35200000,102519996),(44,'陇南','甘肃',33919998,106300003),(45,'平凉','甘肃',35520000,105720001),(46,'东莞','广东',23049999,113750000),(48,'韶关','广东',25120000,114300003),(49,'佛山','广东',22899999,112879997),(50,'茂名','广东',22350000,110949996),(51,'珠海','广东',22069999,113400001),(52,'梅州','广东',24149999,115730003),(53,'中山','广东',22520000,113379997),(54,'清远','广东',24780000,112379997),(55,'湛江','广东',21430000,110769996),(56,'阳江','广东',22180000,111779998),(57,'河源','广东',23819999,114769996),(58,'潮州','广东',23670000,117000000),(59,'广州','广东',23549999,113580001),(60,'云浮','广东',22770000,111569999),(61,'揭阳','广东',23299999,116180000),(62,'惠州','广东',23729999,114250000),(63,'江门','广东',22180000,112300003),(64,'汕头','广东',23420000,117019996),(65,'肇庆','广东',23329999,112680000),(66,'汕尾','广东',22950000,115650001),(67,'深圳','广东',22549999,114220001),(68,'桂林','广西',24829999,110830001),(69,'河池','广西',24500000,108669998),(70,'崇左','广西',22120000,106750000),(71,'钦州','广西',22270000,109550003),(72,'来宾','广西',23819999,108870002),(73,'百色','广西',24770000,105330001),(74,'防城港','广西',21530000,107970001),(75,'贺州','广西',24829999,111269996),(76,'南宁','广西',22680000,109269996),(77,'北海','广西',21670000,109199996),(78,'梧州','广西',22920000,110980003),(79,'柳州','广西',25780000,109599998),(80,'贵港','广西',23399999,110080001),(81,'玉林','广西',22719999,110349998),(82,'贵阳','贵州',26549999,106470001),(83,'六盘水','贵州',25719999,104470001),(84,'铜仁','贵州',27520000,109199996),(85,'毕节','贵州',27129999,104720001),(86,'遵义','贵州',27819999,106419998),(87,'安顺','贵州',25750000,106080001),(88,'黔南','贵州',25979999,107870002),(89,'黔东南','贵州',26200000,107800003),(90,'黔西南','贵州',25250000,105330000),(91,'三亚','海南',18250000,109500000),(92,'海口','海南',20030000,110370002),(93,'定安','海南',19700000,110319999),(94,'儋州','海南',19520000,109569999),(95,'万宁','海南',18799999,110400001),(96,'保亭','海南',18629999,109699996),(100,'屯昌','海南',19370000,110099998),(101,'昌江','海南',19250000,109050003),(102,'陵水','海南',18500000,110029998),(103,'五指山','海南',19030000,109830001),(104,'琼中','海南',19030000,109830001),(105,'乐东','海南',18750000,109169998),(106,'临高','海南',19920000,109680000),(107,'琼海','海南',19250000,110470001),(108,'白沙','海南',19229999,109449996),(109,'东方','海南',19100000,108629997),(110,'澄迈','海南',19729999,110000000),(111,'文昌','海南',19549999,110800003),(112,'秦皇岛','河北',39880001,118870002),(113,'沧州','河北',38430000,116080001),(114,'石家庄','河北',38080001,114300003),(115,'邯郸','河北',36700000,114199996),(116,'廊坊','河北',39979999,117069999),(117,'承德','河北',41930000,117750000),(118,'衡水','河北',38020000,115550003),(119,'张家口','河北',40970001,115269996),(120,'唐山','河北',40020000,118699996),(121,'邢台','河北',36849998,114500000),(122,'保定','河北',39330001,115849998),(123,'大兴安岭','黑龙江',52970001,122529998),(124,'黑河','黑龙江',48520000,126199996),(125,'伊春','黑龙江',46979999,128020004),(126,'齐齐哈尔','黑龙江',48479999,124870002),(127,'佳木斯','黑龙江',47250000,132029998),(128,'鹤岗','黑龙江',47279998,131850006),(129,'绥化','黑龙江',47470001,126970001),(130,'双鸭山','黑龙江',46799999,134020004),(131,'鸡西','黑龙江',45549999,131869995),(132,'七台河','黑龙江',45750000,130570007),(133,'哈尔滨','黑龙江',44919998,127150001),(134,'牡丹江','黑龙江',44919998,130520004),(135,'大庆','黑龙江',46869998,124449996),(136,'新乡','河南',35470001,113800003),(137,'洛阳','河南',34729999,112779998),(138,'商丘','河南',33919998,116430000),(139,'许昌','河南',34220001,113769996),(140,'濮阳','河南',35700000,115019996),(141,'开封','河南',34819999,114819999),(142,'焦作','河南',34900001,112779998),(143,'三门峡','河南',34520000,110870002),(144,'平顶山','河南',34169998,112830001),(145,'信阳','河南',32349998,114730003),(146,'鹤壁','河南',35599998,114199996),(147,'安阳','河南',36069999,113819999),(148,'郑州','河南',34470001,113029998),(149,'驻马店','河南',32750000,114980003),(150,'周口','河南',33450000,114900001),(151,'南阳','河南',32680000,112080001),(152,'漯河','河南',33819999,113930000),(153,'济源','河南',35069999,112580001),(154,'鄂州','湖北',30399999,114879997),(155,'襄樊','湖北',31719999,112250000),(156,'荆州','湖北',30180000,111769996),(157,'十堰','湖北',32549999,111519996),(158,'荆门','湖北',31170000,112580001),(159,'武汉','湖北',30850000,114800003),(160,'宜昌','湖北',30430000,111769996),(161,'黄冈','湖北',29850000,115550003),(162,'孝感','湖北',30649999,113830001),(163,'黄石','湖北',30100000,114970001),(164,'咸宁','湖北',29719999,113879997),(165,'随州','湖北',31620000,113819999),(166,'恩施','湖北',29899999,110029998),(167,'潜江','湖北',30420000,112879997),(168,'仙桃','湖北',31750000,110669998),(169,'天门','湖北',30670000,113169998),(170,'神农架','湖北',31750000,110669998),(171,'长沙','湖南',28149999,113629997),(172,'衡阳','湖南',26420000,112379997),(173,'常德','湖南',29620000,111879997),(174,'岳阳','湖南',29479999,113470001),(175,'娄底','湖南',27700000,111669998),(176,'株洲','湖南',27670000,113480003),(177,'益阳','湖南',28850000,112379997),(178,'邵阳','湖南',26729999,110629997),(179,'湘西','湖南',29469999,109430000),(180,'郴州','湖南',25979999,113230003),(181,'张家界','湖南',29399999,110150001),(182,'湘潭','湖南',27930000,112519996),(183,'永州','湖南',25180000,111580001),(184,'怀化','湖南',27200000,109819999),(185,'镇江','江苏',31950000,119169998),(186,'南通','江苏',31899999,121169998),(187,'淮安','江苏',33020000,119019996),(188,'盐城','江苏',33200000,120470001),(189,'苏州','江苏',31450000,121099998),(190,'泰州','江苏',32520000,120150001),(191,'宿迁','江苏',33470001,118220001),(192,'南京','江苏',31329999,118879997),(193,'徐州','江苏',34319999,117949996),(194,'无锡','江苏',31350000,119819999),(195,'扬州','江苏',32430000,119550003),(196,'连云港','江苏',34080001,119349998),(197,'常州','江苏',31750000,119569999),(198,'南昌','江西',28370000,116269996),(199,'新余','江西',27819999,114669998),(200,'景德镇','江西',28969999,117120002),(201,'抚州','江西',26829999,116319999),(202,'宜春','江西',28420000,115370002),(203,'鹰潭','江西',28280000,117220001),(204,'吉安','江西',26719999,114269996),(205,'九江','江西',29680000,115669998),(206,'萍乡','江西',27629999,114029998),(207,'上饶','江西',28950000,117569999),(208,'赣州','江西',25649999,114750000),(209,'白城','吉林',45500000,124279998),(210,'松原','吉林',44979999,126019996),(211,'长春','吉林',44529998,125699996),(212,'延边','吉林',43119998,128899993),(213,'吉林','吉林',43119998,128899993),(214,'四平','吉林',43520000,123500000),(215,'白山','吉林',41799999,126900001),(216,'通化','吉林',41119998,126180000),(217,'辽源','吉林',42919998,125000000),(218,'沈阳','辽宁',42000000,122819999),(219,'阜新','辽宁',42380001,122529998),(220,'铁岭','辽宁',42549999,124029998),(221,'锦州','辽宁',41169998,121349998),(222,'大连','辽宁',39700000,122980003),(223,'抚顺','辽宁',42099998,124919998),(224,'本溪','辽宁',41270000,125349998),(225,'盘锦','辽宁',41250000,122019996),(226,'朝阳','辽宁',41250000,119400001),(227,'营口','辽宁',40650001,122500000),(228,'丹东','辽宁',40450000,124069999),(229,'葫芦岛','辽宁',40619998,120720001),(230,'鞍山','辽宁',40880001,122699996),(231,'辽阳','辽宁',41419998,123330001),(232,'呼伦贝尔','内蒙古',50779998,121519996),(233,'兴安盟','内蒙古',45380001,121569999),(234,'锡林郭勒盟','内蒙古',42180000,116470001),(235,'通辽','内蒙古',45529998,119650001),(236,'乌海','内蒙古',39500000,106699996),(237,'乌兰察布','内蒙古',40430000,113150001),(238,'巴彦淖尔','内蒙古',40880001,107150001),(239,'包头','内蒙古',41700000,110430000),(240,'阿拉善盟','内蒙古',41970001,101069999),(241,'鄂尔多斯','内蒙古',39569999,109730003),(242,'赤峰','内蒙古',42279998,119900001),(243,'呼和浩特','内蒙古',41080001,111449996),(244,'中卫','宁夏',36569999,105650001),(245,'固原','宁夏',35849998,106629997),(246,'吴忠','宁夏',38020000,106069999),(247,'石嘴山','宁夏',38900001,106529998),(248,'银川','宁夏',38099998,106330001),(249,'海西','青海',37299999,99019996),(250,'西宁','青海',36680000,101269996),(251,'海北','青海',37330001,100129997),(252,'海南','青海',35580001,100750000),(253,'海东','青海',35849998,102480003),(254,'黄南','青海',34729999,101599998),(255,'玉树','青海',34130001,95800003),(256,'果洛','青海',34919998,98180000),(257,'莱芜','山东',36069999,117800003),(258,'枣庄','山东',35080001,117150001),(259,'日照','山东',35580001,118830001),(260,'东营','山东',37069999,118400001),(261,'威海','山东',36919998,121529998),(262,'临沂','山东',34919998,118650001),(263,'滨州','山东',36880001,117730003),(264,'青岛','山东',36869998,120500000),(265,'济宁','山东',35400001,116970001),(266,'潍坊','山东',36869998,119400001),(267,'济南','山东',36720001,117529998),(268,'泰安','山东',36180000,116769996),(269,'烟台','山东',36779998,121150001),(270,'菏泽','山东',35140000,115260000),(271,'淄博','山东',36180000,118169998),(272,'聊城','山东',36849998,115699996),(273,'德州','山东',36930000,116629997),(274,'太原','山西',37919998,112169998),(275,'朔州','山西',39830001,113080001),(276,'晋中','山西',37029998,111919998),(277,'晋城','山西',35799999,112919998),(278,'吕梁','山西',37270000,111779998),(279,'运城','山西',35599998,110699996),(280,'大同','山西',40029998,113599998),(281,'长治','山西',36330001,113220001),(282,'阳泉','山西',38080001,113400001),(283,'忻州','山西',38729999,112699996),(284,'临汾','山西',36569999,111720001),(285,'渭南','陕西',34569999,110080001),(286,'宝鸡','陕西',34069999,107319999),(287,'榆林','陕西',37619998,110029998),(288,'铜川','陕西',35400001,109120002),(289,'西安','陕西',34529998,109080001),(290,'延安','陕西',35580001,109250000),(291,'商洛','陕西',33680000,109099998),(292,'咸阳','陕西',34299999,108480003),(293,'安康','陕西',32819999,110099998),(294,'汉中','陕西',33529998,107980003),(295,'甘孜','四川',28719999,99279998),(296,'德阳','四川',31350000,104199996),(297,'成都','四川',30629999,103669998),(298,'雅安','四川',30370000,102819999),(299,'眉山','四川',29829999,103849998),(300,'自贡','四川',29180000,104980003),(301,'乐山','四川',29600000,103480003),(302,'凉山','四川',28270000,103569999),(303,'攀枝花','四川',26700000,101849998),(304,'阿坝','四川',32799999,102550003),(305,'宜宾','四川',28829999,104330001),(306,'巴中','四川',31569999,107099998),(307,'绵阳','四川',31780000,104750000),(308,'广安','四川',30379999,106769996),(309,'资阳','四川',30399999,104550003),(310,'内江','四川',29350000,105279998),(311,'南充','四川',31549999,106000000),(312,'广元','四川',31729999,105930000),(313,'遂宁','四川',30579999,105250000),(314,'泸州','四川',28049999,105819999),(315,'达州','四川',32069999,108029998),(316,'和田','新疆',37069999,82680000),(317,'喀什','新疆',39779998,78550003),(318,'克孜勒苏柯尔克孜','新疆',35460000,75980000),(319,'阿克苏','新疆',40500000,79050003),(320,'巴音郭楞','新疆',41979999,86629997),(321,'博尔塔拉','新疆',44970001,81029998),(322,'吐鲁番','新疆',42779998,88650001),(323,'伊犁哈萨克','新疆',43779998,82500000),(324,'哈密地区','新疆',43250000,94699996),(325,'乌鲁木齐','新疆',43799999,87599998),(326,'昌吉','新疆',43830001,90279998),(327,'塔城','新疆',46799999,85720001),(328,'克拉玛依','新疆',46080001,85680000),(329,'阿勒泰','新疆',47430000,85879997),(330,'阿拉尔','新疆',40549999,81279998),(331,'石河子','新疆',44169998,87529998),(332,'五家渠','新疆',44169998,87529998),(333,'图木舒克','新疆',39849998,79129997),(334,'山南','西藏',28969999,90400001),(335,'林芝','西藏',29049999,93069999),(336,'昌都','西藏',30930000,94699996),(337,'拉萨','西藏',29829999,91730003),(338,'那曲','西藏',31780000,87230003),(339,'日喀则','西藏',28280000,88519996),(340,'阿里','西藏',31020000,85169998),(341,'昆明','云南',24920000,102480003),(342,'楚雄','云南',25149999,102080001),(343,'玉溪','云南',24069999,101980003),(344,'红河','云南',22520000,103970001),(345,'普洱','云南',23070000,101030000),(346,'西双版纳','云南',21479999,101569999),(347,'临沧','云南',23149999,99250000),(348,'大理','云南',26569999,100180000),(349,'保山','云南',24829999,99599998),(350,'怒江','云南',26450000,99419998),(351,'丽江','云南',27280000,100849998),(352,'迪庆','云南',27180000,99279998),(353,'德宏','云南',24200000,97800003),(354,'文山','云南',23629999,105620002),(355,'曲靖','云南',26219999,104099998),(356,'昭通','云南',28629999,104400001),(357,'温州','浙江',28129999,120949996),(358,'杭州','浙江',30229999,119720001),(359,'宁波','浙江',29649999,121400001),(360,'衢州','浙江',28750000,118620002),(361,'台州','浙江',28850000,121120002),(362,'舟山','浙江',30729999,122449996),(363,'丽水','浙江',28079999,119129997),(364,'绍兴','浙江',29579999,120819999),(365,'湖州','浙江',30629999,119680000),(366,'金华','浙江',28899999,120029998),(367,'嘉兴','浙江',30629999,120569999),(368,'香港','香港',22200000,114080001),(369,'澳门','澳门',22129999,113330001);
/*!40000 ALTER TABLE `city` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `group`
--

DROP TABLE IF EXISTS `group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `group` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `create_time` bigint(20) DEFAULT NULL,
  `owner_id` int(11) DEFAULT NULL,
  `last_active_time` bigint(20) DEFAULT NULL,
  `title` varchar(45) DEFAULT NULL,
  `location_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_groups_user1_idx` (`owner_id`),
  KEY `fk_group_location1_idx` (`location_id`),
  CONSTRAINT `fk_groups_user1` FOREIGN KEY (`owner_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_group_location1` FOREIGN KEY (`location_id`) REFERENCES `location` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `group`
--

LOCK TABLES `group` WRITE;
/*!40000 ALTER TABLE `group` DISABLE KEYS */;
INSERT INTO `group` VALUES (1,1402574523447,1,1402733837162,'Hey look!',1),(2,1402716639573,4,1402716639573,'费彝民楼是干嘛的唔',1),(3,1402716792552,4,1402716792552,'再来一个',1);
/*!40000 ALTER TABLE `group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `group_favor`
--

DROP TABLE IF EXISTS `group_favor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `group_favor` (
  `user_id` int(11) NOT NULL,
  `group_id` int(11) NOT NULL,
  `favor_time` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`user_id`,`group_id`),
  KEY `fk_user_has_group_group1_idx` (`group_id`),
  KEY `fk_user_has_group_user1_idx` (`user_id`),
  CONSTRAINT `fk_user_has_group_group1` FOREIGN KEY (`group_id`) REFERENCES `group` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_user_has_group_user1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `group_favor`
--

LOCK TABLES `group_favor` WRITE;
/*!40000 ALTER TABLE `group_favor` DISABLE KEYS */;
INSERT INTO `group_favor` VALUES (4,1,NULL);
/*!40000 ALTER TABLE `group_favor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `location`
--

DROP TABLE IF EXISTS `location`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `location` (
  `name` varchar(45) DEFAULT NULL,
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `latitude` int(11) DEFAULT NULL,
  `longitude` int(11) DEFAULT NULL,
  `owner_id` int(11) DEFAULT NULL,
  `city_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_location_user1_idx` (`owner_id`),
  KEY `fk_location_city1_idx` (`city_id`),
  CONSTRAINT `fk_location_city1` FOREIGN KEY (`city_id`) REFERENCES `city` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_location_user1` FOREIGN KEY (`owner_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `location`
--

LOCK TABLES `location` WRITE;
/*!40000 ALTER TABLE `location` DISABLE KEYS */;
INSERT INTO `location` VALUES ('南京大学-费彝民楼',1,32058250,118778425,1,192);
/*!40000 ALTER TABLE `location` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `message`
--

DROP TABLE IF EXISTS `message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `message` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `time` bigint(20) DEFAULT NULL,
  `content` text,
  `sender_id` int(11) DEFAULT NULL,
  `group_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_message_user1_idx` (`sender_id`),
  KEY `fk_message_groups1_idx` (`group_id`),
  CONSTRAINT `fk_message_groups1` FOREIGN KEY (`group_id`) REFERENCES `group` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_message_user1` FOREIGN KEY (`sender_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `message`
--

LOCK TABLES `message` WRITE;
/*!40000 ALTER TABLE `message` DISABLE KEYS */;
INSERT INTO `message` VALUES (1,1402575183534,'hi',1,1),(2,1402575347659,'hi',1,1),(3,1402575484542,'hi',1,1),(4,1402576120586,'message from 2',2,1),(5,1402576279153,'yo, we made connected~',1,1),(6,1402576372318,'haha',2,1),(7,1402576372678,'let me try more than one message',2,1),(8,1402713396715,'鍙戝彂',4,1),(9,1402714700419,'互黑',4,1),(11,1402733827208,'haha',3,1),(12,1402733820240,'哈哈哈',4,1);
/*!40000 ALTER TABLE `message` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `navigation`
--

DROP TABLE IF EXISTS `navigation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `navigation` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `content` text,
  `time` bigint(20) DEFAULT NULL,
  `groups_id` int(11) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_navigation_groups1_idx` (`groups_id`),
  KEY `fk_navigation_user1_idx` (`user_id`),
  CONSTRAINT `fk_navigation_groups1` FOREIGN KEY (`groups_id`) REFERENCES `group` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_navigation_user1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `navigation`
--

LOCK TABLES `navigation` WRITE;
/*!40000 ALTER TABLE `navigation` DISABLE KEYS */;
/*!40000 ALTER TABLE `navigation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `participation`
--

DROP TABLE IF EXISTS `participation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `participation` (
  `user_id` int(11) NOT NULL,
  `group_id` int(11) NOT NULL,
  `last_received_time` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`user_id`,`group_id`),
  KEY `fk_user_has_group_group2_idx` (`group_id`),
  KEY `fk_user_has_group_user2_idx` (`user_id`),
  CONSTRAINT `fk_user_has_group_group2` FOREIGN KEY (`group_id`) REFERENCES `group` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_user_has_group_user2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `participation`
--

LOCK TABLES `participation` WRITE;
/*!40000 ALTER TABLE `participation` DISABLE KEYS */;
INSERT INTO `participation` VALUES (1,1,1402576385563),(2,1,1402576305916);
/*!40000 ALTER TABLE `participation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `session`
--

DROP TABLE IF EXISTS `session`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `session` (
  `session` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `session_UNIQUE` (`session`),
  CONSTRAINT `fk_session_user1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `session`
--

LOCK TABLES `session` WRITE;
/*!40000 ALTER TABLE `session` DISABLE KEYS */;
INSERT INTO `session` VALUES (2,1),(5,2);
/*!40000 ALTER TABLE `session` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(45) NOT NULL,
  `password` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username_UNIQUE` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'doubi','iiiiiiiiiiiiiiiii'),(2,'haha','aaaaaaaaaaaaaaaa'),(3,'satansin','6ED7CAB03CE482FD208A57839B14C0A8'),(4,'aaaa','3DBE00A167653A1AAEE01D93E77E730E');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_detail`
--

DROP TABLE IF EXISTS `user_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_detail` (
  `user_id` int(11) NOT NULL,
  `city_id` int(11) DEFAULT NULL,
  `icon_url` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  KEY `fk_user_detail_city1_idx` (`city_id`),
  CONSTRAINT `fk_user_detail_city1` FOREIGN KEY (`city_id`) REFERENCES `city` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_user_detail_user1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_detail`
--

LOCK TABLES `user_detail` WRITE;
/*!40000 ALTER TABLE `user_detail` DISABLE KEYS */;
INSERT INTO `user_detail` VALUES (1,192,NULL),(2,192,NULL),(3,192,NULL),(4,2,NULL);
/*!40000 ALTER TABLE `user_detail` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2014-06-23 10:27:06
