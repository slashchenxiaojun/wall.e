/*
Navicat MySQL Data Transfer

Source Server         : 本地localhost
Source Server Version : 50542
Source Host           : 127.0.0.1:3306
Source Database       : walle

Target Server Type    : MYSQL
Target Server Version : 50542
File Encoding         : 65001

Date: 2017-04-12 16:14:56
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for w_db_model
-- ----------------------------
DROP TABLE IF EXISTS `w_db_model`;
CREATE TABLE `w_db_model` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL COMMENT '表名',
  `class_name` varchar(45) NOT NULL COMMENT '类名',
  `describe` varchar(100) DEFAULT NULL COMMENT '描述(表的注释)',
  `is_syn_db` bit(1) DEFAULT NULL COMMENT '是否已经同步数据库',
  `is_syn_code` bit(1) DEFAULT NULL COMMENT '是否同步代码',
  `project_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_w_project_w_model1_idx` (`project_id`),
  CONSTRAINT `fk_w_project_w_model1_idx` FOREIGN KEY (`project_id`) REFERENCES `w_project` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8 COMMENT='数据model元数据，通常使用engine为InnoDB\r\n\r\n';

-- ----------------------------
-- Table structure for w_db_model_item
-- ----------------------------
DROP TABLE IF EXISTS `w_db_model_item`;
CREATE TABLE `w_db_model_item` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) DEFAULT NULL,
  `note` varchar(64) DEFAULT NULL COMMENT '注释',
  `type` varchar(45) DEFAULT NULL COMMENT '物理类型',
  `java_type` varchar(45) DEFAULT NULL COMMENT 'java类型',
  `length` int(11) DEFAULT NULL COMMENT '长度',
  `decimal` int(11) DEFAULT NULL COMMENT '小数点',
  `serial` int(11) DEFAULT NULL COMMENT '序列号',
  `format` varchar(45) DEFAULT NULL COMMENT 'format是存在于当type=''date''\n相当于时间格式yyyy-MM-dd hh:mm:ss\n或者是其他的格式，可用于扩展',
  `is_primary` bit(1) DEFAULT b'0' COMMENT '是否是主键',
  `is_required` bit(1) DEFAULT b'0' COMMENT '是否非空',
  `default_value` varchar(45) DEFAULT NULL COMMENT '默认值',
  `w_model_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_w_model_item_w_model1_idx` (`w_model_id`),
  CONSTRAINT `fk_w_model_item_w_model1` FOREIGN KEY (`w_model_id`) REFERENCES `w_db_model` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=137 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for w_db_model_mapping
-- ----------------------------
DROP TABLE IF EXISTS `w_db_model_mapping`;
CREATE TABLE `w_db_model_mapping` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `mapping_schema` enum('oneToMany','ManyToMany','oneToOne') DEFAULT NULL COMMENT '映射模式\noneToMany\nManyToMany\noneToOne\n',
  `mapping_foreign_key` varchar(45) DEFAULT NULL COMMENT '映射的外键,只有oneToMany, oneToOne才会有，ManyToMany不存在映射外键,只有中间表',
  `master_id` int(11) NOT NULL,
  `master_name` varchar(45) DEFAULT NULL COMMENT '主表名,在·当且仅当relationship = slaves才有效',
  `slaves_id` int(11) NOT NULL,
  `slaves_name` varchar(45) DEFAULT NULL COMMENT '从表名',
  PRIMARY KEY (`id`),
  KEY `fk_w_model_mapping_w_db_model1_idx` (`master_id`),
  KEY `fk_w_model_mapping_w_db_model2_idx` (`slaves_id`),
  CONSTRAINT `fk_w_model_mapping_w_db_model1` FOREIGN KEY (`master_id`) REFERENCES `w_db_model` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_w_model_mapping_w_db_model2` FOREIGN KEY (`slaves_id`) REFERENCES `w_db_model` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for w_folder
-- ----------------------------
DROP TABLE IF EXISTS `w_folder`;
CREATE TABLE `w_folder` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `pid` int(11) DEFAULT NULL COMMENT '直接父节点id',
  `root_id` int(11) DEFAULT NULL COMMENT 'root节点的id',
  `level` int(3) DEFAULT NULL COMMENT '文件夹层级',
  `name` varchar(45) DEFAULT NULL COMMENT '文件夹别名',
  `w_project_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_w_folder_w_project1_idx` (`w_project_id`),
  CONSTRAINT `fk_w_folder_w_project1` FOREIGN KEY (`w_project_id`) REFERENCES `w_project` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COMMENT='接口的文件夹-树形结构';

-- ----------------------------
-- Table structure for w_generate
-- ----------------------------
DROP TABLE IF EXISTS `w_generate`;
CREATE TABLE `w_generate` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `package` varchar(45) DEFAULT NULL COMMENT '包路径\n使用''.''来分割层级, 如\norg.hacker\n\n',
  `module_name` varchar(45) DEFAULT NULL COMMENT '模块名称\n',
  `code_style` varchar(45) DEFAULT NULL COMMENT '代码风格',
  `w_model_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_w_generate_w_db_model1_idx` (`w_model_id`),
  CONSTRAINT `fk_w_generate_w_db_model1` FOREIGN KEY (`w_model_id`) REFERENCES `w_db_model` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8 COMMENT='目录结构由 包路径 + 模块 构成';

-- ----------------------------
-- Table structure for w_interface
-- ----------------------------
DROP TABLE IF EXISTS `w_interface`;
CREATE TABLE `w_interface` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(45) DEFAULT NULL COMMENT '接口唯一标识,请注意是项目中唯一，也就意味着在不同的2个项目中允许存在重名code的接口',
  `name` varchar(45) NOT NULL,
  `relative_url` varchar(100) DEFAULT NULL COMMENT '访问接口的真实地址,相对于项目的base_url而言',
  `description` varchar(500) DEFAULT NULL,
  `data` varchar(1000) DEFAULT NULL COMMENT '模拟数据',
  `w_project_id` int(11) NOT NULL,
  `w_folder_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_w_interface_w_project_idx` (`w_project_id`),
  KEY `fk_w_interface_w_folder1_idx` (`w_folder_id`),
  CONSTRAINT `fk_w_interface_w_folder1` FOREIGN KEY (`w_folder_id`) REFERENCES `w_folder` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_w_interface_w_project` FOREIGN KEY (`w_project_id`) REFERENCES `w_project` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 COMMENT='接口是WALL-E的核心，所有接口都是归于某个项目，接口与项目是ManyToOne的关系，为什么不是ManyToMany，因为那样的话违反了DRY(Don''t repect yourself)原则';

-- ----------------------------
-- Table structure for w_interface_log
-- ----------------------------
DROP TABLE IF EXISTS `w_interface_log`;
CREATE TABLE `w_interface_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `access` int(11) DEFAULT NULL COMMENT '访问次数,根据start_date和end_date来区分，通常都是以一天为单位，当然也可以根据需要调节到其他时间区间',
  `start_date` datetime DEFAULT NULL,
  `end_date` datetime DEFAULT NULL,
  `milliseconds` decimal(10,2) DEFAULT NULL COMMENT '平均毫秒',
  `w_interface_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_w_interface_log_w_interface1_idx` (`w_interface_id`),
  CONSTRAINT `fk_w_interface_log_w_interface1` FOREIGN KEY (`w_interface_id`) REFERENCES `w_interface` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='记录一天中或者某个时间段中接口的访问次数，和平均时长，或其他参数';

-- ----------------------------
-- Table structure for w_parameter
-- ----------------------------
DROP TABLE IF EXISTS `w_parameter`;
CREATE TABLE `w_parameter` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `type` enum('bool','numbe','date','string','byte','jsonobj','jsonarray') NOT NULL COMMENT '对应java中的关系\nbool: boolen\nnumber: int, long, flow, double, BigDecimal\ndate: java.util.date\nstring: string\nbyte: object\njsonobj: jsonObject\njsonarray: jsonArray\n''bool'',''numbe'',''date'',''string'',''byte''',
  `require` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否是必须填写的字段',
  `format` varchar(45) DEFAULT NULL COMMENT 'format是存在于当type=''date''\n相当于时间格式yyyy-MM-dd hh:mm:ss\n或者是其他的格式，可用于扩展',
  `length` varchar(45) DEFAULT NULL COMMENT '当type=''string''\n效验string的长度',
  `remarks` varchar(255) DEFAULT NULL COMMENT '参数备注',
  `w_interface_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_w_parameter_w_interface1_idx` (`w_interface_id`),
  CONSTRAINT `fk_w_parameter_w_interface1` FOREIGN KEY (`w_interface_id`) REFERENCES `w_interface` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='接口的参数，与接口是oneToMany的关系，参数是为了满足前后端效验一致而产生的，最理想的情况是使用代码生成来降低容错率';

-- ----------------------------
-- Table structure for w_project
-- ----------------------------
DROP TABLE IF EXISTS `w_project`;
CREATE TABLE `w_project` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `pattern` enum('normal','simulation') NOT NULL DEFAULT 'normal' COMMENT 'normal: 普通模式\nsimulation: 模拟模式\n\n模拟模式中会自动模拟数据生成给前期而不需要访问真实的接口\n',
  `base_url` varchar(45) NOT NULL COMMENT '项目的跟访问路径，这个选项是方便切换模式调试的开放人员可以不用改接口的地址',
  `db_name` varchar(255) DEFAULT NULL COMMENT '生成工程的数据库名称(一般web工程都需要连接数据库)',
  `root_path` varchar(255) DEFAULT NULL COMMENT '生成代码的root路径',
  `create_date` datetime DEFAULT NULL,
  `modify_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COMMENT='项目是承载api接口的载体，一个项目可以拥有多个接口';

-- ----------------------------
-- Table structure for w_result_data
-- ----------------------------
DROP TABLE IF EXISTS `w_result_data`;
CREATE TABLE `w_result_data` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL COMMENT '名称',
  `value` blob NOT NULL COMMENT '序列化的值',
  `type` enum('bool','numbe','date','string','byte','jsonobj','jsonarray') NOT NULL COMMENT '对应java中的关系\nbool: boolen\nnumber: int, long, flow, double, BigDecimal\ndate: java.util.date\nstring: string\nbyte: object\njsonobj: jsonObject\njsonarray: jsonArray\n''bool'',''numbe'',''date'',''string'',''byte''',
  `w_interface_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_w_result_data_w_interface1_idx` (`w_interface_id`),
  CONSTRAINT `fk_w_result_data_w_interface1` FOREIGN KEY (`w_interface_id`) REFERENCES `w_interface` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
