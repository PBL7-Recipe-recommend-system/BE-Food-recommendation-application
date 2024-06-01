ALTER TABLE meal_plans
    RENAME COLUMN daily_calorie TO daily_calories;

ALTER TABLE meal_plans
    RENAME COLUMN total_calorie TO total_calories;

ALTER TABLE recommend_meal_plan
    RENAME COLUMN daily_calorie TO daily_calories;