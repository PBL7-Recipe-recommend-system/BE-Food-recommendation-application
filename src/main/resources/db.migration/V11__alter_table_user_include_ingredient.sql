ALTER TABLE user_include_ingredient
    ADD CONSTRAINT uc_user_ingredient UNIQUE (user_id, ingredient_id);

ALTER TABLE user_exclude_ingredient
    ADD CONSTRAINT uc_user_ingredient UNIQUE (user_id, ingredient_id);