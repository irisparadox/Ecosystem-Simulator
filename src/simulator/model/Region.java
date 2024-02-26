package simulator.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public abstract class Region implements Entity, FoodSupplier, RegionInfo {
    protected static final double FOOD_MULTIPLIER = 60.0;
    protected static final double ANIMAL_COUNT_FACTOR = 5.0;
    protected static final double EXP_MULTIPLIER = 2.0;
    protected List<Animal> _animals_in_region;
    public Region() {
        _animals_in_region = new ArrayList<>();
    }

    final void add_animal(Animal a){
        _animals_in_region.add(a);
    }

    final void remove_animal(Animal a){
        _animals_in_region.remove(a);
    }

    public JSONObject as_JSON(){
        JSONObject o = new JSONObject();
        JSONArray arr = new JSONArray();
        for(Animal a : _animals_in_region){
            arr.put(a.as_JSON());
        }
        o.put("animals",arr);

        return o;
    }

    @Override
    public void update(double dt) {
        for(Animal a : _animals_in_region){
            a.update(dt);
        }
    }

    protected int get_herbivore_animals(){
        int n = 0;
        for(Animal a : _animals_in_region){
            if(a.get_diet() == Diet.HERBIVORE)
                n++;
        }

        return n;
    }

    @Override
    public double get_food(Animal a, double dt) {
        return 0;
    }
}
