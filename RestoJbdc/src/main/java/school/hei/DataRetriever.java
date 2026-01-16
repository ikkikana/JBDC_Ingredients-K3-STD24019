package school.hei;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {

    public Dish findDishById(Integer id) {
        String dishSql = "SELECT id, name, dish_type FROM dish WHERE id = ?";
        String ingSql  = "SELECT id, name, price, category FROM ingredient WHERE id_dish = ?";

        try (Connection conn = new DBConnection().getConnection();
             PreparedStatement psDish = conn.prepareStatement(dishSql);
             PreparedStatement psIng = conn.prepareStatement(ingSql)) {

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
                Ingredient ing = new Ingredient(
                        rsIng.getInt("id"),
                        rsIng.getString("name"),
                        CategoryEnum.valueOf(rsIng.getString("category")),
                        rsIng.getDouble("price"),
                        dish
                );
                ingredients.add(ing);
            }
            dish.setIngredients(ingredients);
            return dish;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Ingredient> findIngredients(int page, int size) {
        List<Ingredient> list = new ArrayList<>();
        String sql = "SELECT id, name, price, category, id_dish FROM ingredient ORDER BY id LIMIT ? OFFSET ?";

        try (Connection conn = new DBConnection().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, size);
            ps.setInt(2, (page - 1) * size);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Dish dish = null;
                int dishId = rs.getInt("id_dish");
                if (!rs.wasNull()) dish = findDishById(dishId);

                Ingredient ing = new Ingredient(
                        rs.getInt("id"),
                        rs.getString("name"),
                        CategoryEnum.valueOf(rs.getString("category")),
                        rs.getDouble("price"),
                        dish
                );
                list.add(ing);
            }
        } catch (SQLException e) { throw new RuntimeException(e); }

        return list;
    }

    public List<Ingredient> createIngredients(List<Ingredient> newIngredients) {
        if (newIngredients == null || newIngredients.isEmpty()) return List.of();

        List<Ingredient> saved = new ArrayList<>();
        DBConnection dbc = new DBConnection();
        try (Connection conn = dbc.getConnection()) {
            conn.setAutoCommit(false);

            String checkSql = "SELECT COUNT(*) FROM ingredient WHERE name = ? AND (id_dish = ? OR (? IS NULL AND id_dish IS NULL))";
            String insertSql = "INSERT INTO ingredient (name, category, price, id_dish) VALUES (?, ?::ingredient_category, ?, ?) RETURNING id";

            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql);
                 PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {

                for (Ingredient ing : newIngredients) {
                    checkStmt.setString(1, ing.getName());
                    if (ing.getDish() != null) {
                        checkStmt.setInt(2, ing.getDish().getId());
                        checkStmt.setInt(3, ing.getDish().getId());
                    } else {
                        checkStmt.setNull(2, Types.INTEGER);
                        checkStmt.setNull(3, Types.INTEGER);
                    }
                    ResultSet rsCheck = checkStmt.executeQuery();
                    rsCheck.next();
                    if (rsCheck.getInt(1) > 0)
                        throw new RuntimeException("L'ingrédient '" + ing.getName() + "' existe déjà !");

                    insertStmt.setString(1, ing.getName());
                    insertStmt.setString(2, ing.getCategory().name());
                    insertStmt.setDouble(3, ing.getPrice());
                    if (ing.getDish() != null) insertStmt.setInt(4, ing.getDish().getId());
                    else insertStmt.setNull(4, Types.INTEGER);

                    ResultSet rsInsert = insertStmt.executeQuery();
                    rsInsert.next();
                    ing.setId(rsInsert.getInt(1));
                    saved.add(ing);
                }

                conn.commit();
            } catch (SQLException | RuntimeException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) { throw new RuntimeException(e); }

        return saved;
    }
}
