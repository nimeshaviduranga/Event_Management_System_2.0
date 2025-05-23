-- Insert seed data for Event Management System
-- Passwords are BCrypt encoded with strength 12

INSERT INTO users (id, name, email, password, role, created_at, updated_at, is_active)
VALUES (
           '11111',
           'Admin User',
           'admin@test.com',
           '$2a$12$LQv3c1y',
           'ADMIN',
           NOW(),
           NOW(),
           true
       );

INSERT INTO users (id, name, email, password, role, created_at, updated_at, is_active)
VALUES (
           '22222',
           'User1',
           'user@test.com',
           '$2a$12$92IXU.',
           'USER',
           NOW(),
           NOW(),
           true
       );

INSERT INTO users (id, name, email, password, role, created_at, updated_at, is_active)
VALUES (
           '33333',
           'Host1',
           'host@test.com',
           '$2a$12$6kzn/oJnm',
           'USER',
           NOW(),
           NOW(),
           true
       );

INSERT INTO events (id, title, description, host_id, start_time, end_time, location, visibility, created_at, updated_at, is_deleted)
VALUES (
           '44444',
           'Test title',
           'test description',
           '33333',
           NOW() + INTERVAL '30 days',
           NOW() + INTERVAL '32 days',
           'New York',
           'PUBLIC',
           NOW(),
           NOW(),
           false
       );

-- Insert attendance records
INSERT INTO attendance (event_id, user_id, status, responded_at)
VALUES (
           '44444444-4444',
           '11111',
           'GOING',
           NOW()
       );

INSERT INTO attendance (event_id, user_id, status, responded_at)
VALUES (
           '44444444-4444',
           '22222',
           'GOING',
           NOW()
       );

INSERT INTO attendance (event_id, user_id, status, responded_at)
VALUES (
           '55555555-5555',
           '11111',
           'MAYBE',
           NOW()
       );

INSERT INTO attendance (event_id, user_id, status, responded_at)
VALUES (
           '66666666-6666',
           '33333',
           'GOING',
           NOW()
       );

-- Display seed data summary
DO $$
BEGIN
    RAISE NOTICE 'Seed data inserted successfully!';
    RAISE NOTICE 'Users created: 3 (1 admin, 2 regular users)';
    RAISE NOTICE 'Events created: 3 (2 public, 1 private)';
    RAISE NOTICE 'Attendance records: 4';
    RAISE NOTICE 'Default login credentials:';
    RAISE NOTICE '  Admin: admin@test.com / admin123';
    RAISE NOTICE '  User: user@test.com / user123';
    RAISE NOTICE '  Host: host@test.com / host123';
END $$;