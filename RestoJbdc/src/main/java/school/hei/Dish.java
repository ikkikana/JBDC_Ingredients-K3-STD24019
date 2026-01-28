package school.hei;

import java.util.ArrayList;
import java.util.List;

public class Dish {
    private Integer id;
    private String name;
    private DishTypeEnum dishType;
    private List<Ingredient> ingredients;
    private Double salePrice; // prix de vente optionnel pour marge brute

    public Dish() { this.ingredients = new ArrayList<>(); }

    public Dish(Integer id, String name, DishTypeEnum dishType, List<Ingredient> ingredients, Double salePrice) {
        this.id = id;
        this.name = name;
        this.dishType = dishType;
        this.salePrice = salePrice;
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
        else this.ingredients = ingredients;
    }

    public Double getSalePrice() { return salePrice; }
    public void setSalePrice(Double salePrice) { this.salePrice = salePrice; }

    // ------------- Méthodes ajoutées / mises à jour ----------------

    // 1️⃣ Calcul du coût du plat en fonction des quantités requises
    public double getDishCost() {
        double total = 0;
        for (Ingredient i : ingredients) {
            double qty = i.getRequiredQuantity() != null ? i.getRequiredQuantity() : 0.0;
            total += i.getPrice() * qty;
        }
        return total;
    }

    // 2️⃣ Calcul de la marge brute
    public double getCrossMargin() {
        if (salePrice == null)
            throw new RuntimeException("Le prix de vente est null pour le plat " + name);
        return salePrice - getDishCost();
    }
}
