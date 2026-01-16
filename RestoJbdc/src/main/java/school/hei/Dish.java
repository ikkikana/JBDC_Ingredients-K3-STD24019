package school.hei;

import java.util.ArrayList;
import java.util.List;

public class Dish {
    private Integer id;
    private String name;
    private DishTypeEnum dishType;
    private List<Ingredient> ingredients;

    public Dish() { this.ingredients = new ArrayList<>(); }

    public Dish(Integer id, String name, DishTypeEnum dishType, List<Ingredient> ingredients) {
        this.id = id;
        this.name = name;
        this.dishType = dishType;
        setIngredients(ingredients);
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public DishTypeEnum getDishType() { return dishType; }
    public void setDishType(DishTypeEnum dishType) { this.dishType = dishType; }

    public List<Ingredient> getIngredients() { return ingredients; }

    public void setIngredients(List<Ingredient> ingredients) {
        if (ingredients == null) this.ingredients = new ArrayList<>();
        else {
            for (Ingredient i : ingredients) i.setDish(this);
            this.ingredients = ingredients;
        }
    }

    public double getDishCost() {
        double total = 0;
        for (Ingredient i : ingredients) {
            if (i.getRequiredQuantity() == null) throw new RuntimeException(
                    "Quantité requise inconnue pour l'ingrédient " + i.getName());
            total += i.getPrice() * i.getRequiredQuantity();
        }
        return total;
    }
}
