package simulator.model;

import java.util.*;

import java.util.function.Predicate;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.misc.Vector2D;

public class RegionManager implements AnimalMapView {

	private int map_width;
	private int map_height;
	private int cols;
	private int rows;
	private int region_width;
	private int region_height;
	private Region[][] _regions;
	private Map<Animal, Region> _animal_region;
	private int currentCol, currentRow;
	private Iterator<RegionData> iterator;
	public RegionManager() {
		this.cols = 0;
		this.rows = 0;
		this.map_height = 0;
		this.map_width = 0;
		this.region_height = 0;
		this.region_width = 0;
		this._regions = new Region[rows][cols];
		this._animal_region = new HashMap<>();
		this.currentRow = 0;
		this.currentCol = 0;
	}
	public RegionManager(int cols, int rows, int width, int height) {
		this.cols = cols;
		this.rows = rows;
		this.map_width = width;
		this.map_height = height;
		this.region_height = height / rows + (height % rows != 0 ? 1 : 0);
		this.region_width = width / cols + (width % cols != 0 ? 1 : 0);
		this._regions = new Region[rows][cols];
		this.ini_regions();
		this._animal_region = new HashMap<>();
		this.currentCol = 0;
		this.currentRow = 0;
	}

	private void ini_regions() {
		for (int i = 0; i < cols; i++) {
			for (int j = 0; j < rows; j++) {
				this._regions[j][i] = new DefaultRegion();
			}
		}
	}

	@Override
	public int get_cols() {
		return this.cols;
	}

	@Override
	public int get_rows() {
		return this.rows;
	}

	@Override
	public int get_width() {
		return this.map_width;
	}

	@Override
	public int get_height() {
		return this.map_height;
	}

	@Override
	public int get_region_width() {
		return this.region_width;
	}

	@Override
	public int get_region_height() {
		return this.region_height;
	}

	void set_region(int row, int col, Region r) {
		r.getAnimals().addAll(this._regions[row][col].getAnimals());
		this._regions[row][col] = r;

		for (Animal a : this._regions[row][col].getAnimals()) {
			this._animal_region.replace(a, r);
		}
	}

	private Vector2D get_animal_region(Animal a) {
		Vector2D pos = a.get_position();

		double xaux = pos.getX() / (this.map_width / this.cols);
		double yaux = pos.getY() / (this.map_height / this.rows);

		int x = (int) xaux;
		int y = (int) yaux;

		return new Vector2D(x, y);
	}

	void register_animal(Animal a) {
		a.init(this);
		Vector2D aux = this.get_animal_region(a);

		int x = (int) aux.getX();
		int y = (int) aux.getY();

		this._regions[y][x].add_animal(a);
		this._animal_region.put(a, this._regions[y][x]);
	}

	void unregister_animal(Animal a) {
		_animal_region.get(a).remove_animal(a);
		;

		this._animal_region.remove(a);
	}

	void update_animal_region(Animal a) {

		Vector2D aux = this.get_animal_region(a);

		int x = (int) aux.getX();
		int y = (int) aux.getY();

		if (this._animal_region.get(a) != this._regions[y][x]) {
			this._animal_region.get(a).remove_animal(a);
			this._regions[y][x].add_animal(a);

			this._animal_region.replace(a, _regions[y][x]);
		}

	}

	void update_all_regions(double dt) {
		for (int i = 0; i < this.rows; i++) {
			for (int j = 0; j < this.cols; j++) {
				this._regions[i][j].update(dt);
			}
		}
	}

	public JSONObject as_JSON() {
		JSONObject o = new JSONObject();
		JSONArray regiones = new JSONArray();

		for (int i = 0; i < this.cols; i++) {
			for (int j = 0; j < this.rows; j++) {
				JSONObject json = new JSONObject();
				json.put("row", j);
				json.put("col", i);
				json.put("data", _regions[j][i].as_JSON());

				regiones.put(json);
			}
		}

		o.put("regions", regiones);

		return o;
	}

	@Override
	public double get_food(Animal a, double dt) {
		Vector2D aux = this.get_animal_region(a);

		int x = (int) aux.getX();
		int y = (int) aux.getY();

		return this._regions[y][x].get_food(a, dt);
	}

	@Override
	public List<Animal> get_animals_in_range(Animal a, Predicate<Animal> filter) {
		List<Animal> ret = new LinkedList<Animal>();

		Vector2D aux = a.get_position();
		int xmin = (int) ((aux.getX() - a.get_sight_range()) * cols / map_width);
		int ymin = (int) ((aux.getY() - a.get_sight_range()) * rows / map_height);
		int xmax = (int) ((aux.getX() + a.get_sight_range()) * cols / map_width);
		int ymax = (int) ((aux.getY() + a.get_sight_range()) * rows / map_height);

		if (xmin < 0)
			xmin = 0;
		if (ymin < 0)
			ymin = 0;
		if (xmax >= cols)
			xmax = cols - 1;
		if (ymax >= rows)
			ymax = rows - 1;

		for (int i = xmin; i < xmax; i++) {
			for (int j = ymin; j < ymax; j++) {
				List<Animal> animales = this._regions[j][i].getAnimals();
				for (Animal actualAnimal : animales) {
					if (filter.test(actualAnimal) && actualAnimal.get_position().minus(a.get_position())
							.magnitude() <= actualAnimal.get_sight_range()) {
						ret.add(actualAnimal);
					}
				}
			}
		}

		return ret;
	}

	/**
	 * Returns an iterator over elements of type {@code T}.
	 *
	 * @return an Iterator.
	 */
	@Override
	public Iterator<RegionData> iterator() {
		iterator = new Iterator<>() {
			@Override
			public boolean hasNext() {
				return currentRow < rows;
			}

			@Override
			public RegionData next() {
				RegionData currentRegionData;
				if (hasNext()) {
					currentRegionData = new RegionData(currentRow, currentCol, _regions[currentRow][currentCol]);
					currentCol++;
					if (currentCol >= cols) {
						currentCol = 0;
						currentRow++;
					}
				} else {
					currentCol = 0;
					currentRow = 0;
					currentRegionData = new RegionData(currentRow, currentCol, _regions[currentRow][currentCol]);
					currentCol++;
				}
				return currentRegionData;
			}
		};

		return iterator;
	}
}
