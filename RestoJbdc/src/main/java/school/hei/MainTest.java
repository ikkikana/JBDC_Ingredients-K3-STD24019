package school.hei;

import java.util.List;

public class MainTest {

    public static void main(String[] args) {
        DataRetriever dr = new DataRetriever();

        try {
            Dish d = dr.findDishById(1);
            System.out.println("Dish 1 : "+d.getName());
            for (Ingredient i:d.getIngredients()) System.out.println("- "+i.getName());
            System.out.println("DishCost : "+d.getDishCost());
            try {
                System.out.println("Marge brute : "+d.getCrossMargin());
            } catch(RuntimeException e){ System.out.println("Erreur marge : "+e.getMessage()); }
        } catch(RuntimeException e){ System.out.println("Erreur a) "+e.getMessage()); }

        try { dr.findDishById(999); } catch(RuntimeException e){ System.out.println("b) Exception attendue : "+e.getMessage()); }

        List<Ingredient> page2 = dr.findIngredients(2,2);
        System.out.println("Page 2, size 2 :"); for(Ingredient i:page2) System.out.println("- "+i.getName());

        List<Dish> dishesWithEur = dr.findDishesByIngredientName("eur");
        System.out.println("Plats contenant 'eur' :"); for(Dish d:dishesWithEur) System.out.println("- "+d.getName());
    }
}
