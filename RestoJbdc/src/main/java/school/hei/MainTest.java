package school.hei;

import java.util.List;

public class MainTest {

    public static void main(String[] args) {
        DataRetriever dr = new DataRetriever();

        try { Dish d=dr.findDishById(1);
            System.out.println("Dish 1 : "+d.getName());
            for(Ingredient i:d.getIngredients()) System.out.println("- "+i.getName());
            System.out.println("DishCost : "+d.getDishCost());
        } catch(RuntimeException e){ System.out.println("Erreur a) "+e.getMessage()); }

        try { dr.findDishById(999); } catch(RuntimeException e){ System.out.println("b) Exception attendue : "+e.getMessage()); }

        List<Ingredient> page2 = dr.findIngredients(2,2);
        System.out.println("Page 2, size 2 :"); for(Ingredient i:page2) System.out.println("- "+i.getName());

        List<Ingredient> page3 = dr.findIngredients(3,5);
        System.out.println("Page 3, size 5 : "+page3.size());

        List<Dish> dishesWithEur = dr.findDishesByIngredientName("eur");
        System.out.println("Plats contenant 'eur' :"); for(Dish d:dishesWithEur) System.out.println("- "+d.getName());

        List<Ingredient> vegs = dr.findIngredientsByCriteria(null, CategoryEnum.VEGETABLE,null,1,10);
        System.out.println("Ingredients VEGETABLE :"); for(Ingredient i:vegs) System.out.println("- "+i.getName());

        List<Ingredient> gTest = dr.findIngredientsByCriteria("cho", null,"Sal",1,10);
        System.out.println("Test g) : "+gTest.size());

        List<Ingredient> hTest = dr.findIngredientsByCriteria("cho", null,"gâteau",1,10);
        System.out.println("Test h) :"); for(Ingredient i:hTest) System.out.println("- "+i.getName());

        try {
            List<Ingredient> created = dr.createIngredients(List.of(
                    new Ingredient(null,"Fromage",CategoryEnum.DAIRY,1200.0,1.0,null),
                    new Ingredient(null,"Oignon",CategoryEnum.VEGETABLE,500.0,2.0,null)
            ));
            System.out.println("Ingrédients créés :"); for(Ingredient i:created) System.out.println("- "+i.getName());
        } catch(RuntimeException e){ System.out.println("Erreur i) "+e.getMessage()); }
    }
}
