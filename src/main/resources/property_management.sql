-- 小区物业管理系统 数据库建表脚本
-- 数据库: property_management
-- 字符集: utf8mb4

CREATE DATABASE IF NOT EXISTS property_management DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE property_management;

SET FOREIGN_KEY_CHECKS = 0;

-- 1. 员工表
DROP TABLE IF EXISTS `employee`;
CREATE TABLE `employee` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '员工ID',
    `name` VARCHAR(50) NOT NULL COMMENT '姓名',
    `gender` ENUM('男','女') DEFAULT NULL COMMENT '性别',
    `phone` VARCHAR(20) DEFAULT NULL UNIQUE COMMENT '手机号',
    `position` VARCHAR(50) DEFAULT NULL COMMENT '岗位',
    `hire_date` DATE DEFAULT NULL COMMENT '入职日期',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_employee_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工表';

-- 2. 业主表
DROP TABLE IF EXISTS `owner`;
CREATE TABLE `owner` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '业主ID',
    `name` VARCHAR(50) NOT NULL COMMENT '姓名',
    `gender` ENUM('男','女') DEFAULT NULL COMMENT '性别',
    `phone` VARCHAR(20) DEFAULT NULL UNIQUE COMMENT '手机号',
    `id_card` VARCHAR(18) DEFAULT NULL UNIQUE COMMENT '身份证号',
    `move_in_date` DATE DEFAULT NULL COMMENT '入住日期',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_owner_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='业主表';

-- 3. 房屋表
DROP TABLE IF EXISTS `house`;
CREATE TABLE `house` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '房屋ID',
    `building` VARCHAR(20) NOT NULL COMMENT '楼栋号',
    `unit` VARCHAR(10) DEFAULT NULL COMMENT '单元号',
    `room_number` VARCHAR(20) NOT NULL COMMENT '房间号',
    `area` DECIMAL(10,2) DEFAULT NULL COMMENT '面积(㎡)' CHECK (`area` > 0),
    `house_type` VARCHAR(20) DEFAULT NULL COMMENT '户型',
    `owner_id` INT DEFAULT NULL COMMENT '业主ID',
    `status` ENUM('已入住','空置','装修中') DEFAULT '空置' COMMENT '入住状态',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uk_house` (`building`, `unit`, `room_number`),
    INDEX `idx_house_owner` (`owner_id`),
    INDEX `idx_house_status` (`status`),
    FOREIGN KEY (`owner_id`) REFERENCES `owner`(`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房屋表';

-- 4. 费用账单表
DROP TABLE IF EXISTS `fee`;
CREATE TABLE `fee` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '费用ID',
    `owner_id` INT NOT NULL COMMENT '业主ID',
    `house_id` INT DEFAULT NULL COMMENT '房屋ID',
    `fee_type` ENUM('物业费','水费','电费','燃气费') NOT NULL COMMENT '费用类型: 物业费/水费/电费/燃气费',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '金额' CHECK (`amount` > 0),
    `should_pay_date` DATE NOT NULL COMMENT '应缴日期',
    `paid_date` DATE DEFAULT NULL COMMENT '实际缴费日期',
    `status` ENUM('未缴','已缴') DEFAULT '未缴' COMMENT '缴费状态',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_fee_owner` (`owner_id`),
    INDEX `idx_fee_house` (`house_id`),
    INDEX `idx_fee_status` (`status`),
    INDEX `idx_fee_owner_status` (`owner_id`, `status`),
    FOREIGN KEY (`owner_id`) REFERENCES `owner`(`id`) ON DELETE RESTRICT,
    FOREIGN KEY (`house_id`) REFERENCES `house`(`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='费用账单表';

-- 5. 停车位表
DROP TABLE IF EXISTS `parking`;
CREATE TABLE `parking` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '车位ID',
    `spot_number` VARCHAR(20) NOT NULL COMMENT '车位编号',
    `license_plate` VARCHAR(20) DEFAULT NULL COMMENT '车牌号',
    `owner_id` INT DEFAULT NULL COMMENT '业主ID',
    `status` ENUM('使用中','空闲') DEFAULT '空闲' COMMENT '使用状态',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_parking_owner` (`owner_id`),
    INDEX `idx_parking_status` (`status`),
    UNIQUE INDEX `idx_parking_spot_number` (`spot_number`),
    FOREIGN KEY (`owner_id`) REFERENCES `owner`(`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='停车位表';

-- 6. 投诉表
DROP TABLE IF EXISTS `complaint`;
CREATE TABLE `complaint` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '投诉ID',
    `owner_id` INT NOT NULL COMMENT '业主ID',
    `title` VARCHAR(100) NOT NULL COMMENT '投诉标题',
    `content` TEXT COMMENT '投诉内容',
    `status` ENUM('待处理','处理中','已处理') DEFAULT '待处理' COMMENT '处理状态',
    `resolved_at` DATETIME DEFAULT NULL COMMENT '解决时间',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_complaint_owner` (`owner_id`),
    INDEX `idx_complaint_status` (`status`),
    FOREIGN KEY (`owner_id`) REFERENCES `owner`(`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='投诉表';

-- 7. 报修表
DROP TABLE IF EXISTS `repair`;
CREATE TABLE `repair` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '报修ID',
    `owner_id` INT NOT NULL COMMENT '业主ID',
    `device_name` VARCHAR(100) NOT NULL COMMENT '设备名称',
    `fault_description` TEXT COMMENT '故障描述',
    `repair_employee_id` INT DEFAULT NULL COMMENT '维修人员(员工ID)',
    `status` ENUM('待维修','维修中','已完成') DEFAULT '待维修' COMMENT '维修状态',
    `completed_at` DATETIME DEFAULT NULL COMMENT '完成时间',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_repair_owner` (`owner_id`),
    INDEX `idx_repair_status` (`status`),
    FOREIGN KEY (`owner_id`) REFERENCES `owner`(`id`) ON DELETE RESTRICT,
    FOREIGN KEY (`repair_employee_id`) REFERENCES `employee`(`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报修表';

-- 8. 值班表
DROP TABLE IF EXISTS `duty`;
CREATE TABLE `duty` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '值班ID',
    `employee_id` INT NOT NULL COMMENT '员工ID',
    `duty_date` DATE NOT NULL COMMENT '值班日期',
    `shift` ENUM('早班','中班','晚班') NOT NULL COMMENT '班次',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uk_duty` (`employee_id`, `duty_date`, `shift`),
    INDEX `idx_duty_employee` (`employee_id`),
    INDEX `idx_duty_date` (`duty_date`),
    FOREIGN KEY (`employee_id`) REFERENCES `employee`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='值班表';

-- 9. 系统用户表
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码(BCrypt加密)',
    `real_name` VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
    `role` ENUM('admin','user') NOT NULL DEFAULT 'user' COMMENT '角色: admin/user',
    `token_version` INT NOT NULL DEFAULT 0 COMMENT 'Token版本号，修改密码时递增使旧Token失效',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- ==================== 测试数据 ====================

-- 员工数据
INSERT INTO `employee` (`name`, `gender`, `phone`, `position`, `hire_date`, `created_at`, `updated_at`) VALUES
('张三', '男', '13800001111', '物业经理', '2020-01-15', '2024-01-01 00:00:00', '2024-01-01 00:00:00'),
('李四', '女', '13800002222', '前台客服', '2021-03-10', '2024-01-01 00:00:00', '2024-01-01 00:00:00'),
('王五', '男', '13800003333', '维修工程师', '2019-06-20', '2024-01-01 00:00:00', '2024-01-01 00:00:00'),
('赵六', '男', '13800004444', '保安队长', '2018-11-01', '2024-01-01 00:00:00', '2024-01-01 00:00:00'),
('钱七', '女', '13800005555', '财务主管', '2020-07-08', '2024-01-01 00:00:00', '2024-01-01 00:00:00');

-- 业主数据
INSERT INTO `owner` (`name`, `gender`, `phone`, `id_card`, `move_in_date`, `created_at`, `updated_at`) VALUES
('刘备', '男', '13900001111', '110101199001011234', '2022-01-10', '2024-01-01 00:00:00', '2024-01-01 00:00:00'),
('关羽', '男', '13900002222', '110101199002021234', '2022-03-15', '2024-01-01 00:00:00', '2024-01-01 00:00:00'),
('张飞', '男', '13900003333', '110101199003031234', '2021-06-20', '2024-01-01 00:00:00', '2024-01-01 00:00:00'),
('赵云', '男', '13900004444', '110101199004041234', '2023-01-05', '2024-01-01 00:00:00', '2024-01-01 00:00:00'),
('黄忠', '男', '13900005555', '110101199005051234', '2022-08-18', '2024-01-01 00:00:00', '2024-01-01 00:00:00');

-- 房屋数据
INSERT INTO `house` (`building`, `unit`, `room_number`, `area`, `house_type`, `owner_id`, `status`, `created_at`, `updated_at`) VALUES
('1号楼', '1单元', '101', 89.50, '两室一厅', 1, '已入住', '2024-01-01 00:00:00', '2024-01-01 00:00:00'),
('1号楼', '1单元', '102', 120.00, '三室两厅', 2, '已入住', '2024-01-01 00:00:00', '2024-01-01 00:00:00'),
('1号楼', '2单元', '201', 75.30, '一室一厅', 3, '已入住', '2024-01-01 00:00:00', '2024-01-01 00:00:00'),
('2号楼', '1单元', '301', 135.00, '四室两厅', 4, '已入住', '2024-01-01 00:00:00', '2024-01-01 00:00:00'),
('2号楼', '2单元', '402', 95.00, '两室一厅', 5, '已入住', '2024-01-01 00:00:00', '2024-01-01 00:00:00'),
('3号楼', '1单元', '501', 110.00, '三室一厅', NULL, '空置', '2024-01-01 00:00:00', '2024-01-01 00:00:00');

-- 费用账单数据
INSERT INTO `fee` (`owner_id`, `house_id`, `fee_type`, `amount`, `should_pay_date`, `paid_date`, `status`, `created_at`, `updated_at`) VALUES
(1, 1, '物业费', 1200.00, '2024-01-01', NULL, '未缴', '2024-01-01 00:00:00', '2024-01-01 00:00:00'),
(1, 1, '水费', 85.50, '2024-01-15', NULL, '未缴', '2024-01-01 00:00:00', '2024-01-01 00:00:00'),
(2, 2, '物业费', 1500.00, '2024-01-01', '2024-01-05', '已缴', '2024-01-01 00:00:00', '2024-01-01 00:00:00'),
(3, 3, '电费', 220.00, '2024-02-01', NULL, '未缴', '2024-01-01 00:00:00', '2024-01-01 00:00:00'),
(4, 4, '物业费', 1800.00, '2024-01-01', NULL, '未缴', '2024-01-01 00:00:00', '2024-01-01 00:00:00'),
(5, 5, '燃气费', 150.00, '2024-01-20', '2024-01-18', '已缴', '2024-01-01 00:00:00', '2024-01-01 00:00:00');

-- 停车位数据
INSERT INTO `parking` (`spot_number`, `license_plate`, `owner_id`, `status`, `created_at`, `updated_at`) VALUES
('A-001', '京A12345', 1, '使用中', '2024-01-01 00:00:00', '2024-01-01 00:00:00'),
('A-002', '京B67890', 2, '使用中', '2024-01-01 00:00:00', '2024-01-01 00:00:00'),
('A-003', NULL, NULL, '空闲', '2024-01-01 00:00:00', '2024-01-01 00:00:00'),
('B-001', '京C11111', 3, '使用中', '2024-01-01 00:00:00', '2024-01-01 00:00:00'),
('B-002', '京D22222', 4, '使用中', '2024-01-01 00:00:00', '2024-01-01 00:00:00');

-- 投诉数据
INSERT INTO `complaint` (`owner_id`, `title`, `content`, `status`, `created_at`, `updated_at`) VALUES
(1, '楼上噪音扰民', '楼上住户经常在晚上10点后制造噪音，影响休息', '待处理', '2024-01-10 14:30:00', '2024-01-10 14:30:00'),
(2, '电梯故障', '1号楼电梯经常出现卡顿现象，存在安全隐患', '处理中', '2024-01-12 09:15:00', '2024-01-12 09:15:00'),
(3, '绿化带被占用', '有人在公共绿化带私自种菜', '已处理', '2024-01-08 16:45:00', '2024-01-08 16:45:00');

-- 报修数据
INSERT INTO `repair` (`owner_id`, `device_name`, `fault_description`, `repair_employee_id`, `status`, `created_at`, `updated_at`) VALUES
(1, '水龙头', '厨房水龙头漏水', 3, '已完成', '2024-01-15 00:00:00', '2024-01-15 00:00:00'),
(2, '空调', '空调不制冷', 3, '维修中', '2024-01-15 00:00:00', '2024-01-15 00:00:00'),
(3, '门锁', '防盗门锁芯损坏', NULL, '待维修', '2024-01-15 00:00:00', '2024-01-15 00:00:00');

-- 值班数据
INSERT INTO `duty` (`employee_id`, `duty_date`, `shift`, `created_at`, `updated_at`) VALUES
(1, '2024-01-15', '早班', '2024-01-15 00:00:00', '2024-01-15 00:00:00'),
(2, '2024-01-15', '中班', '2024-01-15 00:00:00', '2024-01-15 00:00:00'),
(3, '2024-01-15', '晚班', '2024-01-15 00:00:00', '2024-01-15 00:00:00'),
(4, '2024-01-16', '早班', '2024-01-16 00:00:00', '2024-01-16 00:00:00'),
(1, '2024-01-16', '中班', '2024-01-16 00:00:00', '2024-01-16 00:00:00');

-- 系统用户 (密码: admin123, BCrypt加密)
INSERT INTO `sys_user` (`username`, `password`, `real_name`, `role`, `created_at`, `updated_at`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', 'admin', '2024-01-01 00:00:00', '2024-01-01 00:00:00');

-- 10. 状态变更审计日志表
DROP TABLE IF EXISTS `status_change_log`;
CREATE TABLE `status_change_log` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    `entity_type` VARCHAR(20) NOT NULL COMMENT '实体类型: complaint/repair/fee/house/parking',
    `entity_id` INT NOT NULL COMMENT '实体ID',
    `old_status` VARCHAR(20) COMMENT '原状态',
    `new_status` VARCHAR(20) NOT NULL COMMENT '新状态',
    `changed_by` VARCHAR(50) COMMENT '操作人用户名',
    `changed_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '变更时间',
    INDEX `idx_scl_entity` (`entity_type`, `entity_id`),
    INDEX `idx_scl_changed_at` (`changed_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='状态变更审计日志表';

SET FOREIGN_KEY_CHECKS = 1;
