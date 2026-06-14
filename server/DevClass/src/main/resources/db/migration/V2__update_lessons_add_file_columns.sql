ALTER TABLE lessons
    ADD COLUMN original_file_name VARCHAR(255),
    ADD COLUMN file_size BIGINT;
