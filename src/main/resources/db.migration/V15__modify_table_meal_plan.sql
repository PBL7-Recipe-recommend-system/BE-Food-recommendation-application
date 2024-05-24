ALTER TABLE meal_plans
    ADD meal_count INT;

ALTER TABLE meal_plans
    RENAME COLUMN snack1_id TO brunch_id;

ALTER TABLE meal_plans
    RENAME COLUMN snack2_id TO snack_id;