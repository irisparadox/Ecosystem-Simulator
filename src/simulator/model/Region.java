package simulator.model;

import java.util.List;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public abstract class Region implements Entity, FoodSupplier, RegionInfo {

	protected List<Animal> animal_list;

	public Region() {
		this.animal_list = new ArrayList<>();
	}

	final void add_animal(Animal a) {
		this.animal_list.add(a);
	}

	final void remove_animal(Animal a) {
		this.animal_list.remove(a);
	}

	final List<Animal> getAnimals() {
		return this.animal_list;
	}

	public List<AnimalInfo> getAnimalsInfo() {
		return new ArrayList<>(animal_list);
	}

	public JSONObject as_JSON() {
		JSONObject o = new JSONObject();
		JSONArray a = new JSONArray();

		for (Animal animal : animal_list) {
			a.put(animal.as_JSON());
		}
		o.put("animals", a);
		return o;
	}
}
