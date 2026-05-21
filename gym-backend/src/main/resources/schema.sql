-- =============================================
-- GYM MANAGEMENT SYSTEM - DATABASE SCHEMA
-- PostgreSQL
-- =============================================

-- Drop tables if they exist (in reverse dependency order)
DROP TABLE IF EXISTS attendance CASCADE;
DROP TABLE IF EXISTS payments CASCADE;
DROP TABLE IF EXISTS members CASCADE;
DROP TABLE IF EXISTS trainers CASCADE;
DROP TABLE IF EXISTS membership_plans CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- =============================================
-- USERS TABLE (Authentication & Roles)
-- =============================================
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'RECEPTIONIST',
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_user_role CHECK (role IN ('ADMIN', 'TRAINER', 'RECEPTIONIST'))
);

-- =============================================
-- MEMBERSHIP PLANS TABLE
-- =============================================
CREATE TABLE membership_plans (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    duration_months INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    benefits TEXT,
    plan_type VARCHAR(20) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_plan_type CHECK (plan_type IN ('MONTHLY', 'QUARTERLY', 'YEARLY')),
    CONSTRAINT chk_duration_positive CHECK (duration_months > 0),
    CONSTRAINT chk_price_positive CHECK (price > 0)
);

-- =============================================
-- TRAINERS TABLE
-- =============================================
CREATE TABLE trainers (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(20) NOT NULL,
    specialization VARCHAR(100),
    experience_years INT DEFAULT 0,
    bio TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    user_id BIGINT UNIQUE REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =============================================
-- MEMBERS TABLE
-- =============================================
CREATE TABLE members (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(20) NOT NULL,
    gender VARCHAR(10) NOT NULL,
    date_of_birth DATE,
    address TEXT,
    emergency_contact VARCHAR(20),
    photo_url VARCHAR(255),
    qr_code VARCHAR(255) UNIQUE,
    membership_plan_id BIGINT REFERENCES membership_plans(id) ON DELETE SET NULL,
    trainer_id BIGINT REFERENCES trainers(id) ON DELETE SET NULL,
    membership_start_date DATE,
    membership_end_date DATE,
    membership_status VARCHAR(20) NOT NULL DEFAULT 'INACTIVE',
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_gender CHECK (gender IN ('MALE', 'FEMALE', 'OTHER')),
    CONSTRAINT chk_membership_status CHECK (membership_status IN ('ACTIVE', 'INACTIVE', 'EXPIRED', 'FROZEN'))
);

-- =============================================
-- PAYMENTS TABLE
-- =============================================
CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    member_id BIGINT NOT NULL REFERENCES members(id) ON DELETE CASCADE,
    membership_plan_id BIGINT REFERENCES membership_plans(id) ON DELETE SET NULL,
    amount DECIMAL(10, 2) NOT NULL,
    payment_method VARCHAR(20) NOT NULL,
    payment_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    transaction_id VARCHAR(100),
    payment_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    due_date DATE,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_payment_method CHECK (payment_method IN ('CASH', 'CARD', 'UPI', 'BANK_TRANSFER')),
    CONSTRAINT chk_payment_status CHECK (payment_status IN ('PAID', 'PENDING', 'FAILED', 'REFUNDED')),
    CONSTRAINT chk_amount_positive CHECK (amount > 0)
);

-- =============================================
-- ATTENDANCE TABLE
-- =============================================
CREATE TABLE attendance (
    id BIGSERIAL PRIMARY KEY,
    member_id BIGINT NOT NULL REFERENCES members(id) ON DELETE CASCADE,
    check_in_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    check_out_time TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'CHECKED_IN',
    check_in_method VARCHAR(20) NOT NULL DEFAULT 'MANUAL',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_attendance_status CHECK (status IN ('CHECKED_IN', 'CHECKED_OUT')),
    CONSTRAINT chk_checkin_method CHECK (check_in_method IN ('MANUAL', 'QR_CODE', 'ID_CARD'))
);

-- =============================================
-- INDEXES
-- =============================================
CREATE INDEX idx_members_membership_status ON members(membership_status);
CREATE INDEX idx_members_trainer_id ON members(trainer_id);
CREATE INDEX idx_members_plan_id ON members(membership_plan_id);
CREATE INDEX idx_members_qr_code ON members(qr_code);
CREATE INDEX idx_payments_member_id ON payments(member_id);
CREATE INDEX idx_payments_status ON payments(payment_status);
CREATE INDEX idx_payments_date ON payments(payment_date);
CREATE INDEX idx_attendance_member_id ON attendance(member_id);
CREATE INDEX idx_attendance_checkin ON attendance(check_in_time);
CREATE INDEX idx_trainers_user_id ON trainers(user_id);

-- =============================================
-- SEED DATA
-- =============================================

-- Default admin user (password: admin123)
INSERT INTO users (username, email, password, full_name, role)
VALUES ('admin', 'admin@gym.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'System Admin', 'ADMIN');

-- Default membership plans
INSERT INTO membership_plans (name, duration_months, price, benefits, plan_type) VALUES
('Basic Monthly', 1, 999.00, 'Access to gym floor, Basic equipment usage', 'MONTHLY'),
('Standard Quarterly', 3, 2499.00, 'Access to gym floor, All equipment, Group classes', 'QUARTERLY'),
('Premium Yearly', 12, 7999.00, 'Full access, Personal trainer sessions, Sauna, Locker', 'YEARLY');
