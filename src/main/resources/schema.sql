-- CampusGig database schema (MySQL 8.0+)
--
-- Safe to run more than once: it only creates what is missing and never drops data.
--   mysql -u root -p < src/main/resources/schema.sql
-- To start completely fresh, run  DROP DATABASE gig_marketplace;  yourself first.

CREATE DATABASE IF NOT EXISTS gig_marketplace
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE gig_marketplace;

-- Everyone who can log in. 'role' decides what the account may do:
--   student = applies to gigs, poster = creates gigs and hires students.
CREATE TABLE IF NOT EXISTS users (
    user_id       INT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(100)  NOT NULL,
    email         VARCHAR(190)  NOT NULL,
    password_hash VARCHAR(100)  NOT NULL,              -- BCrypt hash, never the password
    role          ENUM('student', 'poster') NOT NULL,
    created_at    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_users_email UNIQUE (email)
) ENGINE = InnoDB;

-- A micro-task. Life cycle: OPEN -> IN_PROGRESS (someone hired) -> COMPLETED (poster confirms).
CREATE TABLE IF NOT EXISTS gigs (
    gig_id               INT AUTO_INCREMENT PRIMARY KEY,
    poster_id            INT            NOT NULL,
    title                VARCHAR(200)   NOT NULL,
    description          TEXT           NOT NULL,
    budget               DECIMAL(10, 2) NOT NULL,
    category             VARCHAR(50)    NOT NULL,
    application_deadline DATE           NULL,          -- NULL = flexible
    completion_deadline  DATE           NULL,          -- NULL = flexible
    status               ENUM('OPEN', 'IN_PROGRESS', 'COMPLETED') NOT NULL DEFAULT 'OPEN',
    created_at           TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at         TIMESTAMP      NULL DEFAULT NULL,
    CONSTRAINT fk_gigs_poster FOREIGN KEY (poster_id) REFERENCES users (user_id),
    CONSTRAINT chk_gigs_budget CHECK (budget > 0),
    INDEX idx_gigs_status_created (status, created_at),
    INDEX idx_gigs_poster (poster_id)
) ENGINE = InnoDB;

-- A student's application to a gig. A student can apply to a gig only once.
CREATE TABLE IF NOT EXISTS applications (
    application_id INT AUTO_INCREMENT PRIMARY KEY,
    gig_id         INT          NOT NULL,
    applicant_id   INT          NOT NULL,
    pitch_text     TEXT         NOT NULL,
    portfolio_file VARCHAR(100) NULL,                  -- random stored file name (not the user's)
    portfolio_name VARCHAR(255) NULL,                  -- original file name, for downloads
    status         ENUM('PENDING', 'HIRED', 'REJECTED') NOT NULL DEFAULT 'PENDING',
    applied_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_apps_gig FOREIGN KEY (gig_id) REFERENCES gigs (gig_id) ON DELETE CASCADE,
    CONSTRAINT fk_apps_applicant FOREIGN KEY (applicant_id) REFERENCES users (user_id),
    CONSTRAINT uq_apps_gig_applicant UNIQUE (gig_id, applicant_id),
    INDEX idx_apps_applicant (applicant_id)
) ENGINE = InnoDB;

-- The poster's rating of the hired student, one per completed gig.
CREATE TABLE IF NOT EXISTS reviews (
    review_id   INT AUTO_INCREMENT PRIMARY KEY,
    gig_id      INT          NOT NULL,
    reviewer_id INT          NOT NULL,                 -- the poster
    reviewee_id INT          NOT NULL,                 -- the hired student
    rating      TINYINT      NOT NULL,
    comment     VARCHAR(500) NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_reviews_gig FOREIGN KEY (gig_id) REFERENCES gigs (gig_id) ON DELETE CASCADE,
    CONSTRAINT fk_reviews_reviewer FOREIGN KEY (reviewer_id) REFERENCES users (user_id),
    CONSTRAINT fk_reviews_reviewee FOREIGN KEY (reviewee_id) REFERENCES users (user_id),
    CONSTRAINT uq_reviews_gig UNIQUE (gig_id),
    CONSTRAINT chk_reviews_rating CHECK (rating BETWEEN 1 AND 5),
    INDEX idx_reviews_reviewee (reviewee_id)
) ENGINE = InnoDB;
