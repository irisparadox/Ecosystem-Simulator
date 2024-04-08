package simulator.model;

import java.util.List;

import simulator.misc.Utils;

import simulator.misc.Vector2D;

public class Sheep extends Animal {

	// constantes para constructores
	private final static String GENETIC_CODE = "Sheep";
	private final static Diet DIET = Diet.HERBIVORE;
	private final static double INITIAL_SIGHT_RANGE = 40.0;
	private final static double INITIAl_SPEED = 35.0;

	// constantes para update
	private final static double DISTANCE_TO_CHANGE_DESTINATION = 8.0;
	private final static double UPDATE_MOVE_PARAM1 = 100.0;
	private final static double UPDATE_MOVE_PARAM2 = 0.007;
	private final static double UPDATE_ENERGY_MUL = 20.0;
	private final static double UPDATE_DESIRE_MUL = 40.0;
	private final static double MINIMUM_DESIRE = 65.0;
	private final static double UPDATE_MOVE_MUL = 2.0;
	private final static double UPDATE_ENERGY_MUL2 = 1.2;
	private final static double PROP_NEW_BABY = 0.9;
	private final static double AGE_MAX = 8.0;
	private final static double MIN = 0.0;
	private final static double MAX = 100.0;

	private Animal _danger_source;
	private SelectionStrategy _danger_strategy;

	public Sheep(SelectionStrategy mate_strategy, SelectionStrategy danger_strategy, Vector2D pos) {
		super(GENETIC_CODE, DIET, INITIAL_SIGHT_RANGE, INITIAl_SPEED, mate_strategy, pos);
		this._danger_strategy = danger_strategy;
		this._danger_source = null; // ?
	}

	protected Sheep(Sheep p1, Animal p2) {
		super(p1, p2);
		this._danger_strategy = p1._danger_strategy;
		this._danger_source = null;
	}

	@Override
	public void update(double dt) {
		// 1
		if (this._state == State.DEAD)
			return;

		// 2
		switch (this._state) {
		case DEAD:
			break;
		case NORMAL:
			this.updateNormal(dt);
			break;
		case DANGER:
			this.updateDanger(dt);
			break;
		case MATE:
			this.updateMate(dt);
			break;
		case HUNGER:
			break;
		}

		// 3.
		if (this._pos.getX() >= _region_mngr.get_width() || this._pos.getX() < 0
				|| this._pos.getY() >= this._region_mngr.get_height() || this._pos.getY() < 0) {
			this._pos = super.adjust_position();
			set_state_normal();
		}

		// 4.
		if (this._energy <= 0 || this._age > AGE_MAX) {
			this._state = State.DEAD;
		}

		// 5
		if (this._state != State.DEAD) {
			_energy += _region_mngr.get_food(this, dt);
			Utils.constrain_value_in_range(_energy, MIN, MAX);
		}
	}

	private void set_state_normal() {
		this._state = State.NORMAL;
		_danger_source = null;
		_mate_target = null;
	}

	private void set_state_mate() {
		this._state = State.MATE;
		_danger_source = null;
	}

	private void set_state_danger() {
		this._state = State.DANGER;
		_mate_target = null;
	}

	private void updateNormal(double dt) {
		// 1.
		// 1.1
		if (this._pos.distanceTo(this._dest) < DISTANCE_TO_CHANGE_DESTINATION) {
			double x = Utils._rand.nextDouble(this._region_mngr.get_width() - 1);
			double y = Utils._rand.nextDouble(this._region_mngr.get_height() - 1);
			this._dest = new Vector2D(x, y);
		}
		// 1.2
		super.move(this._speed * dt * Math.exp((this._energy - UPDATE_MOVE_PARAM1) * UPDATE_MOVE_PARAM2));
		// 1.3
		this._age += dt;
		// 1.4
		this._energy -= UPDATE_ENERGY_MUL * dt;
		this._energy = Utils.constrain_value_in_range(this._energy, MIN, MAX);
		// 1.5
		this._desire += UPDATE_DESIRE_MUL * dt;
		this._desire = Utils.constrain_value_in_range(this._desire, MIN, MAX);

		// 2.
		// 2.1
		if (this._danger_source == null) {
			List<Animal> carnivoros = _region_mngr.get_animals_in_range(this, a -> a._diet == Diet.CARNIVORE);
			this._danger_source = this._danger_strategy.select(this, carnivoros);

		}
		// 2.2
		if (this._danger_source != null) {
			set_state_danger();
		} else if (this._danger_source == null && this._desire > MINIMUM_DESIRE) {
			set_state_mate();
		}
	}

	private void updateDanger(double dt) {
		// 1.
		if (this._danger_source != null && this._danger_source._state == State.DEAD) {
			this._danger_source = null;
		}

		// 2.
		if (this._danger_source == null) {
			this.updateNormal(dt);
		} else {
			// 2.1
			this._dest = this._pos.plus(this._pos.minus(_danger_source._pos).direction());
			// 2.2
			super.move(UPDATE_MOVE_MUL * this._speed * dt
					* Math.exp((this._energy - UPDATE_MOVE_PARAM1) * UPDATE_MOVE_PARAM2));
			// 2.3
			this._age += dt;
			// 2.4
			this._energy -= UPDATE_ENERGY_MUL * UPDATE_ENERGY_MUL2 * dt;
			this._energy = Utils.constrain_value_in_range(this._energy, MIN, MAX);
			// 2.5
			this._desire += UPDATE_DESIRE_MUL * dt;
			this._desire = Utils.constrain_value_in_range(this._desire, MIN, MAX);
		}

		// 3.
		// 3.1
		if (this._danger_source == null || this._pos.distanceTo(this._danger_source._pos) > this._sight_range) {
			// 3.1.1
			List<Animal> carnivoros = _region_mngr.get_animals_in_range(this, a -> a._diet == Diet.CARNIVORE);
			this._danger_source = this._danger_strategy.select(this, carnivoros);

			// 3.2.1
			if (this._danger_source == null) {
				if (this._desire < MINIMUM_DESIRE) {
					set_state_normal();
				} else {
					set_state_mate();
				}
			}
		}
	}

	private void updateMate(double dt) {

		// 1.
		if (this._mate_target != null && this._state == State.DEAD
				|| this._mate_target != null && this._pos.distanceTo(this._mate_target._pos) < this._sight_range) {
			this._mate_target = null;
		}

		// 2.
		if (this._mate_target == null) {
			List<Animal> posiblesMate = _region_mngr.get_animals_in_range(this,
					a -> a._genetic_code.equals(this._genetic_code) && this != a);
			this._mate_target = this._mate_strategy.select(this, posiblesMate);
		}
		if (this._mate_target == null) {
			this.updateNormal(dt);
		} else {
			// 2.1
			this._dest = this._mate_target.get_position();
			// 2.2
			super.move(UPDATE_MOVE_MUL * this._speed * dt
					* Math.exp((this._energy - UPDATE_MOVE_PARAM1) * UPDATE_MOVE_PARAM2));
			// 2.3
			this._age += dt;
			// 2.4
			this._energy -= UPDATE_ENERGY_MUL * UPDATE_ENERGY_MUL2 * dt;
			this._energy = Utils.constrain_value_in_range(this._energy, MIN, MAX);
			// 2.5
			this._desire += UPDATE_DESIRE_MUL * dt;
			this._desire = Utils.constrain_value_in_range(this._desire, MIN, MAX);
			// 2.6
			if (this._pos.distanceTo(this._mate_target._pos) < DISTANCE_TO_CHANGE_DESTINATION) {
				// 2.6.1
				this._desire = 0;
				this._mate_target._desire = 0;
				// 2.6.2
				if (!super.is_pregnent() && Utils._rand.nextDouble() < PROP_NEW_BABY) {
					this._baby = new Sheep(this, _mate_target);
				}
				// 2.6.3
				this._mate_target = null;
			}
		}

		// 3.
		if (this._danger_source == null) {
			List<Animal> carnivoros = _region_mngr.get_animals_in_range(this, a -> a._diet == Diet.CARNIVORE);
			this._danger_source = this._danger_strategy.select(this, carnivoros);
		}

		// 4.
		if (this._danger_source != null) {
			set_state_danger();
		} else if (this._danger_source == null && this._desire < MINIMUM_DESIRE) {
			set_state_normal();
		}
	}
}
