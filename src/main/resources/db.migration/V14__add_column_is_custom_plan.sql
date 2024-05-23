ALTER TABLE user
    ADD COLUMN is_custom_plan BOOLEAN DEFAULT FALSE;
UPDATE user
SET is_custom_plan = FALSE;