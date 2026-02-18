package school.hei;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/restaurant",
                "postgres",
                "postgres"
        );
    }

    // ================= INGREDIENT =================

    public List<Ingredient> findIngredients(int limit, int offset){

        List<Ingredient> list = new ArrayList<>();

        String sql = """
        SELECT * FROM ingredient
        ORDER BY id
        LIMIT ? OFFSET ?
        """;

        try(Connection c = getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){

            ps.setInt(1,limit);
            ps.setInt(2,offset);

            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                Ingredient i = new Ingredient();
                i.setId(rs.getInt("id"));
                i.setName(rs.getString("name"));
                i.setUnitPrice(rs.getDouble("unit_price"));
                list.add(i);
            }

        }catch(Exception e){
            throw new RuntimeException(e);
        }

        return list;
    }

    // ================= DISH =================

    public Dish findDishById(int id){

        Dish dish = null;

        try(Connection c = getConnection()){

            PreparedStatement ps = c.prepareStatement("""
            SELECT * FROM dish WHERE id=?
            """);

            ps.setInt(1,id);
            ResultSet rs = ps.executeQuery();

            if(!rs.next()) throw new RuntimeException("Dish not found");

            dish = new Dish();
            dish.setId(id);
            dish.setName(rs.getString("name"));
            dish.setPrice(rs.getDouble("price"));

            PreparedStatement ps2 = c.prepareStatement("""
            SELECT i.*,di.required_quantity
            FROM dish_ingredient di
            JOIN ingredient i ON i.id=di.ingredient_id
            WHERE di.dish_id=?
            """);

            ps2.setInt(1,id);
            ResultSet rs2 = ps2.executeQuery();

            while(rs2.next()){
                Ingredient ing = new Ingredient();
                ing.setId(rs2.getInt("id"));
                ing.setName(rs2.getString("name"));
                ing.setUnitPrice(rs2.getDouble("unit_price"));

                DishIngredient di = new DishIngredient();
                di.setIngredient(ing);
                di.setRequiredQuantity(rs2.getDouble("required_quantity"));

                dish.getIngredients().add(di);
            }

        }catch(Exception e){
            throw new RuntimeException(e);
        }

        return dish;
    }

    public List<Dish> findDishesByIngredientName(String key){

        List<Dish> list = new ArrayList<>();

        String sql = """
        SELECT DISTINCT d.*
        FROM dish d
        JOIN dish_ingredient di ON d.id=di.dish_id
        JOIN ingredient i ON i.id=di.ingredient_id
        WHERE LOWER(i.name) LIKE ?
        """;

        try(Connection c = getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){

            ps.setString(1,"%"+key.toLowerCase()+"%");
            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                Dish d = new Dish();
                d.setId(rs.getInt("id"));
                d.setName(rs.getString("name"));
                d.setPrice(rs.getDouble("price"));
                list.add(d);
            }

        }catch(Exception e){
            throw new RuntimeException(e);
        }

        return list;
    }

    // ================= STOCK =================

    public Stock getStockValueAt(Ingredient ing, Instant at){

        String sql="""
        SELECT quantity,type FROM stock_movement
        WHERE ingredient_id=?
        AND movement_time<=?
        """;

        double total=0;

        try(Connection c=getConnection();
            PreparedStatement ps=c.prepareStatement(sql)){

            ps.setInt(1,ing.getId());
            ps.setTimestamp(2,Timestamp.from(at));

            ResultSet rs=ps.executeQuery();

            while(rs.next()){
                double q=rs.getDouble("quantity");
                String t=rs.getString("type");

                if(t.equals("IN")) total+=q;
                else total-=q;
            }

        }catch(Exception e){
            throw new RuntimeException(e);
        }

        return new Stock(total);
    }

    // ================= ORDER =================

    public void saveOrder(Order o){

        if(o.getStatus()==OrderStatusEnum.DELIVERED)
            throw new RuntimeException("Already delivered");

        String sql="""
        INSERT INTO orders(reference,type,status)
        VALUES(?,?,?)
        """;

        try(Connection c=getConnection();
            PreparedStatement ps=c.prepareStatement(sql)){

            ps.setString(1,o.getReference());
            ps.setObject(2,o.getType(),Types.OTHER);
            ps.setObject(3,o.getStatus(),Types.OTHER);

            ps.executeUpdate();

        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }
    public Order findOrderByReference(String reference){

        try(Connection c = new DBConnection().getConnection()){

            PreparedStatement ps = c.prepareStatement("""
            SELECT * FROM orders WHERE reference=?
        """);

            ps.setString(1, reference);

            ResultSet rs = ps.executeQuery();

            if(!rs.next())
                throw new RuntimeException("Order not found");

            Order o = new Order();
            o.setId(rs.getInt("id"));
            o.setReference(reference);
            o.setStatus(OrderStatusEnum.valueOf(rs.getString("status")));

            return o;

        } catch(Exception e){
            throw new RuntimeException(e);
        }
    }

}
