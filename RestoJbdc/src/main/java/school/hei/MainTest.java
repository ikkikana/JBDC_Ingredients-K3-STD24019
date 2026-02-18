package school.hei;


import java.time.Instant;

public class MainTest {

    public static void main(String[] args){

        DataRetriever dr=new DataRetriever();

        Dish d=dr.findDishById(1);
        System.out.println(d.getName());
        System.out.println("Cost="+d.getDishCost());

        Ingredient i=dr.findIngredients(1,1).get(0);
        System.out.println(i.getStockValueAt(Instant.parse("2024-01-06T12:00:00Z")).getQuantity());

        Order o=dr.findOrderByReference("REF001");
        o.setStatus(OrderStatusEnum.DELIVERED);
        dr.saveOrder(o);

        try{
            dr.saveOrder(o);
        }catch(Exception e){
            System.out.println("BLOCK DELIVERED OK");
        }
    }
}
