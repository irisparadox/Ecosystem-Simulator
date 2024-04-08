package simulator.model;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.misc.Utils;
import simulator.misc.Vector2D;

public abstract class Animal implements Entity, AnimalInfo {

	private final static double ToleranceSpeed1stConstructor = 0.1;
	private final static double InitialEnergy1stConstructor = 100.0;
	private final static double FactorPos2ndConstructor = 60.0;
	private final static double ToleranceSightRange2ndConstructor = 0.2;
	private final static double ToleranceSpeed2ndConstructor = 0.2;

	protected String _genetic_code;
	protected Diet _diet;
	protected State _state;
	protected Vector2D _pos;
	protected Vector2D _dest;
	protected double _energy;
	protected double _speed;
	protected double _age;
	protected double _desire;
	protected double _sight_range;
	protected Animal _mate_target;
	protected Animal _baby;
	protected AnimalMapView _region_mngr;
	protected SelectionStrategy _mate_strategy;

	protected Animal(String genetic_code, Diet diet, double sight_range, double init_speed,
			SelectionStrategy mate_strategy, Vector2D pos) throws IllegalArgumentException {
		if (genetic_code.length() == 0)
			throw new IllegalArgumentException("Genetic code string vacio");
		if (sight_range < 0)
			throw new IllegalArgumentException("Sight range negativo");
		if (init_speed < 0)
			throw new IllegalArgumentException("Init speed negativo");
		if (mate_strategy == null)
			throw new IllegalArgumentException("Mate strategy es null");
		this._genetic_code = genetic_code;
		this._diet = diet;
		this._sight_range = sight_range;
		this._pos = pos;
		this._mate_strategy = mate_strategy;
		this._speed = Utils.get_randomized_parameter(init_speed, ToleranceSpeed1stConstructor);
		this._state = State.NORMAL;
		this._energy = InitialEnergy1stConstructor;
		this._desire = 0.0;
		this._dest = null;
		this._mate_target = null;
		this._baby = null;
		this._region_mngr = null;
		this._age = 0.0;
	}

	protected Animal(Animal p1, Animal p2) {
		this._dest = null;
		this._baby = null;
		this._mate_target = null;
		this._region_mngr = null;
		this._state = State.NORMAL;
		this._desire = 0.0;
		this._genetic_code = p1._genetic_code;
		this._diet = p1._diet;
		this._mate_strategy = p2._mate_strategy;
		this._energy = (p1._energy + p2._energy) / 2;
		this._pos = p1._pos.plus(
				Vector2D.get_random_vector(-1, 1).scale(FactorPos2ndConstructor * (Utils._rand.nextGaussian() + 1)));
		this._sight_range = Utils.get_randomized_parameter((p1._sight_range + p2._sight_range) / 2,
				ToleranceSightRange2ndConstructor);
		this._speed = Utils.get_randomized_parameter((p1._speed + p2._speed) / 2, ToleranceSpeed2ndConstructor);
		this._age = 0.0; // ?
	}

	void init(AnimalMapView reg_mngr) {
		this._region_mngr = reg_mngr;

		if (this._pos == null) {
			double x = Utils._rand.nextDouble(reg_mngr.get_width() - 1);
			double y = Utils._rand.nextDouble(reg_mngr.get_height() - 1);
			this._pos = new Vector2D(x, y);

		} else {
			this._pos = this.adjust_position();
		}

		double x = Utils._rand.nextDouble(reg_mngr.get_width() - 1);
		double y = Utils._rand.nextDouble(reg_mngr.get_height() - 1);
		this._dest = new Vector2D(x, y);
	}

	protected Vector2D adjust_position() {
		double width = _region_mngr.get_width();
		double height = _region_mngr.get_height();
		double x = this._pos.getX();
		double y = this._pos.getY();

		while (x >= width)
			x = (x - width);
		while (x < 0)
			x = (x + width);
		while (y >= height)
			y = (y - height);
		while (y < 0)
			y = (y + height);

		return new Vector2D(x, y);
	}

	Animal deliver_baby() {
		Animal baby = this._baby;
		this._baby = null;
		return baby;
	}

	protected void move(double speed) {
		_pos = _pos.plus(_dest.minus(_pos).direction().scale(speed));
	}

	public JSONObject as_JSON() {
		JSONObject o = new JSONObject();
		JSONArray a = new JSONArray();
		a.put(_pos.getX());
		a.put(_pos.getY());
		o.put("pos", a);
		o.put("gcod", this._genetic_code);
		o.put("diet", this._diet.toString());
		o.put("state", this._state.toString());
		return o;
	}

	/*
	 ** methods interface Entity
	 */

	/*
	 ** methods interface AnimalInfo
	 */
	@Override
	public State get_state() {
		return this._state;
	}

	@Override
	public Vector2D get_position() {
		return this._pos;
	}

	@Override
	public String get_genetic_code() {
		return this._genetic_code;
	}

	@Override
	public Diet get_diet() {
		return this._diet;
	}

	@Override
	public double get_speed() {
		return this._speed;
	}

	@Override
	public double get_sight_range() {
		return this._sight_range;
	}

	@Override
	public double get_energy() {
		return this._energy;
	}

	@Override
	public double get_age() {
		return this._age;
	}

	@Override
	public Vector2D get_destination() {
		return this._dest;
	}

	@Override
	public boolean is_pregnent() {
		boolean ret = false;
		if (this._baby != null)
			ret = true;
		return ret;
	}

}
