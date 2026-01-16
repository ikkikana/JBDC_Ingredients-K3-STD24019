
CREATE DATABASE mini_dish_db;

CREATE USER mini_dish_db_manager
WITH PASSWORD '1234';

GRANT CONNECT ON DATABASE mini_dish_db TO mini_dish_db_manager; // Permission de se connecter.

GRANT CREATE, USAGE ON SCHEMA public TO mini_dish_db_manager;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO mini_dish_db_manager; //pour voir les tables.
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO mini_dish_db_manager; //Auto_increment



//