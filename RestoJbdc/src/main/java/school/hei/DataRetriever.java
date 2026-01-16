package school.hei;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {

    public Dish findDishById(Integer id) {
        String dishSql = "SELECT id, name, dish_type FROM dish WHERE id=?";
        String ingSql = "SELECT id, name, price, category, required_quantity FROM ingredient WHERE id_dish=?";
        try (Connection c = new DBConnection().getConnection();
             PreparedStatement psDish = c.prepareStatement(dishSql);
             PreparedStatement psIng = c.prepareStatement(ingSql)) {

            psDish.setInt(1, id);
            ResultSet rsDish = psDish.executeQuery();
            if (!rsDish.next()) throw new RuntimeException("Dish not found " + id);

            Dish dish = new Dish();
            dish.setId(rsDish.getInt("id"));
            dish.setName(rsDish.getString("name"));
            dish.setDishType(DishTypeEnum.valueOf(rsDish.getString("dish_type")));

            psIng.setInt(1, id);
            ResultSet rsIng = psIng.executeQuery();
            List<Ingredient> ingredients = new ArrayList<>();
            while (rsIng.next()) {
                ingredients.add(new Ingredient(
                        rsIng.getInt("id"),
                        rsIng.getString("name"),
                        CategoryEnum.valueOf(rsIng.getString("category")),
                        rsIng.getDouble("price"),
                        rsIng.getObject("required_quantity") != null ? rsIng.getDouble("required_quantity") : null,
                        dish
                ));
            }
            dish.setIngredients(ingredients);
            return dish;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Ingredient> findIngredients(int page, int size) {
        String sql = "SELECT id, name, price, category, required_quantity, id_dish FROM ingredient ORDER BY id LIMIT ? OFFSET ?";
        List<Ingredient> list = new ArrayList<>();
        try (Connection c = new DBConnection().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, size);
            ps.setInt(2, (page - 1) * size);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Dish dish = null;
                int dishId = rs.getInt("id_dish");
                if (!rs.wasNull()) dish = findDishById(dishId);

                list.add(new Ingredient(
                        rs.getInt("id"),
                        rs.getString("name"),
                        CategoryEnum.valueOf(rs.getString("category")),
                        rs.getDouble("price"),
                        rs.getObject("required_quantity") != null ? rs.getDouble("required_quantity") : null,
                        dish
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public List<Ingredient> createIngredients(List<Ingredient> newIngredients) {
        if (newIngredients == null || newIngredients.isEmpty()) return List.of();
        List<Ingredient> saved = new ArrayList<>();
        try (Connection c = new DBConnection().getConnection()) {
            c.setAutoCommit(false);
            try (PreparedStatement check = c.prepareStatement(
                    "SELECT COUNT(*) FROM ingredient WHERE name=? AND (id_dish=? OR (? IS NULL AND id_dish IS NULL))");
                 PreparedStatement insert = c.prepareStatement(
                         "INSERT INTO ingredient (name,category,price,required_quantity,id_dish) VALUES (?,?::ingredient_category,?,?,?) RETURNING id")) {

                for (Ingredient ing : newIngredients) {
                    check.setString(1, ing.getName());
                    if (ing.getDish() != null) {
                        check.setInt(2, ing.getDish().getId());
                        check.setInt(3, ing.getDish().getId());
                    } else {
                        check.setNull(2, Types.INTEGER);
                        check.setNull(3, Types.INTEGER);
                    }
                    ResultSet rsc = check.executeQuery();
                    rsc.next();
                    if (rsc.getInt(1) > 0) throw new RuntimeException("L'ingrédient " + ing.getName() + " existe déjà !");

                    insert.setString(1, ing.getName());
                    insert.setString(2, ing.getCategory().name());
                    insert.setDouble(3, ing.getPrice());
                    if (ing.getRequiredQuantity() != null) insert.setDouble(4, ing.getRequiredQuantity());
                    else insert.setNull(4, Types.NUMERIC);
                    if (ing.getDish() != null) insert.setInt(5, ing.getDish().getId());
                    else insert.setNull(5, Types.INTEGER);

                    ResultSet rsInsert = insert.executeQuery();
                    rsInsert.next();
                    ing.setId(rsInsert.getInt(1));
                    saved.add(ing);
                }
                c.commit();
            } catch (Exception e) {
                c.rollback();
                throw e;
            } finally {
                c.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return saved;
    }

    public List<Dish> findDishesByIngredientName(String ingredientName) {
        List<Dish> dishes = new ArrayList<>();
        String sql = """
                SELECT DISTINCT d.id, d.name, d.dish_type
                FROM dish d
                JOIN ingredient i ON i.id_dish = d.id
                WHERE i.name ILIKE ?
                """;
        try (Connection c = new DBConnection().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, "%" + ingredientName + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Dish dish = new Dish();
                dish.setId(rs.getInt("id"));
                dish.setName(rs.getString("name"));
                dish.setDishType(DishTypeEnum.valueOf(rs.getString("dish_type")));
                dish.setIngredients(findDishById(dish.getId()).getIngredients());
                dishes.add(dish);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return dishes;
    }

    public List<Ingredient> findIngredientsByCriteria(String ingredientName, CategoryEnum category, String dishName, int page, int size) {
        List<Ingredient> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT i.id,i.name,i.price,i.category,i.required_quantity,i.id_dish FROM ingredient i JOIN dish d ON i.id_dish=d.id WHERE 1=1");
        if (ingredientName != null) sql.append(" AND i.name ILIKE ?");
        if (category != null) sql.append(" AND i.category=?::ingredient_category");
        if (dishName != null) sql.append(" AND d.name ILIKE ?");
        sql.append(" ORDER BY i.id LIMIT ? OFFSET ?");

        try (Connection c = new DBConnection().getConnection();
             PreparedStatement ps = c.prepareStatement(sql.toString())) {

            int idx = 1;
            if (ingredientName != null) ps.setString(idx++, "%" + ingredientName + "%");
            if (category != null) ps.setString(idx++, category.name());
            if (dishName != null) ps.setString(idx++, "%" + dishName + "%");
            ps.setInt(idx++, size);
            ps.setInt(idx, (page - 1) * size);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Dish dish = null;
                int dishId = rs.getInt("id_dish");
                if (!rs.wasNull()) dish = findDishById(dishId);
                list.add(new Ingredient(
                        rs.getInt("id"),
                        rs.getString("name"),
                        CategoryEnum.valueOf(rs.getString("category")),
                        rs.getDouble("price"),
                        rs.getObject("required_quantity") != null ? rs.getDouble("required_quantity") : null,
                        dish
                ));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }
}
