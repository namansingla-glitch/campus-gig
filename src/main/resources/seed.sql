-- Optional demo data so you can click around right away. Run AFTER schema.sql:
--   mysql -u root -p < src/main/resources/seed.sql
-- Safe to run more than once. Do not load this on a real deployment.
--
-- All five demo accounts use the password:  password123
--   student: naman@campus.edu, shreyash@campus.edu, shivam@campus.edu
--   poster : riddhi@campus.edu, mohak@campus.edu

USE gig_marketplace;

INSERT IGNORE INTO users (name, email, password_hash, role) VALUES
    ('Naman',    'naman@campus.edu',    '$2a$12$3ViGGQDDxWlD91S18ozF7uZQaoikXQWG3vuJ/X/Jvx5zOyvDfKJf2', 'student'),
    ('Riddhi',   'riddhi@campus.edu',   '$2a$12$3ViGGQDDxWlD91S18ozF7uZQaoikXQWG3vuJ/X/Jvx5zOyvDfKJf2', 'poster'),
    ('Shreyash', 'shreyash@campus.edu', '$2a$12$3ViGGQDDxWlD91S18ozF7uZQaoikXQWG3vuJ/X/Jvx5zOyvDfKJf2', 'student'),
    ('Shivam',   'shivam@campus.edu',   '$2a$12$3ViGGQDDxWlD91S18ozF7uZQaoikXQWG3vuJ/X/Jvx5zOyvDfKJf2', 'student'),
    ('Mohak',    'mohak@campus.edu',    '$2a$12$3ViGGQDDxWlD91S18ozF7uZQaoikXQWG3vuJ/X/Jvx5zOyvDfKJf2', 'poster');

SET @naman    = (SELECT user_id FROM users WHERE email = 'naman@campus.edu');
SET @riddhi   = (SELECT user_id FROM users WHERE email = 'riddhi@campus.edu');
SET @shreyash = (SELECT user_id FROM users WHERE email = 'shreyash@campus.edu');
SET @shivam   = (SELECT user_id FROM users WHERE email = 'shivam@campus.edu');
SET @mohak    = (SELECT user_id FROM users WHERE email = 'mohak@campus.edu');

-- Gigs posted by Riddhi (all open)
INSERT INTO gigs (poster_id, title, description, budget, category, application_deadline, completion_deadline, status, created_at)
SELECT @riddhi, 'Campus Event Poster',
       'Design an A3 poster for the annual tech fest. We will share the logo, date and venue. Looking for a bold, modern look that works in print and on Instagram.',
       900.00, 'Design', DATE_ADD(CURDATE(), INTERVAL 5 DAY), DATE_ADD(CURDATE(), INTERVAL 12 DAY), 'OPEN', NOW() - INTERVAL 1 DAY
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM gigs WHERE poster_id = @riddhi AND title = 'Campus Event Poster');

INSERT INTO gigs (poster_id, title, description, budget, category, application_deadline, completion_deadline, status, created_at)
SELECT @riddhi, 'Build Student Portal',
       'Build a small web portal where students can see their timetable and department notices. Java or Python is fine. A simple login and a clean, responsive UI are the main requirements.',
       3000.00, 'Development', DATE_ADD(CURDATE(), INTERVAL 14 DAY), DATE_ADD(CURDATE(), INTERVAL 45 DAY), 'OPEN', NOW() - INTERVAL 2 DAY
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM gigs WHERE poster_id = @riddhi AND title = 'Build Student Portal');

INSERT INTO gigs (poster_id, title, description, budget, category, application_deadline, completion_deadline, status, created_at)
SELECT @riddhi, 'Python Tutor for First-Years',
       'Weekly one-hour sessions teaching Python basics (loops, functions, lists) to a small group of first-year students. Timings are flexible and can be agreed with the tutor.',
       1500.00, 'Tutoring', NULL, NULL, 'OPEN', NOW() - INTERVAL 3 DAY
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM gigs WHERE poster_id = @riddhi AND title = 'Python Tutor for First-Years');

-- Gigs posted by Mohak (one open, one in progress, one completed)
INSERT INTO gigs (poster_id, title, description, budget, category, application_deadline, completion_deadline, status, created_at)
SELECT @mohak, 'Blog Posts for the Writing Club',
       'Write four 600-word blog posts about campus life for the Writing Club website. Topics will be agreed with you. Good grammar and an engaging voice matter most.',
       800.00, 'Writing', NULL, NULL, 'OPEN', NOW() - INTERVAL 4 DAY
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM gigs WHERE poster_id = @mohak AND title = 'Blog Posts for the Writing Club');

INSERT INTO gigs (poster_id, title, description, budget, category, application_deadline, completion_deadline, status, created_at)
SELECT @mohak, 'Photography for Tech Fest',
       'Cover the two-day tech fest: keynote, workshops and the crowd. Deliver around 60 edited photos within a week of the event.',
       1200.00, 'Event Support', DATE_SUB(CURDATE(), INTERVAL 3 DAY), DATE_ADD(CURDATE(), INTERVAL 6 DAY), 'IN_PROGRESS', NOW() - INTERVAL 10 DAY
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM gigs WHERE poster_id = @mohak AND title = 'Photography for Tech Fest');

INSERT INTO gigs (poster_id, title, description, budget, category, application_deadline, completion_deadline, status, created_at, completed_at)
SELECT @mohak, 'Lab Manual Formatting',
       'Format a 40-page lab manual to the department template: headings, figure captions, table of contents and page numbers.',
       600.00, 'Writing', DATE_SUB(CURDATE(), INTERVAL 20 DAY), DATE_SUB(CURDATE(), INTERVAL 5 DAY), 'COMPLETED', NOW() - INTERVAL 25 DAY, NOW() - INTERVAL 3 DAY
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM gigs WHERE poster_id = @mohak AND title = 'Lab Manual Formatting');

SET @g_poster = (SELECT gig_id FROM gigs WHERE poster_id = @riddhi AND title = 'Campus Event Poster');
SET @g_portal = (SELECT gig_id FROM gigs WHERE poster_id = @riddhi AND title = 'Build Student Portal');
SET @g_tutor  = (SELECT gig_id FROM gigs WHERE poster_id = @riddhi AND title = 'Python Tutor for First-Years');
SET @g_photo  = (SELECT gig_id FROM gigs WHERE poster_id = @mohak  AND title = 'Photography for Tech Fest');
SET @g_lab    = (SELECT gig_id FROM gigs WHERE poster_id = @mohak  AND title = 'Lab Manual Formatting');

-- Applications (no portfolio files for demo rows)
INSERT IGNORE INTO applications (gig_id, applicant_id, pitch_text, status) VALUES
    (@g_poster, @naman,    'I have designed posters for two college clubs in Figma and Canva. I can share a few samples and deliver a first draft in two days.', 'PENDING'),
    (@g_portal, @naman,    'I have built full-stack projects with Java servlets and MySQL, including a campus marketplace. Happy to walk through them.', 'PENDING'),
    (@g_portal, @shreyash, 'I enjoy building dashboards and have worked with Spring Boot and React. I can start this week.', 'PENDING'),
    (@g_tutor,  @shreyash, 'I tutored Python at my school coding club for a year and love explaining basics with small exercises.', 'PENDING'),
    (@g_photo,  @shivam,   'I shoot with a mirrorless camera and covered last year''s cultural fest. Fast turnaround on edits.', 'HIRED'),
    (@g_photo,  @naman,    'I can help with photos and basic editing, though I am newer to event photography.', 'REJECTED'),
    (@g_lab,    @naman,    'I am comfortable with Word styles, automatic tables of contents and caption numbering.', 'HIRED'),
    (@g_lab,    @shreyash, 'I have formatted several reports for my department and know the template well.', 'REJECTED');

INSERT IGNORE INTO reviews (gig_id, reviewer_id, reviewee_id, rating, comment) VALUES
    (@g_lab, @mohak, @naman, 5, 'Fast, careful and the formatting was spotless. Would happily hire again.');
