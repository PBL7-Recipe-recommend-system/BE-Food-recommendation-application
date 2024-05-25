ALTER TABLE meal_plans
    RENAME COLUMN brunch_id TO morning_snack_id;

ALTER TABLE meal_plans
    RENAME COLUMN snack_id TO afternoon_snack_id;