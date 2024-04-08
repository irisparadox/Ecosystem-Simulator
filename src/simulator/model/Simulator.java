package simulator.model;

import java.util.List;

import java.util.LinkedList;

import org.json.JSONObject;

import simulator.factories.Factory;

public class Simulator implements JSONable {

	private Factory<Animal> animals_factory;
	private Factory<Region> regions_factory;
	private RegionManager region_manager;
	private List<Animal> animal_list;
	private double time;

	public Simulator(int cols, int rows, int width, int height, Factory<Animal> animals_factory,
			Factory<Region> regions_factory) {
		this.region_manager = new RegionManager(cols, rows, width, height);
		this.animal_list = new LinkedList<>();
		this.animals_factory = animals_factory;
		this.regions_factory = regions_factory;
		this.time = 0.0;
	}

	private void set_region(int row, int col, Region r) {
		this.region_manager.set_region(row, col, r);
	}

	public void set_region(int row, int col, JSONObject r_json) {
		Region r = regions_factory.create_instance(r_json);
		this.set_region(row, col, r);
	}

	private void add_animal(Animal a) {
		this.animal_list.add(a);
		this.region_manager.register_animal(a);
	}

	public void add_animal(JSONObject a_json) {
		Animal a = animals_factory.create_instance(a_json);
		this.add_animal(a);
	}

	public MapInfo get_map_info() {
		return this.region_manager;
	}

	public List<? extends AnimalInfo> get_animals() {
		return this.animal_list;
	}

	public double get_time() {
		return this.time;
	}

	public void advance(double dt) {
		// 1
		this.time += dt;
		// 2
		List<Animal> animalsDead = new LinkedList<>();
		for (Animal a : this.animal_list) {
			if (a.get_state() == State.DEAD) {
				animalsDead.add(a);
				region_manager.unregister_animal(a);
			}
		}
		this.animal_list.removeAll(animalsDead);
		// 3
		for (Animal a : this.animal_list) {
			a.update(dt);
			this.region_manager.update_animal_region(a);
		}
		// 4.
		this.region_manager.update_all_regions(dt);
		// 5
		List<Animal> aux = new LinkedList<Animal>();
		for (Animal a : this.animal_list) {
			if (a.is_pregnent()) {
				aux.add(a.deliver_baby());
			}
		}
		for (Animal a : aux) {
			this.add_animal(a);
		}
	}

	public JSONObject as_JSON() {
		JSONObject o = new JSONObject();
		o.put("time", this.time);
		o.put("state", this.region_manager.as_JSON());

		return o;
	}
}
