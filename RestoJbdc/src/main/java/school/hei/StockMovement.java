package school.hei;

import java.time.Instant;

public class StockMovement {

    private Integer id;
    private Double quantity;
    private Instant createdAt;
    private MovementTypeEnum type;

    public StockMovement(Integer i,Double q,Instant t,MovementTypeEnum ty){
        id=i; quantity=q; createdAt=t; type=ty;
    }

    public Double getQuantity(){return quantity;}
    public Instant getCreatedAt(){return createdAt;}
    public MovementTypeEnum getType(){return type;}
}
