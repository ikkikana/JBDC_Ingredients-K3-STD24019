package school.hei;

import java.time.Instant;

public class Ingredient {

    private int id;
    private String name;
    private double unitPrice;

    private DataRetriever dr = new DataRetriever();

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }

    public Stock getStockValueAt(Instant instant){
        return dr.getStockValueAt(this, instant);
    }
}
