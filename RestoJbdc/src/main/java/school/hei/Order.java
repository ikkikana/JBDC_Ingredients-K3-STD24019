package school.hei;

public class Order {

    private int id;
    private String reference;
    private OrderTypeEnum type;
    private OrderStatusEnum status;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public OrderTypeEnum getType() { return type; }
    public void setType(OrderTypeEnum type) { this.type = type; }

    public OrderStatusEnum getStatus() { return status; }
    public void setStatus(OrderStatusEnum status) { this.status = status; }
}
