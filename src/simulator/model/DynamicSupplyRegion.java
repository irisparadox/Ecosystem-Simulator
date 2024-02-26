package simulator.model;

import simulator.extra.ExceptionMessages;
import simulator.misc.Utils;

public class DynamicSupplyRegion extends Region {
    private final double _growth_factor;
    private double _food;
    public DynamicSupplyRegion(double init_food, double growth_factor) throws IllegalArgumentException{
        if(init_food < 0) throw new IllegalArgumentException(ExceptionMessages.INVALID_FOOD_ARGUMENT);
        else if(growth_factor < 0) throw new IllegalArgumentException(ExceptionMessages.INVALID_FOOD_GROWTH);
        _food = init_food;
        _growth_factor = growth_factor;
    }

    @Override
    public double get_food(Animal a, double dt) {
        double food;
        if(a.get_diet() == Diet.CARNIVORE)
            food = 0;
        else
            food = Math.min(_food,FOOD_MULTIPLIER * Math.exp(-Math.max(0,
                    get_herbivore_animals() - ANIMAL_COUNT_FACTOR) * EXP_MULTIPLIER) * dt);

        _food -= food;
        return food;
    }

    @Override
    public void update(double dt) {
        if(Utils._rand.nextDouble() < 0.5) _food = dt * _growth_factor;
    }
}
