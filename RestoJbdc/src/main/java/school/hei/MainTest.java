package school.hei;

import java.util.List;

public class MainTest {

    public static void main(String[] args) {
        DataRetriever dr = new DataRetriever();

        // --- a) findDishById 1 ---
        try {
            Dish d = dr.findDishById(1);
            System.out.println("Dish 1 : " + d.getName());
            for (Ingredient i : d.getIngredients()) System.out.println("- " + i.getName());
        } catch (RuntimeException e) {
            System.out.println("Erreur a) " + e.getMessage());
        }

        // --- b) findDishById 999 ---
        try {
            dr.findDishById(999);
        } catch (RuntimeException e) {
            System.out.println("b) Exception attendue : " + e.getMessage());
        }

        // --- c) pagination page=2 size=2 ---
        List<Ingredient> page2 = dr.findIngredients(2, 2);
        System.out.println("Page 2, size 2 :");
        for (Ingredient i : page2) System.out.println("- " + i.getName());

        // --- d) pagination page=3 size=5 ---
        List<Ingredient> page3 = dr.findIngredients(3, 5);
        System.out.println("Page 3, size 5 : " + page3.size());

        // --- i) createIngredients Fromage et Oignon ---
        try {
            List<Ingredient> created = dr.createIngredients(List.of(
                    new Ingredient(null, "Fromage", CategoryEnum.DAIRY, 1200.0),
                    new Ingredient(null, "Oignon", CategoryEnum.VEGETABLE, 500.0)
            ));
            System.out.println("Ingrédients créés :");
            for (Ingredient i : created) System.out.println("- " + i.getName());
        } catch (RuntimeException e) {
            System.out.println("Erreur i) " + e.getMessage());
        }
    }
}
