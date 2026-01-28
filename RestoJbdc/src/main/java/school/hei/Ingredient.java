package school.hei;

public class Ingredient {
    private Integer id;
    private String name;
    private Double price;
    private CategoryEnum category;
    private Double requiredQuantity; // quantité spécifique pour un plat

    public Ingredient() {}

    public Ingredient(Integer id, String name, CategoryEnum category, Double price, Double requiredQuantity) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.requiredQuantity = requiredQuantity;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public CategoryEnum getCategory() { return category; }
    public void setCategory(CategoryEnum category) { this.category = category; }

    public Double getRequiredQuantity() { return requiredQuantity; }
    public void setRequiredQuantity(Double requiredQuantity) { this.requiredQuantity = requiredQuantity; }
}
