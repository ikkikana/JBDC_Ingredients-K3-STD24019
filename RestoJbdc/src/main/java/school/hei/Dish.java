package school.hei;

import java.util.ArrayList;
import java.util.List;

public class Dish {

    private int id;
    private String name;
    private double price;

    private List<DishIngredient> ingredients = new ArrayList<>();

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public List<DishIngredient> getIngredients() {
        return ingredients;
    }

    public double getDishCost(){
        double total = 0;
        for(DishIngredient di : ingredients){
            total += di.getIngredient().getUnitPrice() * di.getRequiredQuantity();
        }
        return total;
    }

    public double getCrossMargin(){
        return price - getDishCost();
    }
}
