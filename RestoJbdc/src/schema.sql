CREATE TYPE ingredient_category AS ENUM (
    'VEGETABLE',
    'ANIMAL',
    'MARINE',
    'DAIRY',
    'OTHER'
);
CREATE TYPE dish_type AS ENUM (
'START',
'MAIN',
'DESSERT'
);
CREATE TABLE dish (
         id SERIAL PRIMARY KEY,
         name VARCHAR(100) NOT NULL,
          dish_type dish_type NOT NULL
);

CREATE TABLE Ingredient (
                            id SERIAL PRIMARY KEY,
                            name VARCHAR(100) not null,
                            price NUMERIC(10,2) not null CHECK (price >= 0),
                            category ingredient_category NOT null,
                            id_dish INT NULL,
                            CONSTRAINT fk_ingredient_dish FOREIGN KEY(id_dish) REFERENCES dish(id)
                                ON DELETE SET NULL
);
