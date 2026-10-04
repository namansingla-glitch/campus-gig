# CampusGig

A campus gig marketplace: students browse and apply for paid micro-tasks, posters hire, track and rate.
Java 11 · Servlets/JSP (Tomcat 9) · MySQL · Maven.

## Setup

1. **Create the database** (safe to re-run):
   ```
   mysql -u root -p < src/main/resources/schema.sql
   mysql -u root -p gig_marketplace < src/main/resources/seed.sql   # optional demo data
   ```
2. **Configure credentials** – never commit them. Either set environment variables:
   ```
   export GIG_DB_URL="jdbc:mysql://localhost:3306/gig_marketplace?useSSL=false&serverTimezone=UTC"
   export GIG_DB_USER=root
   export GIG_DB_PASSWORD=your-password
   export GIG_UPLOAD_DIR=$HOME/campusgig-uploads   # optional
   ```
   or copy `src/main/resources/db.properties.example` to `db.properties` (git-ignored).
3. **Run**:
   ```
   mvn clean package cargo:run
   ```
   Open http://localhost:8080/gig-marketplace/

## Demo accounts (only if you loaded `seed.sql`)

Password for all: `password123`

| Role    | Email                |
|---------|----------------------|
| Student | naman@campus.edu, shreyash@campus.edu, shivam@campus.edu |
| Poster  | riddhi@campus.edu, mohak@campus.edu |

## Features

- Registration with BCrypt-hashed passwords and a student/poster role
- Login throttling, session fixation protection, CSRF tokens on every form
- Gig lifecycle: Open → In progress (hire) → Completed, with reject and rating
- Applications with PDF/DOC/DOCX portfolio upload (5 MB, content-checked, stored outside the web root)
- Dashboards, profile, rewards tiers and leaderboard computed from real data

## Security note

The original repository exposed a database password. **Change that password** on any server where it was used.
