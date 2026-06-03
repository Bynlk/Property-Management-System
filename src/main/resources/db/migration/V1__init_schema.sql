-- V1: 初始化数据库 Schema
-- 小区物业管理系统 DDL（仅结构，不含测试数据）

SET FOREIGN_KEY_CHECKS = 0;

-- 1. 员工表
CREATE TABLE IF NOT EXISTS `employee` (
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
CREATE TABLE IF NOT EXISTS `owner` (
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
CREATE TABLE IF NOT EXISTS `house` (
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
CREATE TABLE IF NOT EXISTS `fee` (
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
CREATE TABLE IF NOT EXISTS `parking` (
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
CREATE TABLE IF NOT EXISTS `complaint` (
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
CREATE TABLE IF NOT EXISTS `repair` (
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
CREATE TABLE IF NOT EXISTS `duty` (
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
CREATE TABLE IF NOT EXISTS `sys_user` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码(BCrypt加密)',
    `real_name` VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
    `role` ENUM('admin','user') NOT NULL DEFAULT 'user' COMMENT '角色: admin/user',
    `token_version` INT NOT NULL DEFAULT 0 COMMENT 'Token版本号，修改密码时递增使旧Token失效',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- 10. 状态变更审计日志表
CREATE TABLE IF NOT EXISTS `status_change_log` (
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
