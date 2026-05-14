ALTER TABLE lessons
    ADD COLUMN description TEXT NULL AFTER title;

ALTER TABLE courses
    ADD COLUMN thumbnail_url VARCHAR(500) NULL;
