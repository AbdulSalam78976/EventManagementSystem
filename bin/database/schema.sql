-- Create database if not exists
CREATE DATABASE IF NOT EXISTS event_management;
USE event_management;

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'EVENT_ORGANIZER', 'ATTENDEE') NOT NULL,
    phone VARCHAR(20),
    active BOOLEAN DEFAULT TRUE,
    registration_date DATE NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_login_at DATETIME NULL,
    security_question1 VARCHAR(255) NOT NULL,
    security_answer1 VARCHAR(255) NOT NULL,
    security_question2 VARCHAR(255) NOT NULL,
    security_answer2 VARCHAR(255) NOT NULL
);

-- Events table
CREATE TABLE IF NOT EXISTS events (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    event_date DATETIME NOT NULL,
    registration_deadline DATETIME NOT NULL,
    venue_name VARCHAR(255) NOT NULL,
    total_slots INT NOT NULL,
    available_slots INT NOT NULL,
    organizer_id INT NOT NULL,
    category VARCHAR(100) NOT NULL,
    contact_info VARCHAR(255),
    eligibility_criteria TEXT,
    schedule TEXT,
    status ENUM('DRAFT', 'PENDING', 'APPROVED', 'REJECTED', 'CANCELLED', 'COMPLETED') DEFAULT 'DRAFT',
    main_image LONGBLOB,
    main_image_type VARCHAR(50),
    additional_documents LONGBLOB,
    additional_documents_type VARCHAR(50),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (organizer_id) REFERENCES users(id)
);

-- Registrations table
CREATE TABLE IF NOT EXISTS registrations (
    id INT PRIMARY KEY AUTO_INCREMENT,
    event_id INT NOT NULL,
    user_id INT NOT NULL,
    registration_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    status ENUM('PENDING', 'APPROVED', 'REJECTED', 'CANCELLED', 'REGISTERED', 'WAITLISTED', 'ATTENDED', 'NO_SHOW') DEFAULT 'PENDING',
    checked_in BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (event_id) REFERENCES events(id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    UNIQUE KEY unique_registration (event_id, user_id)
);

-- Feedback table
CREATE TABLE IF NOT EXISTS feedback (
    id INT PRIMARY KEY AUTO_INCREMENT,
    event_id INT NOT NULL,
    user_id INT NOT NULL,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    is_anonymous BOOLEAN DEFAULT FALSE,
    submitted_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (event_id) REFERENCES events(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Notifications table
CREATE TABLE IF NOT EXISTS notifications (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    title VARCHAR(100) NOT NULL,
    message TEXT NOT NULL,
    type ENUM('EVENT_CREATED', 'EVENT_UPDATED', 'EVENT_CANCELLED', 'REGISTRATION_APPROVED', 'REGISTRATION_REJECTED', 'EVENT_REMINDER', 'SYSTEM') DEFAULT 'SYSTEM',
    event_id INT,
    is_read BOOLEAN DEFAULT FALSE,
    read_at DATETIME NULL,
    action_url VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (event_id) REFERENCES events(id)
);

-- Insert default admin user with all required fields
INSERT INTO users (
    name, 
    email, 
    password, 
    role,
    phone,
    registration_date,
    security_question1,
    security_answer1,
    security_question2,
    security_answer2
) VALUES (
    'Admin',
    'admin@example.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', -- hashed 'admin123'
    'ADMIN',
    '1234567890',
    CURRENT_DATE,
    'What is your mother''s maiden name?',
    'Smith',
    'What was your first pet''s name?',
    'Buddy'
) ON DUPLICATE KEY UPDATE id=id; 