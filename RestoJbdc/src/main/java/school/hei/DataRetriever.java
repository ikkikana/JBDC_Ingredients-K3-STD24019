package school.hei;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {

    // ---------------- FIND METHODS ----------------

    public Dish findDishById(Integer id) {
        String dishSql = "SELECT id, name, dish_type, sale_price FROM dish WHERE id=?";
        String ingSql = """
            SELECT i.id, i.name, i.price, i.category, di.required_quantity
            FROM ingredient i
            JOIN dish_ingredient di ON di.ingredient_id = i.id
            WHERE di.dish_id = ?
        """;

        try (Connection c = new DBConnection().getConnection();
             PreparedStatement psDish = c.prepareStatement(dishSql);
             PreparedStatement psIng = c.prepareStatement(ingSql)) {

            // Plat
            psDish.setInt(1, id);
            ResultSet rsDish = psDish.executeQuery();
            if (!rsDish.next()) throw new RuntimeException("Dish not found " + id);

            Dish dish = new Dish();
            dish.setId(rsDish.getInt("id"));
            dish.setName(rsDish.getString("name"));
            dish.setDishType(DishTypeEnum.valueOf(rsDish.getString("dish_type")));
            dish.setSalePrice(rsDish.getObject("sale_price") != null ? rsDish.getDouble("sale_price") : null);

            // Ingrédients
            psIng.setInt(1, id);
            ResultSet rsIng = psIng.executeQuery();
            List<Ingredient> ingredients = new ArrayList<>();
            while (rsIng.next()) {
                Ingredient ing = new Ingredient(
                        rsIng.getInt("id"),
                        rsIng.getString("name"),
                        CategoryEnum.valueOf(rsIng.getString("category")),
                        rsIng.getDouble("price"),
                        rsIng.getObject("required_quantity") != null ? rsIng.getDouble("required_quantity") : 0.0
                );
                ingredients.add(ing);
            }
            dish.setIngredients(ingredients);
            return dish;

        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public List<Ingredient> findIngredients(int page, int size) {
        String sql = "SELECT id, name, price, category, required_quantity FROM ingredient ORDER BY id LIMIT ? OFFSET ?";
        List<Ingredient> list = new ArrayList<>();
        try (Connection c = new DBConnection().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, size);
            ps.setInt(2, (page - 1) * size);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Ingredient(
                        rs.getInt("id"),
                        rs.getString("name"),
                        CategoryEnum.valueOf(rs.getString("category")),
                        rs.getDouble("price"),
                        rs.getObject("required_quantity") != null ? rs.getDouble("required_quantity") : 0.0
                ));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    public List<Dish> findDishesByIngredientName(String ingredientName) {
        List<Dish> dishes = new ArrayList<>();
        String sql = """
            SELECT DISTINCT d.id
            FROM dish d
            JOIN dish_ingredient di ON di.dish_id = d.id
            JOIN ingredient i ON i.id = di.ingredient_id
            WHERE i.name ILIKE ?
        """;

        try (Connection c = new DBConnection().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, "%" + ingredientName + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                dishes.add(findDishById(rs.getInt("id")));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return dishes;
    }

    public List<Ingredient> findIngredientsByCriteria(String ingredientName, CategoryEnum category, String dishName, int page, int size) {
        List<Ingredient> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
            SELECT i.id, i.name, i.price, i.category, di.required_quantity
            FROM ingredient i
            LEFT JOIN dish_ingredient di ON di.ingredient_id = i.id
            LEFT JOIN dish d ON d.id = di.dish_id
            WHERE 1=1
        """);

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
                list.add(new Ingredient(
                        rs.getInt("id"),
                        rs.getString("name"),
                        CategoryEnum.valueOf(rs.getString("category")),
                        rs.getDouble("price"),
                        rs.getObject("required_quantity") != null ? rs.getDouble("required_quantity") : 0.0
                ));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    // ---------------- SAVE METHODS ----------------

    public Dish saveDish(Dish dish) {
        String sql = "INSERT INTO dish (name, dish_type, sale_price) VALUES (?, ?, ?) RETURNING id";
        try (Connection c = new DBConnection().getConnection()) {
            c.setAutoCommit(false);
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, dish.getName());
                ps.setString(2, dish.getDishType().name());
                if (dish.getSalePrice() != null) ps.setDouble(3, dish.getSalePrice());
                else ps.setNull(3, Types.NUMERIC);

                ResultSet rs = ps.executeQuery();
                if (rs.next()) dish.setId(rs.getInt(1));

                if (dish.getIngredients() != null) {
                    for (Ingredient ing : dish.getIngredients()) {
                        if (ing.getId() == null) saveIngredient(ing, c);
                        saveDishIngredient(c, dish.getId(), ing.getId(), ing.getRequiredQuantity());
                    }
                }
                c.commit();
            } catch (Exception e) {
                c.rollback();
                throw e;
            } finally { c.setAutoCommit(true); }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return dish;
    }

    public Ingredient saveIngredient(Ingredient ingredient, Connection c) throws SQLException {
        String sql = "INSERT INTO ingredient (name, category, price, required_quantity) VALUES (?,?,?,?) RETURNING id";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, ingredient.getName());
            ps.setString(2, ingredient.getCategory().name());
            ps.setDouble(3, ingredient.getPrice());
            if (ingredient.getRequiredQuantity() != null) ps.setDouble(4, ingredient.getRequiredQuantity());
            else ps.setNull(4, Types.NUMERIC);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) ingredient.setId(rs.getInt(1));
        }
        return ingredient;
    }

    private void saveDishIngredient(Connection c, Integer dishId, Integer ingredientId, Double requiredQuantity) throws SQLException {
        String sql = "INSERT INTO dish_ingredient (dish_id, ingredient_id, required_quantity) VALUES (?,?,?)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, dishId);
            ps.setInt(2, ingredientId);
            if (requiredQuantity != null) ps.setDouble(3, requiredQuantity);
            else ps.setNull(3, Types.NUMERIC);
            ps.executeUpdate();
        }
    }
}
