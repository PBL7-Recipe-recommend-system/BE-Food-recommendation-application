DROP PROCEDURE IF EXISTS add_column_if_not_exists;
CREATE PROCEDURE add_column_if_not_exists()
BEGIN
    IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS
                   WHERE TABLE_NAME = 'user' AND COLUMN_NAME = 'meals')
    THEN
ALTER TABLE user
    ADD meals INT;
END IF;

    IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS
                   WHERE TABLE_NAME = 'user' AND COLUMN_NAME = 'avatar')
    THEN
ALTER TABLE user
    ADD avatar VARCHAR(255);
END IF;
END;

CALL add_column_if_not_exists();