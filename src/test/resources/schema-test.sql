-- H2-compatible DDL extracted from property_management.sql
-- All MySQL-specific syntax converted for H2 compatibility

-- 1. employee table
CREATE TABLE IF NOT EXISTS employee (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    gender VARCHAR(2) DEFAULT NULL,
    phone VARCHAR(20) DEFAULT NULL UNIQUE,
    position VARCHAR(50) DEFAULT NULL,
    hire_date DATE DEFAULT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_employee_name ON employee(name);

-- 2. owner table
CREATE TABLE IF NOT EXISTS owner (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    gender VARCHAR(2) DEFAULT NULL,
    phone VARCHAR(20) DEFAULT NULL UNIQUE,
    id_card VARCHAR(18) DEFAULT NULL UNIQUE,
    move_in_date DATE DEFAULT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_owner_name ON owner(name);

-- 3. house table
CREATE TABLE IF NOT EXISTS house (
    id INT PRIMARY KEY AUTO_INCREMENT,
    building VARCHAR(20) NOT NULL,
    unit VARCHAR(10) DEFAULT NULL,
    room_number VARCHAR(20) NOT NULL,
    area DECIMAL(10,2) DEFAULT NULL,
    house_type VARCHAR(20) DEFAULT NULL,
    owner_id INT DEFAULT NULL,
    status VARCHAR(10) DEFAULT '空置',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_house UNIQUE (building, unit, room_number)
);
CREATE INDEX IF NOT EXISTS idx_house_owner ON house(owner_id);
CREATE INDEX IF NOT EXISTS idx_house_status ON house(status);
ALTER TABLE house ADD CONSTRAINT fk_house_owner FOREIGN KEY (owner_id) REFERENCES owner(id) ON DELETE SET NULL;

-- 4. fee table
CREATE TABLE IF NOT EXISTS fee (
    id INT PRIMARY KEY AUTO_INCREMENT,
    owner_id INT NOT NULL,
    house_id INT DEFAULT NULL,
    fee_type VARCHAR(10) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    should_pay_date DATE NOT NULL,
    paid_date DATE DEFAULT NULL,
    status VARCHAR(10) DEFAULT '未缴',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_fee_owner ON fee(owner_id);
CREATE INDEX IF NOT EXISTS idx_fee_house ON fee(house_id);
CREATE INDEX IF NOT EXISTS idx_fee_status ON fee(status);
CREATE INDEX IF NOT EXISTS idx_fee_owner_status ON fee(owner_id, status);
ALTER TABLE fee ADD CONSTRAINT fk_fee_owner FOREIGN KEY (owner_id) REFERENCES owner(id) ON DELETE RESTRICT;
ALTER TABLE fee ADD CONSTRAINT fk_fee_house FOREIGN KEY (house_id) REFERENCES house(id) ON DELETE SET NULL;

-- 5. parking table
CREATE TABLE IF NOT EXISTS parking (
    id INT PRIMARY KEY AUTO_INCREMENT,
    spot_number VARCHAR(20) NOT NULL,
    license_plate VARCHAR(20) DEFAULT NULL,
    owner_id INT DEFAULT NULL,
    status VARCHAR(10) DEFAULT '空闲',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_parking_owner ON parking(owner_id);
CREATE INDEX IF NOT EXISTS idx_parking_status ON parking(status);
CREATE UNIQUE INDEX IF NOT EXISTS idx_parking_spot_number ON parking(spot_number);
ALTER TABLE parking ADD CONSTRAINT fk_parking_owner FOREIGN KEY (owner_id) REFERENCES owner(id) ON DELETE SET NULL;

-- 6. complaint table
CREATE TABLE IF NOT EXISTS complaint (
    id INT PRIMARY KEY AUTO_INCREMENT,
    owner_id INT NOT NULL,
    title VARCHAR(100) NOT NULL,
    content TEXT,
    status VARCHAR(10) DEFAULT '待处理',
    resolved_at DATETIME DEFAULT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_complaint_owner ON complaint(owner_id);
CREATE INDEX IF NOT EXISTS idx_complaint_status ON complaint(status);
ALTER TABLE complaint ADD CONSTRAINT fk_complaint_owner FOREIGN KEY (owner_id) REFERENCES owner(id) ON DELETE RESTRICT;

-- 7. repair table
CREATE TABLE IF NOT EXISTS repair (
    id INT PRIMARY KEY AUTO_INCREMENT,
    owner_id INT NOT NULL,
    device_name VARCHAR(100) NOT NULL,
    fault_description TEXT,
    repair_employee_id INT DEFAULT NULL,
    status VARCHAR(10) DEFAULT '待维修',
    completed_at DATETIME DEFAULT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_repair_owner ON repair(owner_id);
CREATE INDEX IF NOT EXISTS idx_repair_status ON repair(status);
ALTER TABLE repair ADD CONSTRAINT fk_repair_owner FOREIGN KEY (owner_id) REFERENCES owner(id) ON DELETE RESTRICT;
ALTER TABLE repair ADD CONSTRAINT fk_repair_employee FOREIGN KEY (repair_employee_id) REFERENCES employee(id) ON DELETE SET NULL;

-- 8. duty table
CREATE TABLE IF NOT EXISTS duty (
    id INT PRIMARY KEY AUTO_INCREMENT,
    employee_id INT NOT NULL,
    duty_date DATE NOT NULL,
    shift VARCHAR(4) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_duty UNIQUE (employee_id, duty_date, shift)
);
CREATE INDEX IF NOT EXISTS idx_duty_employee ON duty(employee_id);
CREATE INDEX IF NOT EXISTS idx_duty_date ON duty(duty_date);
ALTER TABLE duty ADD CONSTRAINT fk_duty_employee FOREIGN KEY (employee_id) REFERENCES employee(id) ON DELETE CASCADE;

-- 9. sys_user table
CREATE TABLE IF NOT EXISTS sys_user (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    real_name VARCHAR(50) DEFAULT NULL,
    role VARCHAR(10) NOT NULL DEFAULT 'user',
    token_version INT NOT NULL DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 10. status_change_log table
CREATE TABLE IF NOT EXISTS status_change_log (
    id INT PRIMARY KEY AUTO_INCREMENT,
    entity_type VARCHAR(20) NOT NULL,
    entity_id INT NOT NULL,
    old_status VARCHAR(20),
    new_status VARCHAR(20) NOT NULL,
    changed_by VARCHAR(50),
    changed_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_scl_entity ON status_change_log(entity_type, entity_id);
CREATE INDEX IF NOT EXISTS idx_scl_changed_at ON status_change_log(changed_at);
