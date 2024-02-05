package simulator.model;

import java.util.List;

public class SelectFirst implements SelectionStrategy {
    public SelectFirst(){}

    @Override
    public Animal select(Animal a, List<Animal> as) {
        Animal animal;
        if(as.size() == 0)
            animal = null;
        else
            animal = as.get(0);
        return animal;
    }
}
