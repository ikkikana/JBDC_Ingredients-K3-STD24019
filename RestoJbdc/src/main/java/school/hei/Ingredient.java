package school.hei;

public class Ingredient {
    private Integer id;
    private String name;
    private Double price;
    private CategoryEnum category;
    private Dish dish;

    public Ingredient() {}

    public Ingredient(Integer id, String name, CategoryEnum category, Double price) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
    }

    public Ingredient(Integer id, String name, CategoryEnum category, Double price, Dish dish) {
        this(id, name, category, price);
        this.dish = dish;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public CategoryEnum getCategory() { return category; }
    public void setCategory(CategoryEnum category) { this.category = category; }

    public Dish getDish() { return dish; }
    public void setDish(Dish dish) { this.dish = dish; }

    public String getDishName() {
        return dish != null ? dish.getName() : null;
    }
}
