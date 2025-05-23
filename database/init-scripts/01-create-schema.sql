-- Create database schema for Event Management System
-- This script will be executed when the PostgreSQL container starts

-- Create enum type for user roles
CREATE TYPE user_role AS ENUM ('USER', 'ADMIN');

-- Create users table
CREATE TABLE users (
                       id UUID PRIMARY KEY,
                       name VARCHAR(100) NOT NULL,
                       email VARCHAR(100) NOT NULL UNIQUE,
                       password VARCHAR(100) NOT NULL,
                       role user_role NOT NULL DEFAULT 'USER',
                       created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
                       updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
                       is_active BOOLEAN NOT NULL DEFAULT TRUE
);

-- Create index on email for faster lookups
CREATE INDEX idx_users_email ON users(email);

-- Create enum type for event visibility
CREATE TYPE event_visibility AS ENUM ('PUBLIC', 'PRIVATE');

-- Create events table
CREATE TABLE events (
                        id UUID PRIMARY KEY,
                        title VARCHAR(100) NOT NULL,
                        description TEXT NOT NULL,
                        host_id UUID NOT NULL REFERENCES users(id),
                        start_time TIMESTAMP WITH TIME ZONE NOT NULL,
                        end_time TIMESTAMP WITH TIME ZONE NOT NULL,
                        location VARCHAR(100) NOT NULL,
                        visibility event_visibility NOT NULL DEFAULT 'PUBLIC',
                        created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
                        updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
                        is_deleted BOOLEAN NOT NULL DEFAULT FALSE
);

-- Create indexes for faster queries
CREATE INDEX idx_events_host_id ON events(host_id);
CREATE INDEX idx_events_start_time ON events(start_time);
CREATE INDEX idx_events_location ON events(location);
CREATE INDEX idx_events_visibility ON events(visibility);
CREATE INDEX idx_events_is_deleted ON events(is_deleted);

-- Create enum type for attendance status
CREATE TYPE attendance_status AS ENUM ('GOING', 'MAYBE', 'DECLINED');

-- Create attendance table
CREATE TABLE attendance (
                            event_id UUID NOT NULL REFERENCES events(id),
                            user_id UUID NOT NULL REFERENCES users(id),
                            status attendance_status NOT NULL DEFAULT 'GOING',
                            responded_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
                            PRIMARY KEY (event_id, user_id)
);

-- Create indexes for faster queries
CREATE INDEX idx_attendance_event_id ON attendance(event_id);
CREATE INDEX idx_attendance_user_id ON attendance(user_id);
CREATE INDEX idx_attendance_status ON attendance(status);

-- Add comments for documentation
COMMENT ON TABLE users IS 'Stores user account information';
COMMENT ON TABLE events IS 'Stores event information';
COMMENT ON TABLE attendance IS 'Records user attendance at events';