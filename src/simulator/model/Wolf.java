package simulator.model;

import java.util.List;

import simulator.misc.Utils;
import simulator.misc.Vector2D;

public class Wolf extends Animal {

	private final static String GENETIC_CODE = "Wolf";
	private final static Diet DIET = Diet.CARNIVORE;
	private final static double INITIAL_SIGHT_RANGE = 50.0;
	private final static double INITIAL_SPEED = 60.0;
	private final static double AGE_MAX = 14.0;
	private final static double DISTANCE = 8.0;
	private static final double UPDATE_MOVE_PARAM1 = 100.0;
	private static final double UPDATE_MOVE_PARAM2 = 0.007;
	private static final double UPDATE_ENERGY_MUL = 18.0;
	private static final double UPDATE_DESIRE_MUL = 30.0;
	private static final double ENERGY_MIN = 50.0;
	private static final double DESIRE_MAX = 65.0;
	private static final double UPDATE_MOVE_MUL = 3.0;
	private static final double UPDATE_ENERGY_MUL2 = 1.2;
	private static final double ENERGY_TO_ADD = 50.0;
	private static final double PROP_NEW_BABY = 0.9;
	private static final double ENERGY_MINUS = 10.0;
	private static final double MIN = 0.0;
	private static final double MAX = 100.0;

	private Animal _hunt_target;
	private SelectionStrategy _hunting_strategy;

	public Wolf(SelectionStrategy mate_strategy, SelectionStrategy hunting_strategy, Vector2D pos) {
		super(GENETIC_CODE, DIET, INITIAL_SIGHT_RANGE, INITIAL_SPEED, mate_strategy, pos);
		this._hunting_strategy = hunting_strategy;
		this._hunt_target = null;
	}

	protected Wolf(Wolf p1, Animal p2) {
		super(p1, p2);
		this._hunt_target = null;
		this._hunting_strategy = p1._hunting_strategy;
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
		case HUNGER:
			this.updateHunger(dt);
			break;
		case MATE:
			this.updateMate(dt);
			break;
		case DANGER:
			break;
		default:
			break;
		}

		// 3.
		if (this._pos.getX() < 0 || this._pos.getX() > this._region_mngr.get_width() || this._pos.getY() < 0
				|| this._pos.getY() > this._region_mngr.get_height()) {
			super.adjust_position();
			set_state_normal();
		}

		// 4.
		if (this._energy <= 0.0 || this._age > AGE_MAX) {
			this._state = State.DEAD;
		}

		if (this._state != State.DEAD) {
			this._energy += this._region_mngr.get_food(this, dt);
			Utils.constrain_value_in_range(this._energy, MIN, MAX);
		}

	}

	private void set_state_normal() {
		this._state = State.NORMAL;
		_hunt_target = null;
		_mate_target = null;
	}

	private void set_state_mate() {
		this._state = State.MATE;
		_hunt_target = null;
	}

	private void set_state_hunger() {
		this._state = State.HUNGER;
		_mate_target = null;
	}

	private void updateNormal(double dt) {
		// 1.1
		if (this._pos.distanceTo(this._dest) < DISTANCE) {
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
		if (this._energy < ENERGY_MIN) {
			set_state_hunger();
		} else if (this._desire > DESIRE_MAX) {
			set_state_mate();
		}
	}

	private void updateHunger(double dt) {
		// 1.
		if (this._hunt_target == null || (this._hunt_target != null
				&& (this._state == State.DEAD || this._pos.distanceTo(this._hunt_target._pos) > this._sight_range))) {
			List<Animal> posiblesObjetivos = _region_mngr.get_animals_in_range(this, a -> a._diet == Diet.HERBIVORE);
			this._hunt_target = this._hunting_strategy.select(this, posiblesObjetivos);
		}

		// 2.
		if (this._hunt_target == null) {
			this.updateNormal(dt);
		} else {
			// 2.1
			this._dest = this._hunt_target.get_position();
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
			if (this._pos.distanceTo(this._hunt_target._pos) < DISTANCE) {
				// 2.6.1
				this._hunt_target._state = State.DEAD;
				// 2.6.2
				this._hunt_target = null;
				// 2.6.3
				this._energy += ENERGY_TO_ADD;
				Utils.constrain_value_in_range(this._energy, MIN, MAX);
			}
		}

		// 3.
		if (this._energy > ENERGY_MIN) {
			if (this._desire < DESIRE_MAX) {
				set_state_normal();
			} else {
				set_state_mate();
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
			if (this._pos.distanceTo(this._mate_target._pos) < DISTANCE) {
				// 2.6.1
				this._desire = 0;
				this._mate_target._desire = 0;
				// 2.6.2
				if (!super.is_pregnent() && Utils._rand.nextDouble() < PROP_NEW_BABY) {
					this._baby = new Wolf(this, _mate_target);
				}
				// 2.6.3
				this._energy -= ENERGY_MINUS;
				this._energy = Utils.constrain_value_in_range(this._energy, MIN, MAX);
				// 2.6.4
				this._mate_target = null;
			}
		}
		// 3.
		if (this._energy < ENERGY_MIN) {
			set_state_hunger();
		} else if (this._desire < DESIRE_MAX) {
			set_state_normal();
		}
	}

}
