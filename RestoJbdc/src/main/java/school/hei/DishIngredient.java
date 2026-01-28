package school.hei;

public class DishIngredient {
    private Integer id;
    private Dish dish;
    private Ingredient ingredient;
    private Double requiredQuantity;

    public DishIngredient() {}

    public DishIngredient(Dish dish, Ingredient ingredient, Double requiredQuantity) {
        this.dish = dish;
        this.ingredient = ingredient;
        this.requiredQuantity = requiredQuantity;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Dish getDish() { return dish; }
    public void setDish(Dish dish) { this.dish = dish; }

    public Ingredient getIngredient() { return ingredient; }
    public void setIngredient(Ingredient ingredient) { this.ingredient = ingredient; }

    public Double getRequiredQuantity() { return requiredQuantity; }
    public void setRequiredQuantity(Double requiredQuantity) { this.requiredQuantity = requiredQuantity; }
}
