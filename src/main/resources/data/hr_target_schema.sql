
-- -----------------------------------------------------
-- Database: hr_target
-- Mục đích: Lưu thông tin nhân viên, phòng ban, lương và đánh giá hiệu suất
-- -----------------------------------------------------

DROP DATABASE IF EXISTS hr_target;
CREATE DATABASE hr_target CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE hr_target;

-- Bảng departments
CREATE TABLE departments (
    department_id INT AUTO_INCREMENT PRIMARY KEY,
    department_name VARCHAR(100) NOT NULL,
    manager_id INT NULL,
    location VARCHAR(100) NULL
);

-- Bảng employees
CREATE TABLE employees (
    employee_id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    gender ENUM('M','F','O') DEFAULT 'O',
    birth_date DATE,
    hire_date DATE,
    department_id INT,
    email VARCHAR(100),
    phone_number VARCHAR(20),
    FOREIGN KEY (department_id) REFERENCES departments(department_id)
        ON UPDATE CASCADE ON DELETE SET NULL
);

-- Bảng salaries
CREATE TABLE salaries (
    salary_id INT AUTO_INCREMENT PRIMARY KEY,
    employee_id INT NOT NULL,
    base_salary DECIMAL(10,2) NOT NULL,
    bonus DECIMAL(10,2) DEFAULT 0.00,
    effective_date DATE NOT NULL,
    FOREIGN KEY (employee_id) REFERENCES employees(employee_id)
        ON UPDATE CASCADE ON DELETE CASCADE
);

-- Bảng performance_review
CREATE TABLE performance_review (
    review_id INT AUTO_INCREMENT PRIMARY KEY,
    period VARCHAR(20) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    description VARCHAR(255)
);

-- Bảng employee_performance
CREATE TABLE employee_performance (
    id INT AUTO_INCREMENT PRIMARY KEY,
    employee_id INT NOT NULL,
    review_id INT NOT NULL,
    performance_score DECIMAL(4,2),
    comments VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employees(employee_id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (review_id) REFERENCES performance_review(review_id)
        ON UPDATE CASCADE ON DELETE CASCADE
);

-- Bảng department_performance
CREATE TABLE department_performance (
    dept_perf_id INT AUTO_INCREMENT PRIMARY KEY,
    department_id INT NOT NULL,
    review_id INT NOT NULL,
    average_score DECIMAL(4,2),
    ranking INT,
    remarks VARCHAR(255),
    FOREIGN KEY (department_id) REFERENCES departments(department_id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (review_id) REFERENCES performance_review(review_id)
        ON UPDATE CASCADE ON DELETE CASCADE
);

-- Bảng kpi_metrics
CREATE TABLE kpi_metrics (
    kpi_id INT AUTO_INCREMENT PRIMARY KEY,
    kpi_name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    weight DECIMAL(4,2),
    category VARCHAR(50)
);

-- Bảng task_performance
CREATE TABLE task_performance (
    task_id INT AUTO_INCREMENT PRIMARY KEY,
    employee_performance_id INT NOT NULL,
    task_name VARCHAR(100) NOT NULL,
    task_score DECIMAL(4,2),
    note VARCHAR(255),
    FOREIGN KEY (employee_performance_id) REFERENCES employee_performance(id)
        ON UPDATE CASCADE ON DELETE CASCADE
);
