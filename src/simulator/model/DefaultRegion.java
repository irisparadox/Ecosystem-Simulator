package simulator.model;

public class DefaultRegion extends Region {
    @Override
    public double get_food(Animal a, double dt) {
        double food;
        if(a.get_diet() == Diet.CARNIVORE)
            food = 0;
        else
            food = FOOD_MULTIPLIER * Math.exp(-Math.max(0,
                    get_herbivore_animals() - ANIMAL_COUNT_FACTOR) * EXP_MULTIPLIER ) * dt;

        return food;
    }

    @Override
    public void update(double dt){

    }
}
